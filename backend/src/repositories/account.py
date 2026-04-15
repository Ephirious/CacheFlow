from sqlalchemy.ext.asyncio import AsyncSession

from backend.src.models.accounts import Account
from backend.src.repositories.base import GenericRepository
from backend.src.schemas.account import AccountRecord, AccountUpdate

class AccountRepository(GenericRepository[Account, AccountRecord, AccountUpdate]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, Account)