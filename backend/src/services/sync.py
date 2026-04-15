from datetime import datetime
from backend.src.repositories.uow import UnitOfWork
from backend.src.schemas.sync import SyncOperation


class SyncService:
    __slots__ = ("uow",)

    def __init__(self, uow: UnitOfWork):
        self.uow = uow

    async def sync(self, sync_ops: list[SyncOperation], last_sync: datetime):
        ids_ops = {}
        ids = []
        for op in sync_ops:
            ids_ops[op.processing_id] = op
            ids.append(op.processing_id)

        

        
