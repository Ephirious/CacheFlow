# Работа без сети

Изменено: 02.06.2026

В CacheFlow офлайн-режим состоит из двух частей: кэша приложения в Service Worker и локальных данных с очередью синхронизации.

Service Worker нужен, чтобы приложение открывалось без сети. Очередь синхронизации нужна, чтобы локальные изменения позже доехали до backend.

```text
Service Worker cache
  +
Local data / sync queue
```

## Service Worker

Основной файл: `frontend/webApp/src/workers/sw.js`.

Кэширование включается только при наличии `BUILD_HASH`. Если хэша нет, Service Worker логирует, что offline-first mode отключён, и не включает кэш.

Имя кэша строится так:

```text
cacheflow-h${BUILD_HASH}
```

При установке Service Worker заранее кладёт в кэш базовые файлы приложения:

```text
/
/index.html
/manifest.json
/ico/icon-192.png
/ico/icon-512.png
/src/workers/sw.js
/src/workers/sw/notifications.js
/src/workers/sw/strategies.js
/src/workers/sqljs.worker.js
```

При активации старые версии кэша удаляются. Это важно после нового билда: приложение не должно оставаться на старых assets.

## Что кэшируется

Service Worker обрабатывает только `GET`-запросы и только разрешённые hosts:

- текущий origin;
- `cdn.tailwindcss.com`;
- `fonts.googleapis.com`;
- `fonts.gstatic.com`.

Vite dev-запросы, запросы с `token=` и запросы к API не кэшируются.

API определяется по следующим признакам:

```text
/api/
/subscribe
/sync
port 8000
```

Это важная граница: Service Worker не пытается кэшировать backend API и не занимается merge данных.

## Стратегии кэширования

Стратегии лежат в `frontend/webApp/src/workers/sw/strategies.js`.

Для переходов по страницам используется `networkFirst`: сначала сеть, затем fallback на кэш, а если нужной страницы нет - на `/index.html`.

Для остальных assets используется `dynamicCacheFirst`: сначала кэш, затем сеть. Успешные ответы сохраняются обратно в кэш.

## Что происходит при старте приложения

В `initApp()` вызываются:

```text
registerServiceWorker()
observeNetwork(syncManager)
syncManager.forceSync(false)
```

То есть приложение регистрирует Service Worker, начинает следить за сетью и запускает первичную синхронизацию.

## Где заканчивается Service Worker

Service Worker отвечает только за shell и assets. Данные приложения живут в локальной базе, а согласованием с сервером занимается модуль `sync`.
