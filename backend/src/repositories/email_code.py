from typing import Optional
from uuid import UUID

from sqlalchemy import select, delete
from sqlalchemy.ext.asyncio import AsyncSession

from backend.src.models.users import EmailCode, EmailCodeAction
from backend.src.repositories.base import GenericRepository
from backend.src.schemas.email_code import EmailCodeUpdateInner, EmailCodeCreateInner


class EmailCodeRepository(GenericRepository[EmailCode, EmailCodeCreateInner, EmailCodeUpdateInner]):
    def __init__(self, session: AsyncSession):
        super().__init__(session, EmailCode)

    async def get_by_user_id(self, user_id: UUID, action: EmailCodeAction) -> Optional[EmailCode]:
        stmt = select(EmailCode).where(EmailCode.user_id == user_id, EmailCode.action == action)
        res = await self._session.execute(stmt)
        return res.scalar_one_or_none()

    async def delete_by_user_id(self, user_id: UUID, action: EmailCodeAction):
        stmt = delete(EmailCode).where(EmailCode.user_id == user_id, EmailCode.action == action)
        await self._session.execute(stmt)