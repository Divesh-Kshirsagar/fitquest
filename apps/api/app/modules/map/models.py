from datetime import datetime, timezone

from sqlmodel import Field, SQLModel


class HexOwnership(SQLModel, table=True):
    hex_id: str = Field(primary_key=True)
    owner_id: str = Field(index=True)
    claimed_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
