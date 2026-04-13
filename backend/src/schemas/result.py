from dataclasses import dataclass
from enum import Enum
from typing import TypeVar, Optional, Generic

T = TypeVar("T")

class ErrorCode(str, Enum):
    EMAIL_EXISTS = "email_exists"
    NO_ALIVE_CODES = "no_alive_codes"
    INVALID_CODE = "invalid_code"
    INVALID_CREDENTIALS = "invalid_credentials"
    ALREADY_VERIFIED = "already_verified"
    FORBIDDEN = "forbidden"

@dataclass(frozen=True)
class Error:
    message: str
    error_code: ErrorCode

@dataclass(frozen=True)
class Result(Generic[T]):
    value: Optional[T] = None
    error: Optional[Error] = None

    @property
    def is_success(self) -> bool:
        return self.error is None

    @classmethod
    def ok(cls, result: T) -> "Result[T]":
        return cls(value = result)

    @classmethod
    def err(cls, message: str, error_code: ErrorCode) -> "Result[T]":
        error = Error(message = message, error_code = error_code)
        return cls(error = error)
