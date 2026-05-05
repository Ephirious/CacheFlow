from typing import Optional
from uuid import UUID
from pydantic import BaseModel


class TransferRecord(BaseModel):
    id: UUID
    account_from_id: UUID
    account_to_id: UUID

class TransferUpdate(BaseModel):
    account_from_id: Optional[UUID] = None
    account_to_id: Optional[UUID] = None 
