# Kotlin/JS-мост: k2ts

Изменено: 02.06.2026

`k2ts` связывает Kotlin shared layer с React/TypeScript приложением. Через него frontend получает доступ к `RootComponent`, состоянию экранов и Kotlin-логике.

## Что делает модуль

Модуль собирает Kotlin/JS библиотеку (`k2ts`) и генерирует TypeScript-описания.

## Зависимости

`k2ts` подключает те shared-модули, которые нужны при старте приложения:

- `shared:root:presentation`
- `shared:sync:domain`
- `shared:sync:data`
- `shared:settings:domain`
- `shared:core`
- `shared:utils:common`
- Koin

## Точка входа

Главная функция - `initApp()`.

```kotlin
@JsExport
fun initApp()
```

При запуске она:

1. настраивает логирование;
2. регистрирует Service Worker;
3. инициализирует Koin;
4. применяет сохранённую тему;
5. запускает наблюдение за сетью;
6. запускает первичную синхронизацию;
7. создаёт `RootComponent`;
8. возвращает компонент в JavaScript.

> TODO: убрать блок ui-потока при запуске

## Как React получает Kotlin-состояние

Компоненты экспортируются через `@JsExport`. Для передачи состояния и навигации используются interop-обёртки:

- `JsValue<T>`
- `JsChildStack<T>`
- `JsChildSlot<T>`

```text
RootComponent
  -> jsStack
  -> React UI
```

```text
StateFlow / Value
  -> JsValue
  -> TypeScript subscription
```

## Главное правило

React не должен пересобирать бизнес-логику на своей стороне. Если данные или навигация уже живут в Kotlin shared layer, TypeScript должен получать их через `k2ts` и interop API.
