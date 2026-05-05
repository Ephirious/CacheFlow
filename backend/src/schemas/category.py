from uuid import UUID

from pydantic import BaseModel

from backend.src.models.categories import CategoryType


class CategoryRecord(BaseModel):
    id: UUID
    name: str
    emoji: str
    type: CategoryType

class CategoryUpdate(BaseModel):
    name: str
    emoji: str
    type: CategoryType