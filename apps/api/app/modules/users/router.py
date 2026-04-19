from fastapi import APIRouter, Depends, HTTPException, status
from sqlmodel import Session

from app.api.dependencies import get_db
from app.modules.users.schemas import UserCreate, UserResponse
from app.modules.users.service import create_user, get_user_by_id

router = APIRouter()


@router.post("", response_model=UserResponse, status_code=status.HTTP_201_CREATED)
def create_user_endpoint(payload: UserCreate, db: Session = Depends(get_db)) -> UserResponse:
    if get_user_by_id(db, payload.id):
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="User already exists")
    user = create_user(db, payload)
    return UserResponse(id=user.id, username=user.username)


@router.get("/{user_id}", response_model=UserResponse)
def get_user_endpoint(user_id: str, db: Session = Depends(get_db)) -> UserResponse:
    user = get_user_by_id(db, user_id)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")
    return UserResponse(id=user.id, username=user.username)
