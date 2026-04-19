from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    app_name: str = "FitQuest API"
    app_version: str = "1.0.0"
    api_v1_prefix: str = "/api/v1"

    database_url: str = "sqlite:///./fitquest.db"
    supabase_jwt_secret: str = "change-me"

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")


settings = Settings()
