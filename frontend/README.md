# Frontend

Изменено: 02.06.2026

Frontend состоит из React-приложения и Kotlin Multiplatform shared layer.

```text
frontend/
├── webApp/   # React + TypeScript
├── k2ts/     # Kotlin/JS bridge
└── shared/   # общая бизнес-логика
```

## Что где находится

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

### k2ts

Модуль, который экспортирует Kotlin-код в JavaScript и генерирует TypeScript-описания.

Именно через него React получает доступ к состоянию экранов и навигации.

## Перед запуском

Понадобятся:

- Java 17;
- Node.js и npm;
- IntelliJ IDEA с Kotlin Multiplatform Plugin (желательно).

## Сборка Kotlin/JS

Linux/macOS:

```bash
./gradlew buildK2ts
```

Windows:

```powershell
.\gradlew.bat buildK2ts
```

## Запуск React-приложения

```bash
npm install
npm run start
```

## Режимы работы

### Разработка

```bash
npm run start
```

Поддерживает hot reload, но не включает offline-first поведение Service Worker.

### Проверка production-сборки

```bash
npm run preview
```

В этом режиме приложение ближе всего к реальному production-окружению. Можно проверять PWA-функциональность и работу Service Worker.

## Полезные документы

- `../docs/architecture/offline-first.md`
- `../docs/architecture/synchronization.md`
- `../docs/architecture/k2ts.md`
