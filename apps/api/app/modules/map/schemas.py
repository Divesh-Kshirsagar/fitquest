from pydantic import BaseModel


class ViewportQuery(BaseModel):
    north: float
    south: float
    east: float
    west: float


class HexResponse(BaseModel):
    hex_id: str
    owner_id: str
