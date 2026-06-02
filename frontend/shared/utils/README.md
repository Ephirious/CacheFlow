# Utils Module

## Назначение

`utils` содержит общие переиспользуемые утилиты и инфраструктурные abstractions.

Структура:

```text
utils/
├── common/
└── pure/
```

## utils:pure

Нижний слой зависимостей.

Подтвержденные зависимости:

- kotlinx.serialization.json

Web-specific зависимости:

- npm: big.js
- npm: graphemer

Модуль может использоваться любым другим shared-модулем без зависимости на UI или presentation stack.

## utils:common

Подтвержденные зависимости:

- Decompose Core
- FlowMVI Core
- FlowMVI Essenty
- Kotlin Coroutines
- utils:pure

## Подтвержденные обязанности

По найденным использованиям модуль содержит:

- JS interop wrappers (`JsValue`, `JsChildStack`, `JsChildSlot`);
- helpers для Decompose integration;
- coroutine utilities;
- URL/navigation helpers;
- presentation helpers;
- logging utilities.

## Архитектурная роль

`utils` является фундаментом shared layer.

Зависимость на `utils:pure` допускается практически во всех модулях.

Зависимость на `utils:common` используется presentation и startup слоями, которым необходимы Decompose/FlowMVI abstractions.
