from pydantic import BaseModel


class CapturedHexPayload(BaseModel):
    hex_id: str


class RunSyncPayload(BaseModel):
    run_id: str
    user_id: str
    captured_hexes: list[CapturedHexPayload]


class RunSyncResponse(BaseModel):
    run_id: str
    captured_count: int
