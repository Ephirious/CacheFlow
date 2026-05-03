from uuid import UUID
from sqlalchemy import update
from sqlalchemy.ext.asyncio import AsyncSession

from backend.src.models.accounts import Account
from backend.src.repositories.base import GenericRepository
from backend.src.schemas.account import AccountRecord, AccountUpdate

class AccountRepository(GenericRepository[Account, AccountRecord, AccountUpdate]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Account)

    async def delete(self, entity_id: UUID):
        stmt = update(Account).where(Account.id == entity_id).values(is_deleted = True)
        await self._session.execute(stmt)