from datetime import datetime
from typing import Optional, Sequence
from uuid import UUID
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from backend.src.models.sync import SyncOperation
from backend.src.repositories.base import GenericRepository
from backend.src.schemas.sync import SyncOperationBase


class SyncOperationRepository(GenericRepository[SyncOperation, SyncOperationBase, SyncOperationBase]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, SyncOperation)

    async def get_by_processing_ids(self, ids: list[UUID], last_sync_date: Optional[datetime], user_id: UUID) -> Sequence[SyncOperation]:
        stmt = select(SyncOperation).where(
            SyncOperation.processing_id.in_(ids),
            SyncOperation.updated_at >= last_sync_date,
            SyncOperation.user_id == user_id
        ).order_by(SyncOperation.processing_id, SyncOperation.created_at)
        res = await self._session.execute(stmt)
        return res.scalars().all()
    
    async def get_by_date(self, sync_date: datetime, user_id: UUID) -> Sequence[SyncOperation]:
        stmt = select(SyncOperation).where(
            SyncOperation.updated_at >= sync_date,
            SyncOperation.user_id == user_id
        ).order_by(SyncOperation.created_at)
        res = await self._session.execute(stmt)
        return res.scalars().all()
    