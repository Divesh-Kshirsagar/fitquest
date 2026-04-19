from sqlmodel import Session, select

from app.modules.map.models import HexOwnership


def get_hexes_for_viewport(db: Session) -> list[HexOwnership]:
    statement = select(HexOwnership)
    return list(db.exec(statement).all())
