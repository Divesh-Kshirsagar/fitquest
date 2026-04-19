import uuid
from datetime import date, datetime
from typing import Optional

from pydantic import BaseModel


class QuestCreate(BaseModel):
    title: str
    description: str
    target_metric: str
    target_value: int
    reward_xp: int = 0
    active_date: Optional[date] = None


class QuestResponse(BaseModel):
    id: uuid.UUID
    title: str
    description: str
    target_metric: str
    target_value: int
    reward_xp: int
    active_date: Optional[date]


class UserQuestCreate(BaseModel):
    user_id: uuid.UUID
    quest_id: uuid.UUID


class UserQuestUpdate(BaseModel):
    current_progress: int
    is_completed: bool = False


class UserQuestResponse(BaseModel):
    user_id: uuid.UUID
    quest_id: uuid.UUID
    current_progress: int
    is_completed: bool
    completed_at: Optional[datetime]
