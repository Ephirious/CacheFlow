from fastapi import APIRouter, HTTPException

from backend.src.dependencies.models.user import CurrentUser
from backend.src.dependencies.services.sync import SyncServiceDep
from backend.src.schemas.sync import SyncRequest, SyncResponse


router = APIRouter(prefix = "/sync", tags = ["Sync"])

@router.post("/", response_model = SyncResponse, responses = {500: {"description": "Something went wrong"}})
async def sync(user: CurrentUser, body: SyncRequest, sync_service: SyncServiceDep):
    #TODO: add auth check for {user}

    resp = await sync_service.sync(body.operations, body.last_sync_date, user.id)
    if resp.is_success:
        return resp.value
    
    raise HTTPException(
        status_code= 500,
        detail=resp.error.message
    )