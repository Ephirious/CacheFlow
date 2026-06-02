from decimal import Decimal
from typing import Optional
from uuid import UUID
from pydantic import BaseModel


class AccountCreateRecord(BaseModel):
    id: UUID
    name: str
    color: str
    user_id: UUID = None

class AccountOutRecord(AccountCreateRecord):
    funds: Decimal

class AccountUpdate(BaseModel):
    name: Optional[str] = None
    color: Optional[str] = None
    funds: Optional[Decimal] = None