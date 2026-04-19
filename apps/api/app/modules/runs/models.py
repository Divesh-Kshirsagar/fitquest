from datetime import datetime, timezone

from sqlmodel import Field, SQLModel


class RunSession(SQLModel, table=True):
    id: str = Field(primary_key=True)
    user_id: str = Field(index=True)
    started_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))


class CapturedHex(SQLModel, table=True):
    id: int | None = Field(default=None, primary_key=True)
    run_id: str = Field(index=True)
    hex_id: str = Field(index=True)
