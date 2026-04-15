from datetime import datetime
from backend.src.models.sync import Action
from backend.src.repositories.uow import UnitOfWork
from backend.src.schemas.result import Result
from backend.src.schemas.sync import SyncOperation


class SyncService:
    __slots__ = ("uow",)

    def __init__(self, uow: UnitOfWork):
        self.uow = uow

    async def sync(self, sync_ops: list[SyncOperation], last_sync: datetime) -> Result:
        ids_ops: dict[str, list[SyncOperation]] = {}
        ids = []
        for op in sync_ops:
            i = op.processing_id
            if i not in ids_ops:
                ids_ops[str(i)] = [op]
                ids.append(i)
            else:
                if ids_ops[str(i)][0].action == Action.DELETE:
                    continue
                ids_ops[str(i)].append(op)
            
            if op.action == Action.DELETE:
                ids_ops[str(i)] = ids_ops[-1:]

        history = await self.uow.sync_repository.get_by_processing_ids(sync_ops, last_sync)
        cur_processing_id = None
        for h in history:
            cur_processing_id = h.processing_id


        
