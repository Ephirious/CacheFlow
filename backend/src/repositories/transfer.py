from sqlalchemy.ext.asyncio import AsyncSession

from backend.src.models.operations import Transfer
from backend.src.repositories.base import GenericRepository
from backend.src.schemas.transfer import TransferRecord, TransferUpdate

class TransferRepository(GenericRepository[Transfer, TransferRecord, TransferUpdate]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Transfer)