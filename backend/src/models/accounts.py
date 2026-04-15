import decimal

from sqlalchemy import UUID, ForeignKey, String, NUMERIC, Boolean
from sqlalchemy.orm import Mapped, mapped_column

from backend.src.models.base_class import Base


class Account(Base):
    user_id: Mapped[UUID] = mapped_column(ForeignKey('users.id'))
    name: Mapped[str] = mapped_column(String(100))
    color: Mapped[str]
    funds: Mapped[decimal.Decimal] = mapped_column(NUMERIC(10, 2))
    is_deleted: Mapped[bool] = mapped_column(Boolean, default = False)