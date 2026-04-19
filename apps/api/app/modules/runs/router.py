from fastapi import APIRouter, Depends
from sqlmodel import Session

from app.api.dependencies import get_db
from app.modules.runs.schemas import RunSyncPayload, RunSyncResponse
from app.modules.runs.service import process_run_transaction

router = APIRouter()


@router.post("/sync", response_model=RunSyncResponse)
def sync_run_endpoint(payload: RunSyncPayload, db: Session = Depends(get_db)) -> RunSyncResponse:
    captured_count = process_run_transaction(db, payload)
    return RunSyncResponse(run_id=payload.run_id, captured_count=captured_count)
