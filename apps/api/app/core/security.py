from typing import Any

from jose import JWTError, jwt

from app.core.config import settings


def decode_supabase_jwt(token: str) -> dict[str, Any]:
    try:
        payload = jwt.decode(token, settings.supabase_jwt_secret, algorithms=["HS256"])
        return payload
    except JWTError as exc:
        raise ValueError("Invalid or expired token") from exc
