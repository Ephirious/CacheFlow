# WebApp

Изменено: 02.06.2026

`webApp` - React/TypeScript часть frontend. Здесь находится UI приложения, Vite-конфигурация, Service Worker и web-specific entry points.

Kotlin-код здесь не живёт. Бизнес-логика приходит из пакета `k2ts`, который собирается из Kotlin Multiplatform shared layer.

## Структура

```text
webApp/
├── index.html
├── package.json
├── vite.config.ts
├── public/
└── src/
    ├── components/
    ├── styles/
    └── workers/
```

## Точка входа

Основной вход в приложение - `src/index.tsx`.

```text
index.html
  -> src/index.tsx
  -> initApp() from k2ts
  -> RootScreen
```

`index.tsx` делает две вещи:

1. вызывает `initApp()` из `k2ts`;
2. передаёт полученный `RootComponent` в `RootScreen`.

После этого React работает с Kotlin-компонентом как с источником состояния и навигации.

## Vite

Сборка настроена в `vite.config.ts`.

Используются плагины:

- React plugin;
- Tailwind plugin;
- `vite-tsconfig-paths`;
- небольшой локальный plugin для заголовков Service Worker и SQL.js worker.

В dev-режиме приложение запускается на порту `8080`.

Preview запускается на порту `4173`.

## Скрипты

Из `package.json`:

```bash
npm run start
npm run build
npm run preview
```

`start` используется для разработки.

`build` запускает TypeScript compiler и Vite build.

`preview` поднимает production-like сборку локально.

## Service Worker

Service Worker лежит в `src/workers/sw.js`.

Он собирается отдельным entry point через Rollup-настройки Vite и попадает в:

```text
src/workers/sw.js
```

Подробнее см. `../../docs/architecture/offline-first.md`.

## SQL.js worker

`sqljs.worker.js` также собирается отдельным entry point.

Для worker-файлов выставляются заголовки:

```text
Cross-Origin-Opener-Policy: same-origin
Cross-Origin-Embedder-Policy: require-corp
Service-Worker-Allowed: /
```

Это нужно для корректной работы worker-окружения в браузере.

## Зависимости UI

Основные runtime-зависимости:

- React;
- React DOM;
- k2ts;
- Recharts;
- Vaul.

Recharts используется для графиков, Vaul - для drawer-like UI компонентов.

## Что не должно попадать в webApp

`webApp` не должен дублировать бизнес-логику из Kotlin shared layer.

Если логика относится к транзакциям, синхронизации, авторизации, настройкам или навигации, сначала стоит проверить соответствующий shared-модуль. React-часть должна в основном отвечать за отрисовку и пользовательское взаимодействие.
