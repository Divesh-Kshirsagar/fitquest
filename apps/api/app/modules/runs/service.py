import uuid
from sqlmodel import Session, select

from app.modules.runs.models import CapturedHex, RunSession
from app.modules.runs.schemas import RunSyncPayload, RunSyncSummary
from app.modules.map.models import HexOwnership
from app.modules.users.models import User


def process_run_sync(db: Session, payload: RunSyncPayload, current_user_id: uuid.UUID) -> RunSyncSummary:
    summary = RunSyncSummary(
        hexes_defended=0,
        hexes_stolen=0,
        hexes_newly_captured=0,
        xp_earned=0,
        new_total_lifetime_steps=0
    )

    # 1. Update the user's total lifetime steps
    user = db.get(User, current_user_id)
    if user:
        user.total_lifetime_steps += payload.total_session_steps
        summary.new_total_lifetime_steps = user.total_lifetime_steps
        db.add(user)

    # 2. Process the Turf War for each Hexagon
    for hex_id, steps_walked in payload.hexes_to_steps.items():
        # Get the current hex from the database
        statement = select(HexOwnership).where(HexOwnership.hex_id == hex_id)
        current_hex = db.exec(statement).first()

        if not current_hex:
            # NO OWNER: The user claims empty territory
            new_hex = HexOwnership(
                hex_id=hex_id,
                king_id=current_user_id,
                defense_score_steps=steps_walked
            )
            db.add(new_hex)
            summary.hexes_newly_captured += 1
            summary.xp_earned += 50
            
        else:
            # SOMEONE OWNS IT: Let's see who it is and if we beat them
            if current_hex.king_id == current_user_id:
                # User already owns it! Just reinforce the defense score
                current_hex.defense_score_steps += steps_walked
                db.add(current_hex)
                summary.hexes_defended += 1
                summary.xp_earned += 10
            else:
                # RIVAL OWNS IT: Did we beat their score?
                if steps_walked > current_hex.defense_score_steps:
                    # WE STOLE IT!
                    current_hex.king_id = current_user_id
                    current_hex.defense_score_steps = steps_walked # Reset the bar to the new winner's score
                    current_hex.times_stolen += 1
                    db.add(current_hex)
                    summary.hexes_stolen += 1
                    summary.xp_earned += 100

    db.commit()
    return summary