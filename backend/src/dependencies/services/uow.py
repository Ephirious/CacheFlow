from __future__ import annotations
from typing import Annotated

from fastapi import Depends
from backend.src.dependencies.database.postgres import PostgresSession
from backend.src.repositories.uow import UnitOfWork


async def get_uow(session: PostgresSession) -> UnitOfWork:
    return UnitOfWork(session)

UOWDep = Annotated[UnitOfWork, Depends(get_uow)]