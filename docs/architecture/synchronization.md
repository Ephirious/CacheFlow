# Synchronization

Документ описывает подтвержденную реализацию синхронизации.

## Основные компоненты

Подтвержденные элементы:

- SyncManager
- SyncManagerImpl
- SyncRepository
- SyncRepositoryImpl
- SyncQueue
- SyncQueueRepositoryImpl
- SyncScheduler
- SyncRemoteDataSource
- SyncLocalDataSource

## SyncManager

Публичный контракт:

```text
requestSync()
forceSync(retry)
status: StateFlow<SyncStatus>
```

## SyncStatus

Подтвержденные состояния:

- Ok
- InProcess
- Failed
- WouldRetry(inSeconds)

## Scheduling

Синхронизация запускается не напрямую.

Используется `SyncScheduler`, который получает изменения из:

```text
queueRepo.getUnsyncedFlow()
```

После debounce генерируется событие синхронизации.

## Concurrency protection

Подтвержденные механизмы:

- Mutex;
- Web Lock API wrapper (`withWebLock(...)`);
- SupervisorJob;
- отдельный AsyncDispatcher.

Это предотвращает одновременный запуск нескольких процессов синхронизации.

## Retry policy

Подтвержденный алгоритм:

```text
10s
20s
40s
80s
...
max 5 minutes
```

Используется exponential backoff.

Во время ожидания статус переводится в:

```text
WouldRetry(remainingSeconds)
```

## Request pipeline

```text
Unsynced queue rows
        ↓
mapSyncQueueRow(...)
        ↓
SyncRequest
        ↓
remoteDataSource.sendSyncRequest(...)
        ↓
SyncResponse
```

## Server response handling

Подтвержденные блоки ответа:

### acceptedIds

Успешно обработанные операции удаляются из локальной очереди.

### deleteOperations

Применяются локально:

- softDeleteAccount
- softDeleteCategory
- hardDeleteTransfer
- hardDeleteTransaction

### updateState

Применяются локально через repository layer:

- accountsRepo.upsertAccount(...)
- categoriesRepo.upsertCategory(...)
- transactionsRepo.badInsertTransfer(...)
- transactionsRepo.badInsertTransaction(...)

## Authentication requirement

Синхронизация невозможна без токенов авторизации.

Перед запуском проверяется:

```text
TokenStorage.isTokensEmpty()
```

Если токены отсутствуют, синхронизация завершается без выполнения запроса.

## Startup behavior

Во время запуска приложения вызывается:

```text
syncManager.forceSync(false)
```

что инициирует первичное выравнивание локального состояния с сервером.
