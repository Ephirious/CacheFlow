from typing import Annotated
from fastapi import Depends

from backend.src.repositories.uow import UnitOfWork
from backend.src.dependencies.services.uow import get_uow
from backend.src.services.sync import SyncService

async def get_sync_service(uow: UnitOfWork = Depends(get_uow)) -> SyncService:
    return SyncService(uow)

SyncServiceDep = Annotated[SyncService, Depends(get_sync_service)]