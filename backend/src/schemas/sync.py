from datetime import datetime
from typing import Any, Optional, Union
from uuid import UUID

from pydantic import BaseModel, model_validator, Field

from backend.src.models.sync import Action, TableType
from backend.src.schemas.account import AccountCreateRecord, AccountOutRecord
from backend.src.schemas.category import CategoryRecord
from backend.src.schemas.operation import OperationRecord
from backend.src.schemas.transfer import TransferRecord


RECORD_CREATE = Union[AccountCreateRecord, CategoryRecord, TransferRecord, OperationRecord]
RECORD_OUT = Union[AccountOutRecord, CategoryRecord, TransferRecord, OperationRecord]

class SyncOperationBase(BaseModel):
    id: UUID
    processing_id: UUID
    created_at: datetime
    action: Action
    table_type: TableType
    field_to_update: Optional[str] = None
    value_to_update: Optional[Any] = None

class SyncOperationDb(BaseModel):
    id: UUID
    processing_id: UUID
    created_at: datetime
    action: Action
    table_type: TableType
    user_id: UUID
    field_to_update: Optional[str] = None
    value_to_update: Optional[Any] = None


class SyncOperation(SyncOperationBase):
    record_to_create: Optional[Any]

    @model_validator(mode='before')
    @classmethod
    def validate_record_by_table_type(cls, data: Any) -> Any:
        if isinstance(data, dict):
            action = data.get("action")
            table_type_raw = data.get("table_type")
            record = data.get("record_to_create")
            table_type = None
            if table_type_raw:
                try:
                    table_type = TableType(table_type_raw)
                except ValueError:
                    pass

            if action in ("create", Action.CREATE):
                if not record:
                    raise ValueError('"record_to_create" must not be empty')
                
                mapping = {
                    TableType.ACCOUNTS: AccountCreateRecord,
                    TableType.CATEGORIES: CategoryRecord,
                    TableType.TRANSFER: TransferRecord,
                    TableType.OPERATIONS: OperationRecord,
                }
                
                target_model = mapping.get(table_type)
                if target_model and isinstance(record, dict):
                    data["record_to_create"] = target_model.model_validate(record)
            else:
                data["record_to_create"] = None
        return data

    @model_validator(mode='after')
    def check_field_exists(self) -> "SyncOperation":

        if self.action == Action.UPDATE:
            if not self.field_to_update:
                raise ValueError('"field_to_update" must not be empty')

            allowed = {
                TableType.CATEGORIES: {'name', 'emoji'},
                TableType.OPERATIONS: {'account_uuid', 'transfer_id', 'category_uuid', 'amount', 'date', 'notes'},
                TableType.ACCOUNTS: {'name', 'color'},
                TableType.TRANSFER: {'account_from_id', 'account_to_id'}
            }

            allowed_type = allowed.get(self.table_type)
            if not allowed_type or self.field_to_update not in allowed_type:
                raise ValueError(f'"field_to_update" must be one of {allowed_type}')

        return self


class SyncRequest(BaseModel):
    last_sync_date: datetime
    operations: list[SyncOperation]


class StateUpdate(BaseModel):
    table_type: TableType
    record: Any
    updated_at: datetime

    @model_validator(mode='before')
    @classmethod
    def validate_record_by_table_type(cls, data: Any) -> Any:
        if isinstance(data, dict):
            table_type_raw = data.get("table_type")
            record = data.get("record")

            table_type = None
            if table_type_raw:
                try:
                    table_type = TableType(table_type_raw)
                except ValueError:
                    pass

            if True:
                if not record:
                    raise ValueError('"record_to_create" must not be empty')

                mapping = {
                    TableType.ACCOUNTS: AccountOutRecord,
                    TableType.CATEGORIES: CategoryRecord,
                    TableType.TRANSFER: TransferRecord,
                    TableType.OPERATIONS: OperationRecord,
                }


                target_model = mapping.get(table_type)
                if target_model and isinstance(record, dict):
                    data["record"] = target_model.model_validate(record)
        return data

class StateDelete(BaseModel):
    table_type: TableType
    id: UUID

class SyncResponse(BaseModel):
    last_sync_date: datetime
    accepted_ids: list[UUID]
    delete_operations: list[StateDelete]
    update_state: list[StateUpdate]