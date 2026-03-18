from uuid import UUID

from sqlalchemy import ForeignKey, String
from sqlalchemy.orm import Mapped, mapped_column

from backend.src.models.base_class import Base


class Category(Base):
    __tablename__ = 'categories'

    user_id: Mapped[UUID] = mapped_column(ForeignKey('users.id'))
    name: Mapped[str] = mapped_column(String(100))
