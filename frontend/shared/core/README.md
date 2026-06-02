# Модуль core

Изменено: 02.06.2026

`shared:core` - инфраструктурный модуль frontend-части. Здесь собраны вещи, которые нужны многим data-модулям: бд, сетевой клиент, настройки и DI.

## Что подключает модуль

Через `commonMain` подключаются:
- Koin Core
- Ktor Client
- Multiplatform Settings
- `shared:utils:pure`
- `shared:auth:domain`

Для JS-таргета подключён SQLDelight Web Worker driver.

## SQLDelight

`core` создаёт общую SQLDelight-базу `Database`:

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

`generateAsync.set(true)`: работа с базой строится вокруг асинхронного API.
