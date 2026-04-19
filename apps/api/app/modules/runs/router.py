from fastapi import APIRouter, Depends
import uuid
from sqlmodel import Session

from app.api.dependencies import get_db, get_current_user
from app.modules.runs.schemas import RunSyncPayload, RunSyncSummary
from app.modules.runs.service import process_run_sync

router = APIRouter()

@router.post("/sync", response_model=RunSyncSummary)
def sync_run_endpoint(
    payload: RunSyncPayload, 
    db: Session = Depends(get_db), 
    current_user: dict = Depends(get_current_user)
) -> RunSyncSummary:
    user_id = uuid.UUID(current_user["id"])
    return process_run_sync(db, payload, user_id)
