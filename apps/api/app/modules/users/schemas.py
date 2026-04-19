import uuid
from datetime import date
from typing import Optional

from pydantic import BaseModel, ConfigDict


class UserCreate(BaseModel):
    username: str
    avatar_url: Optional[str] = None


class UserUpdate(BaseModel):
    username: Optional[str] = None
    avatar_url: Optional[str] = None
    total_lifetime_steps: Optional[int] = None
    total_hexes_captured: Optional[int] = None
    current_streak: Optional[int] = None
    longest_streak: Optional[int] = None
    last_activity_date: Optional[date] = None


class UserProfileResponse(BaseModel):
    """Flattened user profile for UI rendering (no Friendship table exposed)."""

    id: uuid.UUID
    username: str
    avatar_url: Optional[str] = None

    # Stats
    total_lifetime_steps: int
    total_hexes_captured: int

    # Streaks
    current_streak: int
    longest_streak: int

    model_config = ConfigDict(from_attributes=True)


class FriendListResponse(BaseModel):
    """Friends and pending friend requests for the profile screen."""

    friends: list[UserProfileResponse]
    pending_requests: list[UserProfileResponse]


class UserResponse(UserProfileResponse):
    """Full user response including activity tracking."""

    last_activity_date: Optional[date] = None
