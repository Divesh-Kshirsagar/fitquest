from sqlmodel import Session, select

from app.modules.users.models import User
from app.modules.users.schemas import UserCreate


def create_user(db: Session, payload: UserCreate) -> User:
    user = User(id=payload.id, username=payload.username)
    db.add(user)
    db.commit()
    db.refresh(user)
    return user


def get_user_by_id(db: Session, user_id: str) -> User | None:
    statement = select(User).where(User.id == user_id)
    return db.exec(statement).first()
