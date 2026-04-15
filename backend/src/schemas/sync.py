from datetime import datetime
from decimal import Decimal
from typing import Any, Optional, Union
from uuid import UUID

from pydantic import BaseModel, model_validator

from backend.src.models.sync import Action, TableType



class AccountRecord(BaseModel):
    id: UUID
    name: str
    color: str
    funds: Decimal


class CategoryRecord(BaseModel):
    id: UUID
    name: str


class TransferRecord(BaseModel):
    id: UUID
    account_from_id: UUID
    account_to_id: UUID


class OperationRecord(BaseModel):
    id: UUID
    account_uuid: UUID
    transfer_id: Optional[UUID] = None
    category_id: Optional[UUID] = None
    amount: Decimal
    date: datetime
    notes: str


records = Union[AccountRecord, CategoryRecord, TransferRecord, OperationRecord]


class SyncOperation(BaseModel):
    processing_id: UUID
    created_at: datetime
    action: Action
    table_type: TableType

    field_to_update: Optional[str] = None
    value_to_update: Optional[Any] = None
    record_to_create: Optional[records] = None

    @model_validator(mode='after')
    def check_field_exists(self):
        if self.action == Action.CREATE and not self.record_to_create:
            raise ValueError('"record_to_create" must not be empty')

        if self.action == Action.UPDATE:
            if not self.field_to_update:
                raise ValueError('"field_to_update" must not be empty')

            allowed = {
                TableType.CATEGORIES: {'name'},
                TableType.OPERATIONS: {'account_uuid', 'transfer_id', 'category_id', 'amount', 'date', 'notes'},
                TableType.ACCOUNTS: {'name', 'color', 'funds'},
                TableType.TRANSFER: {'account_from_id', 'account_to_id'}
            }

            allowed_type = allowed[self.table_type]
            if self.field_to_update not in allowed_type:
                raise ValueError('"field_to_update" must be one of {}'.format(allowed_type))
