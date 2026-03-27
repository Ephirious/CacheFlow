from typing import Annotated
from fastapi import Depends

from backend.src.repositories.uow import UnitOfWork
from backend.src.services.auth import AuthService
from backend.src.dependencies.services.uow import get_uow

async def get_auth_service(uow: UnitOfWork = Depends(get_uow)) -> AuthService:
    return AuthService(uow)

AuthServiceDep = Annotated[AuthService, Depends(get_auth_service)]