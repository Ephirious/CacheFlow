import enum
from typing import Any, Optional
from uuid import UUID
from sqlalchemy.dialects.postgresql import JSONB

from sqlalchemy import Index, String, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column

from backend.src.models.base_class import Base


class Action(enum.Enum):
    CREATE = 'create'
    UPDATE = 'update'
    DELETE = 'delete'

class TableType(enum.Enum):
    CATEGORIES = 'categories'
    ACCOUNTS = 'accounts'
    OPERATIONS = 'operations'
    TRANSFER = 'transfer'

class SyncOperation(Base):
    __tablename__ = 'sync_operations'

    processing_id: Mapped[UUID]
    action: Mapped[Action]
    table_type: Mapped[TableType]
    field_to_update: Mapped[Optional[str]] = mapped_column(String(100), nullable=True)
    value_to_update: Mapped[Optional[Any]] = mapped_column(JSONB, nullable=True)
    user_id: Mapped[UUID] = mapped_column(ForeignKey('users.id'))

    __table_args__ = (
        Index("idx_sync_proc_id_date", "processing_id", "created_at"),
        Index("idx_sync_created_at", "created_at"),
        Index("idx_processing_id", "processing_id")
    )