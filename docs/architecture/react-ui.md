# React UI

Изменено: 02.06.2026

React-часть проекта находится в `frontend/webApp`. Она отвечает за отрисовку интерфейса, обработку пользовательских действий и подключение к Kotlin shared layer через `k2ts`.

Главная особенность проекта: React здесь не является основным владельцем бизнес-состояния. Состояние экранов, навигация и большая часть логики приходят из Kotlin-компонентов.

## Общий поток

```text
React UI
  -> k2ts
  -> RootComponent
  -> Decompose
  -> FlowMVI
  -> UseCase
  -> Repository
```

React получает `RootComponent` при запуске приложения:

```text
index.html
  -> src/index.tsx
  -> initApp()
  -> RootScreen
```

`initApp()` импортируется из `k2ts`, инициализирует Kotlin-слой и возвращает корневой компонент.

## Что делает React

React отвечает за:

- верстку;
- отображение состояния;
- обработку кликов и ввода;
- вызов методов компонентов;
- подписку на состояние из Kotlin.

## Что приходит из Kotlin

Из Kotlin shared layer приходят:

- корневой компонент приложения;
- состояние экранов;
- навигационные структуры;
- методы отправки intent'ов;
- одноразовые actions.

Для связи используются JS-обертки из `utils`, например `JsValue`, `JsChildStack` и `JsChildSlot`.

## Где находится UI-код

Основная web-часть:

```text
frontend/webApp/
├── index.html
├── vite.config.ts
├── package.json
└── src/
    ├── components/
    ├── styles/
    └── workers/
```

`src/index.tsx` - точка входа React-приложения.

`components/layout/Root.tsx` получает `RootComponent` и строит верхний уровень UI.
