from sqlalchemy import URL
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession, async_sessionmaker

from backend.src.core.config import settings

url = URL.create(
    drivername="postgresql+asyncpg",
    username=settings.postgres_user,
    password=settings.postgres_password,
    host=settings.database_host,
    database=settings.database_name,
    port=settings.database_port,
)

url_alembic = URL.create(
    drivername="postgresql+psycopg2",
    username=settings.postgres_user,
    password=settings.postgres_password,
    host=settings.database_host,
    database=settings.database_name,
    port=settings.database_port,
)

engine = create_async_engine(
    url,
    future=True,
    pool_pre_ping = settings.pool_pre_ping,
    pool_timeout = settings.pool_timeout,
    pool_recycle = settings.pool_recycle,
    pool_size = settings.pool_size,
    max_overflow = settings.max_overflow,
)

AsyncSessionLocal = async_sessionmaker(
    engine,
    class_=AsyncSession,
    expire_on_commit=False,
)

async def get_session():
    async with AsyncSessionLocal() as session:
        try:
            yield session
        except SQLAlchemyError:
            await session.rollback()
            raise
        finally:
            await session.close()
