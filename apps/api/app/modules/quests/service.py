import uuid

from sqlmodel import Session, select

from app.modules.quests.models import Quest, UserQuest
from app.modules.quests.schemas import QuestCreate, UserQuestUpdate


def create_quest(db: Session, payload: QuestCreate) -> Quest:
    """Create a new quest."""
    quest = Quest(**payload.model_dump())
    db.add(quest)
    db.commit()
    db.refresh(quest)
    return quest


def get_quest_by_id(db: Session, quest_id: uuid.UUID) -> Quest | None:
    """Fetch a quest by ID."""
    return db.get(Quest, quest_id)


def get_all_quests(db: Session) -> list[Quest]:
    """Fetch all active quests."""
    statement = select(Quest)
    return list(db.exec(statement).all())


def get_user_quests(db: Session, user_id: uuid.UUID) -> list[UserQuest]:
    """Fetch all quests for a user."""
    statement = select(UserQuest).where(UserQuest.user_id == user_id)
    return list(db.exec(statement).all())


def get_user_quest(
    db: Session, user_id: uuid.UUID, quest_id: uuid.UUID
) -> UserQuest | None:
    """Fetch a user's progress on a specific quest."""
    statement = select(UserQuest).where(
        (UserQuest.user_id == user_id) & (UserQuest.quest_id == quest_id)
    )
    return db.exec(statement).first()


def create_user_quest(
    db: Session, user_id: uuid.UUID, quest_id: uuid.UUID
) -> UserQuest:
    """Enroll a user in a quest."""
    user_quest = UserQuest(user_id=user_id, quest_id=quest_id)
    db.add(user_quest)
    db.commit()
    db.refresh(user_quest)
    return user_quest


def update_user_quest(
    db: Session, user_id: uuid.UUID, quest_id: uuid.UUID, payload: UserQuestUpdate
) -> UserQuest | None:
    """Update a user's quest progress."""
    user_quest = get_user_quest(db, user_id, quest_id)
    if user_quest is None:
        return None

    user_quest.current_progress = payload.current_progress
    user_quest.is_completed = payload.is_completed
    if payload.is_completed and user_quest.completed_at is None:
        from datetime import datetime
        user_quest.completed_at = datetime.utcnow()

    db.add(user_quest)
    db.commit()
    db.refresh(user_quest)
    return user_quest
