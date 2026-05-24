import enum
from uuid import UUID

from sqlalchemy import ForeignKey, String, Boolean
from sqlalchemy.orm import Mapped, mapped_column

from backend.src.models.base_class import Base

class CategoryType(enum.Enum):
    INCOME = 'income'
    OUTCOME = 'outcome'

class Category(Base):
    __tablename__ = 'categories'

    user_id: Mapped[UUID] = mapped_column(ForeignKey('users.id'))
    name: Mapped[str] = mapped_column(String(100))
    emoji: Mapped[str] = mapped_column(String(20))
    type: Mapped[CategoryType]
    is_deleted: Mapped[bool] = mapped_column(Boolean, default = False)