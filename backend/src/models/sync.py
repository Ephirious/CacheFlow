import enum
from uuid import UUID

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

class SyncOperation(Base):
    __tablename__ = 'sync_operations'

    processing_id: Mapped[UUID]
    action: Mapped[Action]
    table_type: Mapped[TableType]