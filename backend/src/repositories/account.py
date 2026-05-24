from typing import Sequence
from uuid import UUID
from sqlalchemy import select, update
from sqlalchemy.ext.asyncio import AsyncSession

from backend.src.models.accounts import Account
from backend.src.repositories.base import GenericRepository
from backend.src.schemas.account import AccountCreateRecord, AccountUpdate

class AccountRepository(GenericRepository[Account, AccountCreateRecord, AccountUpdate]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Account)

    async def delete(self, entity_id: UUID):
        stmt = update(Account).where(Account.id == entity_id).values(is_deleted = True)
        await self._session.execute(stmt)

    async def get_by_in(self, entity_ids: Sequence[UUID]) -> Sequence[Account]:
        stmt = select(Account).where(Account.id.in_(entity_ids))
        res = await self._session.execute(stmt)
        return res.scalars()