from typing import Annotated

from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from backend.src.database.session import get_session


PostgresSession = Annotated[AsyncSession, Depends(get_session)]