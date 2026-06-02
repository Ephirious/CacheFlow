# Модуль stats

Изменено: 02.06.2026

`stats` строит статистику по счетам и транзакциям. Собственной базы данных у него нет - он работает поверх уже существующих данных.

## Структура

```text
stats/
└── presentation/
```

На текущий момент модуль состоит только из presentation-слоя.

## Основные классы

- `StatsComponent`
- `RealStatsComponent`
- `StatsCalculator`
- `StatsState`
- `StatsIntent`

## Откуда берутся данные

Статистика использует:

- `GetAccountsFlowUseCase`
- `GetTransactionsFlowUseCase`

Потоки объединяются через `combine(...)`, после чего пересчитываются агрегаты.

Состояние экрана хранит выбранный счёт, период, метрику и данные, необходимые для построения аналитики.

Поддерживаются действия:

- `SelectAccount`
- `SelectMetric`
- `SelectPresetPeriod`
- `SelectCustomPeriod`

## Интеграция с UI

`StatsComponent` экспортируется в JavaScript через `@JsExport` и предоставляет React-приложению состояние (`jsState`) и метод отправки intent'ов.
