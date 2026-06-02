# Frontend

Изменено: 02.06.2026

Frontend состоит из React-приложения и Kotlin Multiplatform shared layer.

```text
frontend/
├── webApp/          # React + TypeScript
├── k2ts/            # Kotlin/JS bridge
├── shared/          # общая бизнес-логика
└── ksp-processor/   # кодогенерация
```

### shared

Здесь находится основная бизнес-логика приложения:
- авторизация;
- транзакции;
- счета и категории;
- синхронизация;
- статистика;
- навигация.

Подробности по каждому модулю описаны в `shared/*/README.md`.

### webApp

React-приложение, которое использует Kotlin/JS библиотеку, собранную из `k2ts`.


## Полезная документация
- `../docs/setup/local-development.md`
- `../docs/architecture/offline-first.md`
- `../docs/architecture/synchronization.md`
- `../docs/architecture/k2ts.md`
