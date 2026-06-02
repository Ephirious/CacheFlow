from datetime import datetime
from decimal import Decimal
from typing import Optional
from uuid import UUID

from pydantic import BaseModel


class OperationRecord(BaseModel):
    id: UUID
    account_uuid: UUID
    transfer_id: Optional[UUID] = None
    category_uuid: Optional[UUID] = None
    amount: Decimal
    date: datetime
    notes: str


class OperationUpdate(BaseModel):
    account_uuid: Optional[UUID] = None
    transfer_id: Optional[UUID] = None
    category_uuid: Optional[UUID] = None
    amount: Optional[Decimal] = None
    date: Optional[datetime] = None
    notes: Optional[str] = None