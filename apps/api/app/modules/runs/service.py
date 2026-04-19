from sqlmodel import Session

from app.modules.runs.models import CapturedHex, RunSession
from app.modules.runs.schemas import RunSyncPayload


def process_run_transaction(db: Session, payload: RunSyncPayload) -> int:
    session = db.get(RunSession, payload.run_id)
    if session is None:
        session = RunSession(id=payload.run_id, user_id=payload.user_id)
        db.add(session)

    for hex_payload in payload.captured_hexes:
        db.add(CapturedHex(run_id=payload.run_id, hex_id=hex_payload.hex_id))

    db.commit()
    return len(payload.captured_hexes)
