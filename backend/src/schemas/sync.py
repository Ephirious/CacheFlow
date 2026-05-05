from datetime import datetime
from typing import Any, Optional, Union
from uuid import UUID

from pydantic import BaseModel, model_validator

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


class SyncOperation(SyncOperationBase):
    record_to_create: Optional[RECORD_CREATE] = None

    @model_validator(mode='after')
    def check_field_exists(self):
        if self.action == Action.CREATE and not self.record_to_create:
            raise ValueError('"record_to_create" must not be empty')

        if self.action == Action.UPDATE:
            if not self.field_to_update:
                raise ValueError('"field_to_update" must not be empty')

            allowed = {
                TableType.CATEGORIES: {'name', 'emoji'},
                TableType.OPERATIONS: {'account_uuid', 'transfer_id', 'category_uuid', 'amount', 'date', 'notes'},
                TableType.ACCOUNTS: {'name', 'color'},
                TableType.TRANSFER: {'account_from_id', 'account_to_id'}
            }

            allowed_type = allowed[self.table_type]
            if self.field_to_update not in allowed_type:
                raise ValueError('"field_to_update" must be one of {}'.format(allowed_type))


class SyncRequest(BaseModel):
    last_sync_date: datetime
    operations: list[SyncOperation]


class StateUpdate(BaseModel):
    table_type: TableType
    record: RECORD_OUT
    updated_at: datetime

class StateDelete(BaseModel):
    table_type: TableType
    id: UUID

class SyncResponse(BaseModel):
    last_sync_date: datetime
    accepted_ids: list[UUID]
    delete_operations: list[StateDelete]
    update_state: list[StateUpdate]
    