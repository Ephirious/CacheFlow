# Stats Module

## Назначение

Модуль отвечает за построение статистики и аналитических представлений на основе счетов и транзакций.

## Структура

```text
stats/
└── presentation/
```

На текущий момент в репозитории подтвержден только presentation layer.

## Подтвержденные классы

- StatsComponent
- RealStatsComponent
- StatsCalculator
- StatsState
- StatsIntent

## Источники данных

Модуль использует:

- GetAccountsFlowUseCase
- GetTransactionsFlowUseCase

Данные объединяются через `combine(...)`.

## State model

Компонент хранит:

- список счетов;
- список транзакций;
- выбранный счет;
- выбранный период;
- выбранную метрику.

## Поддерживаемые intents

Подтверждены:

- SelectAccount
- SelectMetric
- SelectPresetPeriod
- SelectCustomPeriod

## JS interop

`StatsComponent` экспортируется через `@JsExport`.

Предоставляет:

- observable state (`jsState`);
- метод `intent(...)`.

## Архитектурная роль

Stats не владеет собственным хранилищем данных.

Модуль является вычислительным слоем, который агрегирует данные из accounts и transactions и строит аналитическое состояние для UI.
