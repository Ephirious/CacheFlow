from sqlalchemy.ext.asyncio import AsyncSession
from backend.src.models.operations import Operation
from backend.src.repositories.base import GenericRepository
from backend.src.schemas.operation import OperationRecord, OperationUpdate

class OperationRepository(GenericRepository[Operation, OperationRecord, OperationUpdate]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Operation)