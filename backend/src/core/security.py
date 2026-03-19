import secrets
import string
from typing import Literal

from passlib.context import CryptContext
from passlib.hash import sha256_crypt

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