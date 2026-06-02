# Data Flow

Документ описывает движение данных между React UI, Kotlin Multiplatform shared layer и backend API.

## Frontend flow

```text
React UI
   ↓
TypeScript bindings
   ↓
Kotlin/JS exported API
   ↓
Decompose Component
   ↓
FlowMVI Container / Store
   ↓
UseCase
   ↓
Repository interface
   ↓
Repository implementation
   ↓
SQLDelight / Settings / Ktor API
```

## UI events

UI не должен напрямую работать с базой данных или HTTP-клиентом. Он отправляет пользовательские действия в presentation layer:

- submit формы;
- изменение фильтра;
- переход между экранами;
- создание/редактирование сущности;
- запуск синхронизации.

Presentation layer преобразует события в intents/actions и передает их в MVI container.

## MVI state updates

Типичный цикл:

```text
Intent
  → business action
  → repository call
  → state mutation
  → StateFlow emission
  → React render
```

Состояние экрана должно быть производным от domain/data state. UI-компоненты не должны хранить бизнес-состояние, которое уже есть в shared layer.

## Local-first write flow

```text
User Action
   ↓
UseCase
   ↓
Local Repository
   ↓
SQLDelight transaction
   ↓
Sync Queue operation
   ↓
UI state update
   ↓
Background/server sync
```

Frontend сначала фиксирует изменение локально, после чего sync layer доставляет операцию на backend.

## Read flow

```text
Screen opened
   ↓
Component subscribes to Flow
   ↓
Repository observes SQLDelight query
   ↓
Flow emits domain models
   ↓
MVI state is updated
```

## Backend flow

```text
HTTP Request
   ↓
FastAPI router
   ↓
Dependency injection
   ↓
Service
   ↓
UnitOfWork
   ↓
Repository
   ↓
SQLAlchemy model
   ↓
PostgreSQL
```

## Sync flow

```text
Client sync queue
   ↓
POST /sync
   ↓
SyncService
   ↓
server-side operation validation
   ↓
repository updates
   ↓
SyncResponse
   ↓
client local merge
```

## Ошибки

Ошибки должны проходить через domain/presentation result model:

- backend возвращает HTTP error или structured error;
- data layer мапит ошибку в domain-level result;
- usecase возвращает failure;
- MVI container отображает ошибку в state.
