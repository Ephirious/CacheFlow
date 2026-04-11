from datetime import datetime
from uuid import UUID

from pydantic import BaseModel, EmailStr, ConfigDict


class UserBase(BaseModel):
    email: EmailStr
    name: str

class UserCreate(UserBase):
    password: str

class UserUpdateBase(BaseModel):
    name: str | None = None
    email: EmailStr | None = None

class UserUpdate(UserUpdateBase):
    password: str | None = None

class UserRead(UserBase):
    id: UUID
    created_at: datetime

    model_config = ConfigDict(from_attributes=True)

class UserCreateInner(UserBase):
    password_hash: str

class UserUpdateInner(UserUpdateBase):
    password_hash: str | None = None
    verified: bool | None = None
