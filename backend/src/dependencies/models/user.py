from __future__ import annotations
from typing import Annotated
from fastapi import Depends
from jose import JWTError, jwt

from backend.src.core.config import settings
from backend.src.core.security import oauth2_scheme_user
from backend.src.dependencies.services.uow import UOWDep
from backend.src.models import User
from backend.src.schemas.exception import raise_exception

async def get_by_token(uow: UOWDep, token = Depends(oauth2_scheme_user)) -> User:
    try:
        if not token:
            raise JWTError
        payload = jwt.decode(token, key=settings.token_key, algorithms=["HS256"])
        public_id = payload["id"]
        async with uow:
            user = await uow.user_repository.get_by_id(public_id)
        if not user:
            raise_exception(
                err_type="unauthorized",
                msg="User not found",
                loc=["token"]
            )
        return user
    except JWTError:
        raise_exception(
            err_type = "unauthorized",
            msg = "Invalid token",
            loc = ["token"]
        )

CurrentUser = Annotated[User, Depends(get_by_token)]