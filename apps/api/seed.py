import uuid
import datetime
from jose import jwt
from sqlmodel import Session, select
from app.core.database import engine, create_db_and_tables
from app.modules.users.models import User
from app.modules.users.models import Friendship
from app.modules.map.models import HexOwnership
from app.modules.runs.models import RunSession, CapturedHex
from app.modules.quests.models import Quest, UserQuest
from app.core.config import settings

def seed_db():
    print("Seeding database...")
    create_db_and_tables()

    test_user_id = uuid.UUID("11111111-2222-3333-4444-555555555555")

    with Session(engine) as session:
        user = session.exec(select(User).where(User.id == test_user_id)).first()
        if not user:
            user = User(
                id=test_user_id,
                username="testuser",
                total_lifetime_steps=1000,
                total_hexes_captured=5
            )
            session.add(user)
            session.commit()
            print("✅ User seeded successfully!")
        else:
            print("✅ User already exists.")

    # Generate Test JWT
    payload = {
        "sub": str(test_user_id),
        "aud": "authenticated",
        "exp": datetime.datetime.utcnow() + datetime.timedelta(days=7)
    }

    token = jwt.encode(payload, settings.supabase_jwt_secret, algorithm="HS256")
    print(f"\n🔑 Test Bearer Token:\n{token}\n")

if __name__ == "__main__":
    seed_db()
