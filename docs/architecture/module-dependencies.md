# Структура модулей и зависимости

Изменено: 02.06.2026

Этот документ описывает, как собран проект и какие слои за что отвечают.

## Общая картина

```text
webApp
  -> k2ts
  -> root:presentation

root:presentation
  -> auth:presentation
  -> transactions:presentation
  -> settings:presentation
  -> editors:presentation
  -> stats:presentation
```

Типичный feature-модуль выглядит так:

```text
presentation
  -> domain
  -> data
  -> core / utils / core-validation
```

## Какие модули есть в проекте

Из `frontend/settings.gradle.kts` подключаются:

```text
:k2ts
:ksp-processor

:shared:core-validation
:shared:core
:shared:root:presentation

:shared:transactions:*
:shared:stats:*
:shared:settings:*
:shared:editors:*
:shared:sync:*
:shared:auth:*

:shared:utils:common
:shared:utils:pure
```

## Что лежит в слоях

### Presentation

Здесь находятся компоненты, контейнеры, состояние экранов и навигация.

Presentation-слой работает через Decompose и FlowMVI и не должен напрямую обращаться к SQLDelight или сетевым клиентам.

### Domain

Здесь лежат:

- use case'ы;
- интерфейсы репозиториев;
- доменные модели.

Domain знает, какие данные нужны приложению, но не знает, где именно они хранятся.

### Data

Data-слой связывает доменную модель с реальной реализацией.

Здесь находятся:

- реализации репозиториев;
- SQLDelight;
- Settings;
- Ktor;
- интеграция с очередью синхронизации.

## Несколько важных связей

Стоит отметить, что некоторые **доменные** модули связаны напрямую:

- `auth:domain` зависит от `sync:domain` и `transactions:domain`. Авторизация влияет не только на токены, но и на пользовательское состояние: после входа нужна синхронизация, а при выходе - очистка локальных данных.

- `editors` работает со счетами и категориями. **Эти же сущности используются в других модулях:** транзакции, статистика, настройки, синхронизация.

- `sync:data` использует репозитории счетов, категорий и транзакций, чтобы применить ответ сервера к локальной базе.

> `stats` не хранит данные сам, а подписывается на существующие потоки из других модулей.

## Backend

```text
API
  -> Services
  -> UnitOfWork
  -> Repositories
  -> SQLAlchemy Models
  -> PostgreSQL
```

Бизнес-логика находится в сервисах, а доступ к базе происходит через репозитории и UnitOfWork.
