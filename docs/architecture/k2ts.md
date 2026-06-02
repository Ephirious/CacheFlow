# k2ts

`k2ts` — Kotlin/JS bridge между shared Kotlin-кодом и React/TypeScript frontend.

## Назначение

Модуль собирает Kotlin Multiplatform код в JavaScript библиотеку и генерирует TypeScript определения.

## Kotlin/JS configuration

Подтвержденная конфигурация:

```text
js(IR)
browser()
binaries.library()
generateTypeScriptDefinitions()
useEsModules()
```

Output module name:

```text
k2ts
```

## Зависимости

Подтвержденные зависимости:

- shared.root.presentation
- shared.sync.domain
- shared.sync.data
- shared.settings.domain
- shared.core
- shared.utils.common
- Koin

## initApp

Главная точка входа:

```kotlin
@JsExport
fun initApp()
```

Функция:

1. настраивает logging;
2. регистрирует service worker;
3. инициализирует Koin;
4. применяет сохраненную тему;
5. запускает network observer;
6. запускает первичную синхронизацию;
7. создает RootComponent;
8. возвращает его в JavaScript.

## JS-facing API

Компоненты экспортируются через:

- `@JsExport`;
- `JsValue<T>`;
- `JsChildStack<T>`;
- `JsChildSlot<T>`.

## Navigation bridge

TypeScript получает доступ к Decompose navigation через:

```text
RootComponent
   ↓
jsStack
   ↓
React UI
```

## State bridge

State передается через observable wrappers:

```text
StateFlow / Value
   ↓
JsValue
   ↓
TypeScript subscription
```

## Архитектурная роль

`k2ts` является единственной официальной точкой доступа React frontend к shared Kotlin бизнес-логике.
