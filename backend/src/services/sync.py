from datetime import datetime
from typing import Optional
from uuid import UUID

from pydantic import BaseModel
from backend.src.models.sync import Action, TableType
from backend.src.repositories.base import GenericRepository
from backend.src.repositories.uow import UnitOfWork
from backend.src.schemas.account import AccountRecord, AccountUpdate
from backend.src.schemas.category import CategoryRecord, CategoryUpdate
from backend.src.schemas.operation import OperationRecord, OperationUpdate
from backend.src.schemas.result import Result
from backend.src.schemas.sync import RECORD, StateDelete, StateUpdate, SyncOperation, SyncOperationBase, SyncResponse
from backend.src.models import SyncOperation as ModelSyncOperation, Account, Transfer, Operation, Category
from backend.src.schemas.transfer import TransferRecord, TransferUpdate


class SyncService:
    __slots__ = ("uow", "_schemas_map")

    def __init__(self, uow: UnitOfWork):
        self.uow = uow
        self._schemas_map: dict[TableType, tuple[GenericRepository, BaseModel, BaseModel]] = {
            TableType.ACCOUNTS: (self.uow.account_repository, AccountRecord, AccountUpdate),
            TableType.OPERATIONS: (self.uow.operation_repository, OperationRecord, OperationUpdate),
            TableType.CATEGORIES: (self.uow.category_repository, CategoryRecord, CategoryUpdate),
            TableType.TRANSFER: (self.uow.transfer_repository, TransferRecord, TransferUpdate)
        }


    async def _apply_updates(self, id: UUID, updates: dict[str, str], table_type: TableType) -> Optional[RECORD]:
        if not updates:
            return None
        repo, schema_record, schema_upd = self._schemas_map[table_type]
        db_record = await repo.get_by_id(id)
        if not db_record:
            return None
        upd = schema_upd.model_validate(updates)
        updated = await repo.update(db_record, upd)
        return schema_record.model_validate(updated, from_attributes=True)
        

    async def _apply_create(self, row: RECORD, table_type: TableType) -> RECORD:
        repo, schema_record, _ = self._schemas_map[table_type]
        created = await repo.insert(row)
        return schema_record.model_validate(created, from_attributes=True)
    
    async def _apply_delete(self, entity_id: UUID, table_type: TableType):
        repo, _, _ = self._schemas_map[table_type]
        await repo.delete(entity_id=entity_id)


    async def sync(self, sync_ops: list[SyncOperation], last_sync: datetime) -> Result[SyncResponse]:
        ids_ops: dict[str, list[SyncOperation]] = {}
        ids = []
        for op in sync_ops:
            i = op.processing_id
            if i not in ids_ops:
                ids_ops[str(i)] = [op]
                ids.append(i)
            else:
                if ids_ops[str(i)][0].action == Action.DELETE:
                    continue
                ids_ops[str(i)].append(op)
            
            if op.action == Action.DELETE:
                ids_ops[str(i)] = ids_ops[-1:]

        history = await self.uow.sync_repository.get_by_processing_ids(sync_ops, last_sync)

        resp = SyncResponse(
            last_sync_date = datetime.now(),
            accepted_ids=[],
            delete_operations=[],
            update_state=[]
            )

        cur_updates = {}
        cur_record = None
        for processing_id, ops in ids_ops.items():
            hist: list[ModelSyncOperation] = []
            table_type = ops[0].table_type
            for h in history:
                if h.processing_id == processing_id:
                    if h.action == Action.DELETE:
                        resp.delete_operations.append(StateDelete(table_type = h.table_type, id = processing_id))
                        break
                    hist.append(h)
            else:
                for op in ops:
                    to_create_op = None
                    match op.action:
                        case Action.CREATE:
                            try:
                                rec = await self._apply_create(op.record_to_create, op.table_type)
                                if not rec:
                                    break
                                cur_record = rec
                                to_create_op = SyncOperationBase.model_validate(op, from_attributes=True)
                            except:
                                break

                        case Action.UPDATE:
                            for other in filter(lambda x: x.field_to_update == op.field_to_update, hist):
                                if other.created_at >= op.created_at:
                                    break
                            else:
                                cur_updates[op.field_to_update] = op.value_to_update
                                to_create_op = SyncOperationBase.model_validate(op, from_attributes=True)

                        case Action.DELETE:
                            await self._apply_delete(op.processing_id, op.table_type)
                            cur_record = None
                            cur_updates = {}
                            to_create_op = SyncOperationBase.model_validate(op, from_attributes=True)
                            break
                    
                    if to_create_op:
                        await self.uow.sync_repository.insert(to_create_op)

                cur_record = await self._apply_updates(processing_id, cur_updates, table_type) or cur_record
                resp.accepted_ids.append(processing_id)
                resp.update_state.append(StateUpdate(table_type = table_type, record = cur_record, updated_at = datetime.now()))

                cur_updates = {}
                cur_record = None

        resp.last_sync_date = datetime.now()
        return Result.ok(resp)