# Модуль root:presentation

Изменено: 02.06.2026

`root:presentation` собирает верхний уровень навигации приложения. Это место, где Decompose связывает основные экраны в один стек.

## Основные классы

- `RootComponent`
- `RealRootComponent`
- `RootConfig`
- `RootChild`
- `RootOutput`
- `PersistentRootComponents`

## Как устроена навигация

`RealRootComponent` использует `StackNavigation<RootConfig>` и `childStack(...)`.

В корневом стеке сейчас есть три основных направления:

- `MainChild`
- `StatsChild`
- `SettingsChild`

Начальный стек выбирается по deep link URL:

```text
/             -> Main
/stats        -> Main + Stats
/settings/... -> Main + Settings
```

Для web-навигации используется `childStackWebNavigation(...)`. Корневой стек передаётся в JavaScript через `JsValue<JsChildStack<RootChild>>` и `asJsStack()`.

## Что важно помнить

Этот модуль не должен содержать бизнес-логику экранов. Его задача - собрать компоненты, настроить стартовый стек и связать Kotlin-навигацию с web-историей браузера.
