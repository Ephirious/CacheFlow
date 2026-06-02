# Offline-first

Документ описывает подтвержденные offline-first механики frontend-части CacheFlow.

## Основные элементы

Offline-first поведение состоит из двух независимых частей:

```text
Service Worker cache
   +
Local data / sync queue
```

Service Worker отвечает за доступность shell/assets приложения без сети. Sync layer отвечает за доставку локальных изменений на backend и получение удаленных изменений.

## Service Worker

Файл: `frontend/webApp/src/workers/sw.js`.

Подтвержденное поведение:

- кэширование включается только при наличии `BUILD_HASH`;
- имя кэша строится как `cacheflow-h${BUILD_HASH}`;
- при отсутствии `BUILD_HASH` offline-first caching отключается;
- во время `install` кэшируются базовые assets;
- во время `activate` удаляются старые версии кэша;
- Service Worker вызывает `skipWaiting()` и `clients.claim()`.

## Precache assets

Подтвержденный список assets:

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

## Fetch strategy

Service Worker обрабатывает только `GET` запросы.

Исключения:

- Vite dev requests (`/@vite`);
- запросы с `token=` в query string;
- запросы к неразрешенным hosts;
- API/sync requests;
- любые запросы при выключенном `CACHE_NAME`.

## Не кэшируются

API-запросы не кэшируются Service Worker-ом.

Подтвержденные признаки API-запроса:

```text
/api/
/subscribe
/sync
port 8000
```

## Strategies

Файл: `frontend/webApp/src/workers/sw/strategies.js`.

### networkFirst

Используется для navigation requests.

Поведение:

1. сначала выполняется `fetch(request)`;
2. успешный `200` response сохраняется в cache;
3. при ошибке сети возвращается cached response;
4. если cached response отсутствует, возвращается cached `/index.html`.

### dynamicCacheFirst

Используется для остальных cacheable assets.

Поведение:

1. сначала ищется cached response;
2. при отсутствии cache выполняется network request;
3. успешный basic `200` response сохраняется в cache;
4. для navigation fallback возвращается `/index.html`, если он есть в cache.

## Startup integration

В `initApp()` вызывается:

```text
registerServiceWorker()
observeNetwork(syncManager)
syncManager.forceSync(false)
```

Это означает, что приложение:

- регистрирует Service Worker при старте;
- подписывается на network state;
- запускает первичную синхронизацию после инициализации Koin.

## Границы ответственности

Service Worker не занимается merge данных и не кэширует API.

За согласование локального и серверного состояния отвечает `sync` модуль.
