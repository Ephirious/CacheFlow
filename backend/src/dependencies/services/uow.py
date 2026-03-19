from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from backend.src.database.session import get_session
from backend.src.repositories.uow import UnitOfWork


async def get_uow(session: AsyncSession = Depends(get_session)) -> UnitOfWork:
    return UnitOfWork(session)