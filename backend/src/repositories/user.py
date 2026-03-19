from typing import Optional

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from backend.src.models import User
from backend.src.repositories.base import GenericRepository
from backend.src.schemas.user import UserCreateInner, UserUpdateInner


class UserRepository(GenericRepository[User, UserCreateInner, UserUpdateInner]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, User)

    async def get_by_email(self, email: str) -> Optional[User]:
        stmt = select(User).where(User.email == email)
        res = await self._session.execute(stmt)
        return res.scalar_one_or_none()