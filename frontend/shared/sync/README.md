# Sync Module

## Назначение

Модуль реализует синхронизацию локального состояния приложения с backend.

Система построена вокруг очереди операций и менеджера синхронизации.

## Структура

```text
sync/
├── data/
└── domain/
```

## Domain

### SyncStatus

Подтвержденные состояния:

- Ok
- InProcess
- Failed
- WouldRetry(inSeconds)

### SyncRepository

Подтвержденные операции:

- resetLastSyncDate()
- setSyncLock(isSyncRunning)

### SyncManager

Подтвержденные операции:

- requestSync()
- forceSync(retry)

Подтвержденные свойства:

- status: StateFlow<SyncStatus>
- scope: CoroutineScope

## Data

Подтвержденные элементы:

- SyncRepositoryImpl
- SyncQueueRepositoryImpl
- SyncQueue
- SyncManagerImpl

Data слой содержит реализацию очереди синхронизации и обмена данными с backend API.

## Архитектурная роль

Модуль является центральной точкой согласования локального и удаленного состояния.

Используется другими feature-модулями для доставки изменений на сервер.

## UI Integration

Статус синхронизации доступен через `StateFlow<SyncStatus>` и может отображаться в интерфейсе без прямого доступа к внутренней реализации очереди.
