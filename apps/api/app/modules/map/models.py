import uuid
from datetime import datetime
from typing import TYPE_CHECKING, Optional

from sqlmodel import Field, Relationship, SQLModel

if TYPE_CHECKING:
    from app.modules.users.models import User


class HexOwnership(SQLModel, table=True):
    """
    The 'King of the Hill' table.
    Represents the CURRENT owner of a specific H3 hexagon.
    """

    # The H3 String (e.g., "8a2a1072b59ffff") is the perfect Primary Key
    hex_id: str = Field(primary_key=True)

    # The current "King" of this hexagon
    king_id: uuid.UUID = Field(foreign_key="user.id", index=True)

    # The score someone else needs to beat to steal this hex
    defense_score_steps: int = Field(default=0)

    captured_at: datetime = Field(default_factory=datetime.utcnow)

    # How many times this hex has changed hands (great for "Hotzone" UI)
    times_stolen: int = Field(default=0)

    # --- RELATIONSHIPS ---
    king: Optional["User"] = Relationship(back_populates="owned_hexes")
