from uuid import UUID

from pydantic import BaseModel


class CategoryRecord(BaseModel):
    id: UUID
    name: str
    emoji: str

class CategoryUpdate(BaseModel):
    name: str
    emoji: str