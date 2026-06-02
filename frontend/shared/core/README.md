# Core Module

## Назначение

`shared:core` — инфраструктурный shared-модуль frontend-части.

Подтвержденная ответственность:

- общая конфигурация локальной базы данных;
- SQLDelight database generation;
- подключение Ktor client dependencies;
- подключение Multiplatform Settings;
- интеграция Koin;
- зависимость от auth domain для установки auth feature / token refresh.

## Gradle-конфигурация

Модуль использует плагины:

```text
shared
sqldelight
```

## SQLDelight

Подтвержденная конфигурация:

```kotlin
sqldelight {
    databases {
        create("Database") {
            packageName = Config.namespace + ".db"
            generateAsync.set(true)
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:...")
        }
    }
}
```

## Dependencies

`commonMain`:

- Koin Core;
- Ktor Client bundle;
- Multiplatform Settings bundle;
- `shared:utils:pure`;
- `shared:auth:domain`.

`jsMain`:

- SQLDelight Web Worker driver.

## Архитектурная роль

`core` предоставляет базовую инфраструктуру, которую используют data-модули и startup-код приложения.

Модуль не должен содержать feature-specific бизнес-логику.
