from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file = ".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )

    postgres_user: str
    postgres_password: str
    database_host: str
    database_port: int
    database_name: str
    pool_size: int
    max_overflow: int
    pool_timeout: int
    pool_recycle: int
    pool_pre_ping: bool

    email_code_expire_min: int
    smtp_server: str
    smtp_port: int
    smtp_username: str
    smtp_password: str
    from_email: str

settings = Settings()