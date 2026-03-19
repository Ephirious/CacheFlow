import enum
from datetime import datetime, timezone, timedelta
from uuid import UUID

from sqlalchemy import String, Boolean, ForeignKey, Enum, DateTime
from sqlalchemy.orm import Mapped, mapped_column

from backend.src.core.config import settings
from backend.src.models.base_class import Base

class User(Base):
    email: Mapped[str] = mapped_column(String(255), unique=True)
    password_hash: Mapped[str] = mapped_column(String(255))
    name: Mapped[str] = mapped_column(String(100))
    verified: Mapped[bool] = mapped_column(Boolean, default=False)


class EmailCodeAction(enum.Enum):
    change_password = "CHANGE_PASSWORD"
    change_email = "CHANGE_EMAIL"
    register = "REGISTER"


class EmailCode(Base):
    __tablename__ = 'email_codes'
    code_hash: Mapped[str] = mapped_column(String(255), index = True)
    action: Mapped[EmailCodeAction] = mapped_column(Enum(EmailCodeAction, native_enum=True))
    user_id: Mapped[UUID] = mapped_column(ForeignKey('users.id'))
    expires_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc) + timedelta(minutes=settings.email_code_expire_min)
    )

    @property
    def is_expired(self) -> bool:
        return datetime.now(timezone.utc) > self.expires_at
