from decimal import Decimal
from typing import Optional
from uuid import UUID
from pydantic import BaseModel


class AccountRecord(BaseModel):
    id: UUID
    name: str
    color: str
    funds: Decimal


class AccountUpdate(BaseModel):
    name: Optional[str] = None
    color: Optional[str] = None
    funds: Optional[Decimal] = None