from __future__ import annotations
from typing import Generic, TypeVar, Type, Optional, Sequence
from uuid import UUID

from pydantic import BaseModel
from sqlalchemy import select, delete
from sqlalchemy.ext.asyncio import AsyncSession

from backend.src.models.base_class import Base

ModelType = TypeVar("ModelType", bound=Base)
CreateSchemaType = TypeVar("CreateSchemaType", bound=BaseModel)
UpdateSchemaType = TypeVar("UpdateSchemaType", bound=BaseModel)

class GenericRepository(Generic[ModelType, CreateSchemaType, UpdateSchemaType]):
    def __init__(self, session: AsyncSession, model: Type[ModelType]):
        self._session = session
        self._model = model

    async def get_by_id(self, entity_id: UUID) -> Optional[ModelType]:
        stmt = select(self._model).where(self._model.id == entity_id)
        res = await self._session.execute(stmt)
        return res.scalar_one_or_none()

    async def get_many(self, *, skip = 0, limit = 100) -> Sequence[ModelType]:
        stmt = select(self._model).offset(skip).limit(limit)
        res = await self._session.execute(stmt)
        return res.scalars().all()

    async def insert(self, body: CreateSchemaType) -> ModelType:
        data = body.model_dump()
        obj = self._model(**data)
        self._session.add(obj)
        await self._session.flush()
        await self._session.refresh(obj)
        return obj

    async def delete(self, entity_id: UUID):
        stmt = delete(self._model).where(self._model.id == entity_id)
        await self._session.execute(stmt)

    async def update(self, db_obj: ModelType, obj_in: UpdateSchemaType) -> ModelType:
        update_data = obj_in.model_dump(exclude_unset=True)
        for field in update_data:
            setattr(db_obj, field, update_data[field])
        self._session.add(db_obj)
        await self._session.flush()
        await self._session.refresh(db_obj)
        return db_obj