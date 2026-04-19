import uuid
from datetime import date, datetime
from typing import TYPE_CHECKING, Optional

from sqlmodel import Field, Relationship, SQLModel

if TYPE_CHECKING:
    from app.modules.map.models import HexOwnership
    from app.modules.quests.models import UserQuest


class Friendship(SQLModel, table=True):
    """
    Tracks friend requests and accepted friends.
    Composite primary key prevents duplicate friendships.
    """

    requester_id: uuid.UUID = Field(foreign_key="user.id", primary_key=True)
    addressee_id: uuid.UUID = Field(foreign_key="user.id", primary_key=True)
    status: str = Field(default="pending")  # "pending", "accepted", "blocked"
    created_at: datetime = Field(default_factory=datetime.utcnow)


class User(SQLModel, table=True):
    """Core User model with Profile stats, Streak logic, and Relationships."""

    # CRITICAL: If using Supabase Auth, this ID should match Supabase's auth.users UUID
    id: uuid.UUID = Field(default_factory=uuid.uuid4, primary_key=True)
    username: str = Field(unique=True, index=True)
    avatar_url: Optional[str] = None

    # --- PROFILE STATS ---
    total_lifetime_steps: int = Field(default=0)
    total_hexes_captured: int = Field(default=0)

    # --- STREAK SYSTEM ---
    # To calculate a streak, we just check if today > last_activity_date + 1 day
    current_streak: int = Field(default=0)
    longest_streak: int = Field(default=0)
    last_activity_date: Optional[date] = None

    # --- RELATIONSHIPS ---
    # Defines the reverse relationship for the Map module
    owned_hexes: list["HexOwnership"] = Relationship(back_populates="king")
    active_quests: list["UserQuest"] = Relationship(back_populates="user")
