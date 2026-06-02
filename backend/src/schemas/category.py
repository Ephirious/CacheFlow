from uuid import UUID

from pydantic import BaseModel

from backend.src.models.categories import CategoryType


class CategoryRecord(BaseModel):
    id: UUID
    name: str
    emoji: str
    type: CategoryType
    user_id: UUID = None

class CategoryUpdate(BaseModel):
    name: str = None
    emoji: str = None
    type: CategoryType = None