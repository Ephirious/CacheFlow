from datetime import datetime
from uuid import UUID

from pydantic import BaseModel

from backend.src.models.users import EmailCodeAction


class EmailCodeCreateInner(BaseModel):
    code_hash: str
    user_id: UUID
    action: EmailCodeAction

class EmailCodeUpdateInner(BaseModel):
    code_hash: str | None = None
    expires_at: datetime | None = None