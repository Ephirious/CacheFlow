# Backend

Изменено: 02.06.2026

Backend отвечает за API, авторизацию, синхронизацию и хранение серверного состояния в PostgreSQL.

Backend использует FastAPI для API-слоя, SQLAlchemy для работы с базой, Alembic для миграций; сервисный слой отвечает за бизнес-логику. Доступ к базе должен идти через репозитории и Unit of Work, а не напрямую из роутов.

## Дополнительная документация

- `docs/architecture.md`
- `docs/auth.md`
- `docs/sync.md`
- `docs/database.md`
- `docs/unit-of-work.md`
- `docs/security.md`

## Структура

```text
backend/
├── alembic.ini
├── pyproject.toml
├── uv.lock
└── src/
    ├── api/
    ├── core/
    ├── database/
    ├── dependencies/
    ├── migrations/
    ├── models/
    ├── repositories/
    ├── schemas/
    └── services/
```

## Команды

Создать новую миграцию:

```bash
make revision m="сообщение_миграции"
```

Применить миграции:

```bash
make upgrade
```

Запустить backend:

```bash
make run
```

## Правила для backend-кода

- Роуты не должны работать с базой напрямую.
- Сервисы должны использовать `UnitOfWork`.
- Репозитории должны инкапсулировать SQLAlchemy-запросы.
- Новые изменения схемы базы должны сопровождаться миграцией.
- Если в проекте уже используется `Result`, новый сервисный код лучше писать в том же стиле.
