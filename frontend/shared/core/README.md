# Модуль core

Изменено: 02.06.2026

`shared:core` - инфраструктурный модуль frontend-части. Здесь собраны вещи, которые нужны многим data-модулям: база, сетевой клиент, настройки и DI.

## Что подключает модуль

В Gradle у модуля включены плагины:

```text
shared
sqldelight
```

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

`generateAsync.set(true)` важен для Kotlin/JS: работа с базой строится вокруг асинхронного API.

## Как использовать

Feature-модули не должны заново настраивать базу, Ktor или Settings. Если модулю нужна инфраструктура хранения или сети, он должен подключаться к уже собранным абстракциям из `core` и своих data-модулей.
