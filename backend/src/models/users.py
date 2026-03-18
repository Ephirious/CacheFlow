from sqlalchemy import String, Boolean
from sqlalchemy.orm import Mapped, mapped_column

from backend.src.models.base_class import Base

class User(Base):
    email: Mapped[str] = mapped_column(String(255), unique=True)
    password_hash: Mapped[str] = mapped_column(String(255))
    name: Mapped[str] = mapped_column(String(100))
    verified: Mapped[bool] = mapped_column(Boolean, default=False)
