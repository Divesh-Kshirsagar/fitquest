from typing import Dict

from pydantic import BaseModel


class RunSyncPayload(BaseModel):
    """
    The exact payload the Android app sends when a run finishes.
    Matches the Android Map<String, Int> (HexID to Steps) perfectly.
    """

    total_session_steps: int
    # Maps hex_id -> steps_walked_in_hex
    hexes_to_steps: Dict[str, int]


class RunSyncSummary(BaseModel):
    """
    Gamified result of the run.
    Triggers animations on the Android side.
    """

    hexes_defended: int  # Hexes retained from previous owner
    hexes_stolen: int  # Hexes captured from another player
    hexes_newly_captured: int  # Unclaimed hexes now captured
    xp_earned: int
    new_total_lifetime_steps: int
