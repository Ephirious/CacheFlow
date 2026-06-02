# Unit of Work

Изменено: 02.06.2026

`UnitOfWork` собирает репозитории вокруг одной SQLAlchemy session и управляет commit / rollback.

## Что создаётся внутри

При входе в context manager создаются репозитории:

- `UserRepository`
- `EmailCodeRepository`
- `SyncOperationRepository`
- `AccountRepository`
- `CategoryRepository`
- `OperationRepository`
- `TransferRepository`

## Как использовать

Сервис должен работать так:

```python
async with self.uow as uow:
    user = await uow.user_repository.get_by_id(user_id)
```

Если внутри блока произошла ошибка, вызывается `rollback()`.

Если блок завершился без ошибки, вызывается `commit()`.

После этого session закрывается.

## Главное правило

Сервис не должен создавать репозитории вручную и не должен сам управлять SQLAlchemy session. Для этого уже есть `UnitOfWork`.
