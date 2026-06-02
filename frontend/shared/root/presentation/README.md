# Root Presentation Module

## Назначение

`root:presentation` — composition root frontend shared layer.

Модуль отвечает за:

- создание корневого Decompose component tree;
- управление root navigation stack;
- обработку deep links;
- web navigation integration;
- маршрутизацию между основными разделами приложения.

## Подтвержденные классы

- RealRootComponent
- RootComponent
- RootConfig
- RootChild
- RootOutput
- PersistentRootComponents

## Navigation stack

`RealRootComponent` использует:

- `StackNavigation<RootConfig>`;
- `childStack(...)`;
- `ChildStack<RootConfig, RootChild>`;
- `childStackWebNavigation(...)`;
- `WebNavigation`.

## Root children

Подтвержденные root children:

- MainChild
- StatsChild
- SettingsChild

## Initial stack

Начальный стек выбирается по deep link URL.

Подтвержденное поведение:

```text
/             → Main
/stats        → Main + Stats
/settings/... → Main + Settings
```

## JS interop

Root stack экспортируется в TypeScript через:

- `JsValue<JsChildStack<RootChild>>`;
- `asJsStack()`.

## Ответственность модуля

Root-модуль не реализует бизнес-логику экранов. Он связывает основные feature-компоненты и отвечает за навигационную композицию приложения.
