# CacheFlow

Изменено: 02.06.2026

CacheFlow - приложение для учета финансов с локальным хранением данных, синхронизацией с backend и web-интерфейсом на React.

Проект состоит из двух основных частей:

```text
CacheFlow/
├── backend/   # FastAPI, PostgreSQL, Alembic, сервисы авторизации и синхронизации
└── frontend/  # Kotlin Multiplatform shared layer + React/TypeScript UI
```

## Где читать документацию

Основной вход для frontend-части:

- [frontend/README.md](./frontend/README.md)

Архитектура:

- [Поток данных](./docs/architecture/data-flow.md)
- [Структура модулей и зависимости](./docs/architecture/module-dependencies.md)
- [MVI в frontend shared layer](./docs/architecture/mvi.md)
- [Навигация и Decompose](./docs/architecture/decompose.md)
- [Работа без сети](./docs/architecture/offline-first.md)
- [Синхронизация данных](./docs/architecture/synchronization.md)
- [Kotlin/JS bridge: k2ts](./docs/architecture/k2ts.md)

Настройка и запуск:

- [Локальная разработка](./docs/setup/local-development.md)
- [Сборка frontend](./docs/setup/build-pipeline.md)

Документация модулей лежит рядом с кодом в `frontend/shared/*/README.md`.

## Быстрый старт frontend

```bash
cd frontend
./gradlew buildK2ts
npm install
npm run start
```

Для production-like проверки:

```bash
npm run preview
```

Подробности по backend см. в [backend/README.md](./backend/README.md).
