from uuid import UUID

from pydantic import BaseModel


class CategoryRecord(BaseModel):
    id: UUID
    name: str

class CategoryUpdate(BaseModel):
    name: str