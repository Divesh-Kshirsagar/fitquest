from pydantic import BaseModel


class UserCreate(BaseModel):
    id: str
    username: str


class UserResponse(BaseModel):
    id: str
    username: str
