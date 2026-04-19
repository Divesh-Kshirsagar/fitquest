import uuid
from datetime import date, datetime
from typing import TYPE_CHECKING, Optional

from sqlmodel import Field, Relationship, SQLModel

if TYPE_CHECKING:
    from app.modules.users.models import User


class Quest(SQLModel, table=True):
    """
    The global definition of a challenge.
    Created by the admin/system.
    """

    id: uuid.UUID = Field(default_factory=uuid.uuid4, primary_key=True)
    title: str = Field(index=True)  # e.g., "Weekend Warrior"
    description: str

    # Metrics: "steps", "hexes_captured", "hexes_stolen"
    target_metric: str
    target_value: int

    # Rewards
    reward_xp: int = Field(default=0)

    # If it's a daily quest, this defines which day it belongs to
    active_date: Optional[date] = Field(default=None, index=True)

    # --- RELATIONSHIPS ---
    user_progress: list["UserQuest"] = Relationship(back_populates="quest")


class UserQuest(SQLModel, table=True):
    """
    Tracks a specific user's progress on a specific quest.
    """

    user_id: uuid.UUID = Field(foreign_key="user.id", primary_key=True)
    quest_id: uuid.UUID = Field(foreign_key="quest.id", primary_key=True)

    current_progress: int = Field(default=0)
    is_completed: bool = Field(default=False)
    completed_at: Optional[datetime] = None

    # --- RELATIONSHIPS ---
    user: Optional["User"] = Relationship(back_populates="active_quests")
    quest: Optional["Quest"] = Relationship(back_populates="user_progress")
