import datetime
import secrets
import string
from typing import Literal
from uuid import UUID

from fastapi import Depends
from fastapi.security import OAuth2PasswordBearer
from passlib.context import CryptContext
from passlib.hash import sha256_crypt
from jose import jwt, JWTError

from backend.src.core.config import settings
from backend.src.models import User

cryptoctx = CryptContext(schemes="bcrypt")

class Password:
    @staticmethod
    def encrypt(password: str, ctx: Literal["bcrypt", "sha256"] = "bcrypt") -> str:
        if ctx == "bcrypt":
            return cryptoctx.encrypt(password)
        return sha256_crypt.hash(password)

    @staticmethod
    def verify(password: str, hashed_password: str, ctx: Literal["bcrypt", "sha256"] = "bcrypt") -> bool:
        if ctx == "bcrypt":
            return cryptoctx.verify(password, hashed_password)
        return sha256_crypt.verify(password, hashed_password)

    @staticmethod
    def generate_otp(length: int = 6) -> str:
        return "".join(secrets.choice(string.digits) for _ in range(length))

class Email:
    @staticmethod
    def mask(email: str) -> str:
        try:
            user_part, domain = email.split("@")
            if len(user_part) <= 2:
                return f"{user_part[0]}***@{domain}"
            return f"{user_part[:2]}***{user_part[-1]}@{domain}"
        except Exception:
            return "***@***"

oauth2_scheme_user = OAuth2PasswordBearer(tokenUrl="/auth/login", auto_error=False)

class Token:
    @staticmethod
    def create_token(user_id: UUID):
        payload = {}
        payload["id"] = str(user_id)
        payload_refresh = payload.copy()
        payload_access = payload.copy()
        payload_access["type"] = "access"
        payload_access["exp"] = datetime.datetime.now() + datetime.timedelta(minutes=settings.access_expire_min)

        payload_refresh["type"] = "refresh"
        payload_refresh["exp"] = datetime.datetime.now() + datetime.timedelta(minutes=settings.refresh_expire_min)

        access = jwt.encode(payload_access, key=settings.token_key, algorithm="HS256")
        refresh = jwt.encode(payload_refresh, key=settings.token_key, algorithm="HS256")
        return {
            "access_token": access,
            "refresh_token": refresh
        }