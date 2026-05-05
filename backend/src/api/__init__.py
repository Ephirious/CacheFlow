from backend.src.api.auth import router as auth_router
from backend.src.api.sync import router as sync_router

__all__ = [
    "auth_router",
    "sync_router"
]