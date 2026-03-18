from datetime import datetime, timezone
from uuid import UUID as PyUUID
from sqlalchemy import UUID, DateTime
from sqlalchemy.ext.asyncio import AsyncAttrs
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, declared_attr
from uuid_extensions import uuid7


class Base(AsyncAttrs, DeclarativeBase):
    __abstract__ = True
    id: Mapped[PyUUID] = mapped_column(UUID(as_uuid=True), unique=True, default = uuid7, primary_key=True)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default = lambda: datetime.now(timezone.utc),
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc),
        onupdate=lambda: datetime.now(timezone.utc),
    )

    @declared_attr.directive
    def __tablename__(cls) -> str:

        return cls.__name__.lower() + 's'
