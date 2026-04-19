from fastapi import APIRouter

from app.modules.map import router as map_router
from app.modules.quests import router as quests_router
from app.modules.runs import router as runs_router
from app.modules.users import router as users_router

api_router = APIRouter()

api_router.include_router(users_router.router, prefix="/users", tags=["Users"])
api_router.include_router(map_router.router, prefix="/map", tags=["Map"])
api_router.include_router(runs_router.router, prefix="/runs", tags=["Runs"])
api_router.include_router(quests_router.router, prefix="/quests", tags=["Quests"])
