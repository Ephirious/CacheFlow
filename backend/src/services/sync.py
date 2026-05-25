import traceback
from datetime import datetime, timezone
from typing import Optional
from uuid import UUID

from pydantic import BaseModel
from backend.src.models.sync import Action, TableType
from backend.src.repositories.base import GenericRepository
from backend.src.repositories.uow import UnitOfWork
from backend.src.schemas.account import AccountCreateRecord, AccountUpdate, AccountOutRecord
from backend.src.schemas.category import CategoryRecord, CategoryUpdate
from backend.src.schemas.operation import OperationRecord, OperationUpdate
from backend.src.schemas.result import ErrorCode, Result
from backend.src.schemas.sync import RECORD_CREATE, RECORD_OUT, StateDelete, StateUpdate, SyncOperation, SyncResponse, SyncOperationBase, SyncOperationDb
from backend.src.models import SyncOperation as ModelSyncOperation
from backend.src.schemas.transfer import TransferRecord, TransferUpdate


class SyncService:
    __slots__ = ("uow", "_schemas_map")

    def __init__(self, uow: UnitOfWork):
        self.uow = uow
        self._schemas_map: dict[TableType, tuple[GenericRepository, BaseModel, BaseModel, BaseModel]] = {
            TableType.ACCOUNTS: (self.uow.account_repository, AccountCreateRecord, AccountUpdate, AccountOutRecord),
            TableType.OPERATIONS: (self.uow.operation_repository, OperationRecord, OperationUpdate, OperationRecord),
            TableType.CATEGORIES: (self.uow.category_repository, CategoryRecord, CategoryUpdate, CategoryRecord),
            TableType.TRANSFER: (self.uow.transfer_repository, TransferRecord, TransferUpdate, TransferRecord)
        }


    async def _apply_updates(self, id: UUID, updates: dict[str, str], table_type: TableType) -> Optional[RECORD_OUT]:
        if not updates:
            return None
        repo, _, schema_upd, schema_record = self._schemas_map[table_type]
        db_record = await repo.get_by_id(id)
        if not db_record:
            return None
        upd = schema_upd.model_validate(updates)
        updated = await repo.update(db_record, upd)
        return schema_record.model_validate(updated, from_attributes=True)
        

    async def _apply_create(self, row: RECORD_CREATE, table_type: TableType, user_id) -> RECORD_OUT:
        repo, schema_record, _, _ = self._schemas_map[table_type]
        print(row)
        row["user_id"] = user_id
        row = schema_record.model_validate(row)
        created = await repo.insert(row)
        return schema_record.model_validate(created, from_attributes=True)
    
    async def _apply_delete(self, entity_id: UUID, table_type: TableType):
        repo, _, _ = self._schemas_map[table_type]
        await repo.delete(entity_id=entity_id)


    async def sync(self, sync_ops: list[SyncOperation], last_sync: datetime, user_id) -> Result[SyncResponse]:
        grouped_ops: dict[UUID, list[SyncOperation]] = {}
        for op in sync_ops:
            grouped_ops.setdefault(op.processing_id, []).append(op)

        history = await self.uow.sync_repository.get_by_processing_ids(
            [op.processing_id for op in sync_ops], last_sync
        )
        
        history_map: dict[UUID, list[ModelSyncOperation]] = {}
        for h in history:
            history_map.setdefault(h.processing_id, []).append(h)

        resp = SyncResponse(
            last_sync_date=datetime.now(timezone.utc),
            accepted_ids=[],
            delete_operations=[],
            update_state=[]
        )

        affected_accounts = set()

        try:
            for p_id, ops in grouped_ops.items():
                table_type = ops[0].table_type
                h_ops = history_map.get(p_id, [])

                if any(h.action == Action.DELETE for h in h_ops):
                    resp.delete_operations.append(StateDelete(table_type=table_type, id=p_id))
                    resp.accepted_ids.append(p_id)
                    continue

                cur_record = None
                pending_updates = {}
                should_apply = False

                for op in sorted(ops, key=lambda x: x.created_at):
                    if op.action == Action.CREATE:
                        db_exists = await self._schemas_map[table_type][0].get_by_id(p_id)
                        if not db_exists:
                            cur_record = await self._apply_create(op.record_to_create, table_type, user_id)
                            should_apply = True
                            if op.table_type == TableType.OPERATIONS:
                                affected_accounts.add(cur_record.account_uuid)
                        else:
                            cur_record = db_exists

                    elif op.action == Action.UPDATE:
                        if op.table_type == TableType.OPERATIONS and op.field_to_update == 'account_uuid':
                            oper_rec = await self.uow.operation_repository.get_by_id(p_id)
                            if oper_rec:
                                affected_accounts.add(oper_rec.account_uuid)
                        is_stale = any(
                            h.field_to_update == op.field_to_update and h.created_at >= op.created_at 
                            for h in h_ops
                        )
                        if not is_stale:
                            pending_updates[op.field_to_update] = op.value_to_update
                            should_apply = True

                    elif op.action == Action.DELETE:
                        if op.table_type == TableType.OPERATIONS:
                            rec = await self.uow.operation_repository.get_by_id(p_id)
                            affected_accounts.add(rec.account_uuid)
                        await self._apply_delete(p_id, table_type)
                        
                        resp.delete_operations.append(StateDelete(table_type=table_type, id=p_id))
                        should_apply = True
                        cur_record = None
                        pending_updates = {}
                        break

                    if should_apply:
                        to_ins = SyncOperationDb.model_validate(op, from_attributes=True)
                        await self.uow.sync_repository.insert(to_ins)

                if pending_updates:
                    cur_record = await self._apply_updates(p_id, pending_updates, table_type)

                if cur_record or should_apply:
                    resp.accepted_ids.append(p_id)
                    if cur_record:
                        if op.table_type == TableType.OPERATIONS:
                            affected_accounts.add(cur_record.account_uuid)
                        resp.update_state.append(StateUpdate(
                            table_type=table_type, 
                            record=cur_record, 
                            updated_at=datetime.now(timezone.utc)
                        ))

            other_ops = await self.uow.sync_repository.get_by_date(last_sync)
            added_ids = set(resp.accepted_ids)
            for s_op in other_ops:
                if s_op.processing_id in added_ids:
                    continue

                if s_op.action == Action.DELETE:
                    resp.delete_operations.append(StateDelete(
                        table_type=s_op.table_type, 
                        id=s_op.processing_id
                    ))
                else:
                    repo, schema_record, _ = self._schemas_map[s_op.table_type]
                    db_obj = await repo.get_by_id(s_op.processing_id)
                    if db_obj:
                        resp.update_state.append(StateUpdate(
                            table_type=s_op.table_type,
                            record=schema_record.model_validate(db_obj, from_attributes=True).model_dump(),
                            updated_at=db_obj.updated_at
                        ))
                added_ids.add(s_op.processing_id)
            resp.last_sync_date = datetime.now(timezone.utc)
            await self.uow._session.flush()

            affected_accounts = {a for a in affected_accounts if a not in added_ids}

            if affected_accounts:
                affected_acc_records = await self.uow.account_repository.get_by_in(list(affected_accounts))
                for acc in affected_acc_records:
                    resp.update_state.append(StateUpdate(
                        table_type = TableType.ACCOUNTS,
                        record = AccountOutRecord.model_validate(acc).model_dump(),
                        updated_at = acc.updated_at
                    ))
                    
            await self.uow.commit()
            return Result.ok(resp)

        except Exception as e:
            await self.uow.rollback()
            traceback.print_exc()
            return Result.err(
                message = str(e), error_code = ErrorCode.INTERNAL
            )