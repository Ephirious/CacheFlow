from __future__ import annotations
from typing import Optional

from sqlalchemy.ext.asyncio import AsyncSession

from backend.src.repositories.email_code import EmailCodeRepository
from backend.src.repositories.user import UserRepository


class UnitOfWork:
    __slots__ = ("_session", "user_repository", "email_code_repository")
    def __init__(self, session: AsyncSession):
        self._session = session
        self.user_repository: Optional[UserRepository] = None
        self.email_code_repository: Optional[EmailCodeRepository] = None

    async def __aenter__(self):
        self.user_repository = UserRepository(self._session)
        self.email_code_repository = EmailCodeRepository(self._session)
        return self

    async def __aexit__(self, exc_type, *args):
        if exc_type:
            await self.rollback()
        else:
            await self.commit()
        await self._session.close()

    async def commit(self):
        await self._session.commit()

    async def rollback(self):
        await self._session.rollback()