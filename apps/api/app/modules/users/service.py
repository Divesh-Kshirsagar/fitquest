import uuid

from sqlmodel import Session, select

from app.modules.users.models import User
from app.modules.users.schemas import UserCreate, UserUpdate


def create_user(db: Session, payload: UserCreate) -> User:
    user = User(username=payload.username, avatar_url=payload.avatar_url)
    db.add(user)
    db.commit()
    db.refresh(user)
    return user


def get_user_by_id(db: Session, user_id: uuid.UUID) -> User | None:
    return db.get(User, user_id)


def get_user_by_username(db: Session, username: str) -> User | None:
    statement = select(User).where(User.username == username)
    return db.exec(statement).first()


def get_all_users(db: Session) -> list[User]:
    statement = select(User)
    return list(db.exec(statement).all())


def update_user(db: Session, user_id: uuid.UUID, payload: UserUpdate) -> User | None:
    user = get_user_by_id(db, user_id)
    if user is None:
        return None

    update_data = payload.model_dump(exclude_unset=True)
    for key, value in update_data.items():
        setattr(user, key, value)

    db.add(user)
    db.commit()
    db.refresh(user)
    return user
