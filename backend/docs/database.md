# База данных

Изменено: 02.06.2026

Backend использует SQLAlchemy для моделей и Alembic для миграций.

## Где что лежит

```text
src/models/        # SQLAlchemy модели
src/repositories/  # запросы к базе
src/database/      # подключение и session factory
src/migrations/    # Alembic migrations
```

## Основной путь к данным

```text
Service
  -> UnitOfWork
  -> Repository
  -> SQLAlchemy
```

Сервис не должен работать с SQLAlchemy session напрямую.

## Модели

В `src/models` лежат модели пользователей, счетов, категорий, операций, email-кодов и синхронизации.

## Репозитории

Для типовых операций используется `GenericRepository`. Если нужна нестандартная выборка, её стоит добавлять в конкретный репозиторий.

## Миграции

Создать миграцию:

```bash
make revision m="описание"
```

Применить миграции:

```bash
make upgrade
```

Если изменилась модель базы, изменение должно сопровождаться миграцией.
