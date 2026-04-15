import enum
from uuid import UUID

from sqlalchemy import Index
from sqlalchemy.orm import Mapped

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

    __table_args__ = (
    Index("idx_sync_proc_id_date", "processing_id", "created_at"),
    Index("idx_sync_created_at", "created_at"),
    Index("idx_processing_id", "processing_id")
    )