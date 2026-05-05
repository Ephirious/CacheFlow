from __future__ import annotations
from typing import Optional

from sqlalchemy.ext.asyncio import AsyncSession

from backend.src.repositories.account import AccountRepository
from backend.src.repositories.category import CategoryRepository
from backend.src.repositories.email_code import EmailCodeRepository
from backend.src.repositories.operation import OperationRepository
from backend.src.repositories.sync import SyncOperationRepository
from backend.src.repositories.transfer import TransferRepository
from backend.src.repositories.user import UserRepository


class UnitOfWork:
    __slots__ = (
        "_session", "user_repository", "email_code_repository", "sync_repository", 
        "account_repository", "operation_repository", "category_repository", "transfer_repository"
                 )
    def __init__(self, session: AsyncSession):
        self._session = session
        self.user_repository: Optional[UserRepository] = None
        self.email_code_repository: Optional[EmailCodeRepository] = None
        self.sync_repository: Optional[SyncOperationRepository] = None
        self.account_repository: Optional[AccountRepository] = None
        self.category_repository: Optional[CategoryRepository] = None
        self.operation_repository: Optional[OperationRepository] = None
        self.transfer_repository: Optional[TransferRepository] = None

    async def __aenter__(self):
        self.user_repository = UserRepository(self._session)
        self.email_code_repository = EmailCodeRepository(self._session)
        self.sync_repository = SyncOperationRepository(self._session)
        self.account_repository = AccountRepository(self._session)
        self.category_repository = CategoryRepository(self._session)
        self.operation_repository = OperationRepository(self._session)
        self.transfer_repository = TransferRepository(self._session)
        
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