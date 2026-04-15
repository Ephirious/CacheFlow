import decimal
from datetime import datetime
from uuid import UUID

from sqlalchemy import ForeignKey, NUMERIC, DateTime, String
from sqlalchemy.orm import Mapped, mapped_column

from backend.src.models.base_class import Base


class Transfer(Base):
    account_from_id: Mapped[UUID] = mapped_column(ForeignKey('accounts.id'))
    account_to_id: Mapped[UUID] = mapped_column(ForeignKey('accounts.id'))

class Operation(Base):
    account_uuid: Mapped[UUID] = mapped_column(ForeignKey('accounts.id'))
    transfer_id: Mapped[UUID | None] = mapped_column(ForeignKey('transfers.id'), nullable = True)
    category_id: Mapped[UUID | None] = mapped_column(ForeignKey("categories.id"), nullable = True)
    amount: Mapped[decimal.Decimal] = mapped_column(NUMERIC(10, 2))
    date: Mapped[datetime] = mapped_column(DateTime(timezone=True))
    notes: Mapped[str] = mapped_column(String(1024))