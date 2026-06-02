# Docker

Изменено: 02.06.2026

В проекте есть production compose-файл `docker-compose-prod.yml`. Он подключает backend compose-файл и поднимает frontend вместе с backend-инфраструктурой.

## Что поднимается

Через Docker Compose запускаются:

- `frontend` - web-приложение, доступно на порту `5000`;
- `backend` - FastAPI API, доступно на порту `8000`;
- `db` - PostgreSQL 16, наружу открыт порт `5433`;
- `migrations` - одноразовый контейнер для применения Alembic-миграций;
- `pgadmin` - доступен на порту `5050`.

## Команды

Команды запускаются из корня репозитория.

Собрать образы:

```bash
make build
```

Пересобрать образы без кэша:

```bash
make rebuild
```

Поднять контейнеры:

```bash
make up
```

Остановить и удалить контейнеры:

```bash
make down
```

Остановить контейнеры и удалить volumes с базой и кэшем:

```bash
make down-v
```

`down-v` удаляет данные PostgreSQL и pgAdmin, потому что volumes тоже будут удалены.

## Переменные окружения

Backend compose-файл использует `.env` из `backend/`.

Нужны значения для PostgreSQL и pgAdmin:

```env
DATABASE_NAME=
POSTGRES_USER=
POSTGRES_PASSWORD=
PGADMIN_DEFAULT_EMAIL=
PGADMIN_DEFAULT_PASSWORD=
```

## Nginx и reverse proxy

В репозитории нет отдельного nginx-конфига.

Для production-деплоя Nginx можно использовать как внешний reverse proxy перед контейнерами. В таком случае он обычно проксирует:

- frontend на порт `5000`;
- backend API на порт `8000`.

Конкретный nginx-конфиг зависит от домена, TLS-сертификатов и окружения деплоя, поэтому в проектной документации он не зафиксирован как часть текущего compose setup.
