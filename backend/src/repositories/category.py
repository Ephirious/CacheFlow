from backend.src.models.categories import Category
from backend.src.repositories.base import GenericRepository
from backend.src.schemas.category import CategoryRecord, CategoryUpdate
from sqlalchemy.ext.asyncio import AsyncSession


class CategoryRepository(GenericRepository[Category, CategoryRecord, CategoryUpdate]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Category)