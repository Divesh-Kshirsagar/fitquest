from fastapi import APIRouter, Depends
from sqlmodel import Session

from app.api.dependencies import get_db
from app.modules.map.schemas import HexResponse, ViewportQuery
from app.modules.map.service import get_hexes_for_viewport

router = APIRouter()


@router.post("/viewport", response_model=list[HexResponse])
def viewport_endpoint(_: ViewportQuery, db: Session = Depends(get_db)) -> list[HexResponse]:
    rows = get_hexes_for_viewport(db)
    return [HexResponse(hex_id=row.hex_id, owner_id=row.owner_id) for row in rows]
