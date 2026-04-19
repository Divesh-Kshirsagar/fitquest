import uuid

from fastapi import APIRouter, Depends, HTTPException, status
from sqlmodel import Session

from app.api.dependencies import get_db
from app.modules.users.schemas import UserCreate, UserResponse, UserUpdate
from app.modules.users.service import (
    create_user,
    get_all_users,
    get_user_by_id,
    get_user_by_username,
    update_user,
)

router = APIRouter()


@router.post("", response_model=UserResponse, status_code=status.HTTP_201_CREATED)
def create_user_endpoint(payload: UserCreate, db: Session = Depends(get_db)) -> UserResponse:
    """Create a new user."""
    if get_user_by_username(db, payload.username):
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT, detail="Username already exists"
        )
    user = create_user(db, payload)
    return UserResponse.model_validate(user)


@router.get("", response_model=list[UserResponse])
def get_users_endpoint(db: Session = Depends(get_db)) -> list[UserResponse]:
    """Get all users."""
    users = get_all_users(db)
    return [UserResponse.model_validate(u) for u in users]


@router.get("/{user_id}", response_model=UserResponse)
def get_user_endpoint(user_id: uuid.UUID, db: Session = Depends(get_db)) -> UserResponse:
    """Get a specific user by ID."""
    user = get_user_by_id(db, user_id)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")
    return UserResponse.model_validate(user)


@router.patch("/{user_id}", response_model=UserResponse)
def update_user_endpoint(
    user_id: uuid.UUID, payload: UserUpdate, db: Session = Depends(get_db)
) -> UserResponse:
    """Update a user's profile."""
    user = update_user(db, user_id, payload)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")
    return UserResponse.model_validate(user)
