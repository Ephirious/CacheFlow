# Поток данных

Изменено: 02.06.2026

Этот файл описывает, как данные проходят через React, Kotlin shared layer и backend API.

Главная идея простая: UI не ходит напрямую в базу или HTTP-клиент. React общается с Kotlin-компонентами, компоненты передают действия в MVI-контейнеры, а бизнес-логика уходит в use case'ы и репозитории.

## Frontend

```text
React UI
  -> TypeScript bindings
  -> Kotlin/JS exported API
  -> Decompose Component
  -> FlowMVI Container / Store
  -> UseCase
  -> Repository interface
  -> Repository implementation
  -> SQLDelight / Settings / Ktor API
```

Пользовательские действия приходят из UI как intents. Это может быть отправка формы, смена фильтра, переход между экранами, создание сущности или ручной запуск синхронизации.

MVI-контейнер обрабатывает intent, вызывает нужный use case и обновляет состояние экрана. React подписан на это состояние через JS-interop обёртки.

## Запись данных

В большинстве пользовательских сценариев приложение сначала меняет локальное состояние, а уже потом синхронизирует его с сервером.

```text
User action
  -> UseCase
  -> Local Repository
  -> SQLDelight transaction
  -> UI state update / Sync Queue operation (parallel)
  -> Background sync
```

Такой порядок нужен, чтобы интерфейс не зависел от качества сети. Пользователь видит результат сразу, а `sync`-модуль позже доставляет изменение на backend.

## Чтение данных

Чтение обычно построено вокруг `Flow`.

```text
Screen opened
  -> Component subscribes to Flow
  -> Repository observes SQLDelight query
  -> Flow emits domain models
  -> MVI state is updated
```

Экран не перечитывает данные вручную после каждого действия. Если локальная база изменилась, поток отдаёт новое значение, а состояние экрана пересобирается.

## Backend

На сервере поток данных выглядит так:

```text
HTTP Request
  -> FastAPI router
  -> Dependency injection
  -> Service
  -> UnitOfWork
  -> Repository
  -> SQLAlchemy model
  -> PostgreSQL
```

API-слой принимает запрос и отдаёт его в сервис. Сервис работает через Unit of Work и репозитории, а не напрямую с SQLAlchemy session.

## Синхронизация

```text
Client sync queue
  -> POST /sync
  -> SyncService
  -> operation validation
  -> repository updates
  -> SyncResponse
  -> local merge
```

Клиент отправляет накопленные операции и дату последней синхронизации. Сервер возвращает принятые операции, удаления и обновлённое состояние. Клиент применяет ответ в локальную базу.
