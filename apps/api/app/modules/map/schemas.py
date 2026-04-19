import uuid
from datetime import datetime
from typing import Optional

from pydantic import BaseModel, ConfigDict


class ViewportQuery(BaseModel):
    """Geo-bounded viewport query with zoom level for aggregation."""

    min_lat: float
    min_lng: float
    max_lat: float
    max_lng: float
    zoom_level: float


class HexDetailResponse(BaseModel):
    """Used when zoom_level >= 14 (Street level) - exact hex details."""

    hex_id: str
    king_id: uuid.UUID
    king_username: str  # Joined from User table
    defense_score_steps: int
    is_owned_by_me: bool  # Calculated on backend for mobile UX

    model_config = ConfigDict(from_attributes=True)


class HeatmapResponse(BaseModel):
    """Used when zoom_level < 14 (City/State level) - aggregated heatmap."""

    parent_hex_id: str  # Lower-resolution H3 hex
    dominant_king_id: uuid.UUID
    dominant_king_username: str
    total_hexes_inside: int  # Count of res-10 hexes owned by this player

    model_config = ConfigDict(from_attributes=True)


class MapViewportResponse(BaseModel):
    """The complete map viewport response, zoom-aware."""

    is_aggregated: bool
    hexes: list[HexDetailResponse] = []
    heatmaps: list[HeatmapResponse] = []


class HexResponse(BaseModel):
    """Raw hex ownership (used internally)."""

    hex_id: str
    king_id: uuid.UUID
    defense_score_steps: int
    captured_at: datetime
    times_stolen: int

    model_config = ConfigDict(from_attributes=True)


class HexCreate(BaseModel):
    hex_id: str
    king_id: uuid.UUID
    defense_score_steps: int = 0
    times_stolen: int = 0


class HexUpdate(BaseModel):
    king_id: Optional[uuid.UUID] = None
    defense_score_steps: Optional[int] = None
    times_stolen: Optional[int] = None
