from uuid import UUID
from pydantic import BaseModel, EmailStr


class UserLogin(BaseModel):
    email: EmailStr
    password: str

class TokenModel(BaseModel):
    access_token: str
    refresh_token: str

class VerifyEmailRequest(BaseModel):
    user_id: UUID
    code: str

class ResendCode(BaseModel):
    user_id: UUID