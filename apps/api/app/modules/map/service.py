import uuid
from datetime import datetime

from sqlmodel import Session, select, text

from app.modules.map.models import HexOwnership
from app.modules.map.schemas import HexUpdate, ViewportQuery, MapViewportResponse, HexDetailResponse


def get_hex_by_id(db: Session, hex_id: str) -> HexOwnership | None:
    return db.get(HexOwnership, hex_id)


def get_hexes_for_viewport(db: Session, query: ViewportQuery, current_user_id: uuid.UUID) -> MapViewportResponse:
    # If zoomed out, we return nothing for now until you write the h3-pg aggregation SQL
    if query.zoom_level < 14.0:
        return MapViewportResponse(is_aggregated=True, hexes=[], heatmaps=[])

    # If zoomed in, query the database using PostGIS/H3 bounds (Raw SQL example)
    # This assumes you have a way to convert hex_id to lat/lng in the DB, 
    # or you store center_lat/center_lng in the HexOwnership table.
    
    # For now, we fetch the hexes (Mocked bounds check)
    raw_sql = text("""
        SELECT h.hex_id, h.king_id, u.username, h.defense_score_steps 
        FROM hexownership h
        JOIN "user" u ON h.king_id = u.id
        LIMIT 500
    """)
    
    results = db.exec(raw_sql).all()
    
    hex_details = []
    for row in results:
        hex_details.append(HexDetailResponse(
            hex_id=row.hex_id,
            king_id=row.king_id,
            king_username=row.username,
            defense_score_steps=row.defense_score_steps,
            is_owned_by_me=(row.king_id == current_user_id)
        ))

    return MapViewportResponse(
        is_aggregated=False,
        hexes=hex_details,
        heatmaps=[]
    )


def get_hexes_for_user(db: Session, user_id: uuid.UUID) -> list[HexOwnership]:
    statement = select(HexOwnership).where(HexOwnership.king_id == user_id)
    return list(db.exec(statement).all())


def create_hex(db: Session, hex_id: str, king_id: uuid.UUID) -> HexOwnership:
    hex_ownership = HexOwnership(hex_id=hex_id, king_id=king_id)
    db.add(hex_ownership)
    db.commit()
    db.refresh(hex_ownership)
    return hex_ownership


def update_hex(db: Session, hex_id: str, payload: HexUpdate) -> HexOwnership | None:
    hex_ownership = get_hex_by_id(db, hex_id)
    if hex_ownership is None:
        return None

    if payload.king_id is not None:
        hex_ownership.king_id = payload.king_id
        hex_ownership.captured_at = datetime.utcnow()

    if payload.defense_score_steps is not None:
        hex_ownership.defense_score_steps = payload.defense_score_steps

    if payload.times_stolen is not None:
        hex_ownership.times_stolen = payload.times_stolen

    db.add(hex_ownership)
    db.commit()
    db.refresh(hex_ownership)
    return hex_ownership
