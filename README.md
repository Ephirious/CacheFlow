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

Код:
- [frontend/README.md](./frontend/README.md)
- [backend/README.md](./backend/README.md)

Архитектура:

- [Поток данных](./docs/architecture/data-flow.md)
- [Структура модулей и зависимости](./docs/architecture/module-dependencies.md)
- [React UI](./docs/architecture/react-ui.md)
- [MVI в frontend shared layer](./docs/architecture/mvi.md)
- [Навигация и Decompose](./docs/architecture/decompose.md)
- [Работа без сети](./docs/architecture/offline-first.md)
- [Синхронизация данных](./docs/architecture/synchronization.md)
- [Kotlin/JS bridge: k2ts](./docs/architecture/k2ts.md)

Настройка и запуск:

- [Локальная разработка](./docs/setup/local-development.md)

Документация frontend модулей лежит рядом с кодом в `frontend/shared/*/README.md`.
