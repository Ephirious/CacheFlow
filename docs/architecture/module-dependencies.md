# Module Dependencies

Документ фиксирует зависимости между frontend shared-модулями и backend слоями.

## Frontend module graph

```text
webApp
  ↓
k2ts
  ↓
root:presentation
  ├── auth:presentation
  ├── transactions:presentation
  ├── settings:presentation
  ├── editors:presentation
  └── stats:presentation

feature:presentation
  ↓
feature:domain
  ↓
feature:data
  ↓
core / utils / core-validation
```

## Gradle modules

`frontend/settings.gradle.kts` подключает следующие группы модулей:

```text
:k2ts
:ksp-processor

:shared:core-validation
:shared:core
:shared:root:presentation

:shared:transactions:data
:shared:transactions:domain
:shared:transactions:presentation

:shared:stats:presentation

:shared:settings:data
:shared:settings:domain
:shared:settings:presentation

:shared:editors:data
:shared:editors:domain
:shared:editors:presentation

:shared:sync:data
:shared:sync:domain

:shared:auth:data
:shared:auth:domain
:shared:auth:presentation

:shared:utils:common
:shared:utils:pure
```

## Dependency rules

### Presentation layer

Presentation modules may depend on:

- own domain module;
- other presentation modules only through navigation/composition contracts;
- `core`, `utils:common`, `utils:pure`;
- Decompose and FlowMVI APIs.

Presentation modules must not directly depend on SQLDelight, Settings storage or backend DTO implementations.

### Domain layer

Domain modules contain:

- usecases;
- repository interfaces;
- domain models;
- validation orchestration.

Domain modules should not know concrete storage/network implementations.

### Data layer

Data modules contain:

- repository implementations;
- local data sources;
- SQLDelight access;
- Multiplatform Settings access;
- Ktor integration;
- sync queue integration.

## Cross-feature dependencies

Some modules intentionally depend on other domain modules:

- `auth:domain` uses sync and transaction domain contracts for authenticated app initialization.
- `editors:domain` works with account/category entities used by transactions and sync.
- `transactions:presentation` composes filters, summary and transaction list features.
- `sync:data` depends on concrete repositories and backend API contracts.

## Backend dependency graph

```text
api routers
  ↓
services
  ↓
unit of work
  ↓
repositories
  ↓
SQLAlchemy models
  ↓
PostgreSQL
```

## Backend repository rules

- New database access logic should be added through repositories.
- Services should use `UnitOfWork`, not raw sessions.
- Service methods should return typed `Result` objects where this pattern is already used.
- Alembic migrations must reflect model changes.
