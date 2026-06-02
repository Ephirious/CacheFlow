# Модуль transactions

Изменено: 02.06.2026

`transactions` - основной модуль для работы с операциями. Через него главный экран получает список транзакций, применяет фильтры, удаляет записи и сохраняет изменения в локальную базу.

## Структура

```text
transactions/
├── data/
├── domain/
└── presentation/
```

## Основные части

В `domain` лежат сценарии чтения и изменения транзакций:

- `GetTransactionUseCase`
- `GetTransactionsFlowUseCase`
- `GetFilteredTransactionsFlowUseCase`
- `UpsertTransactionUseCase`
- `DeleteTransactionUseCase`

Основной контракт - `TransactionsRepository`. Он отдаёт список транзакций, список с фильтрами, умеет делать upsert, delete и выбирать транзакцию по id.

В `data` находятся `TransactionsRepositoryImpl`, `TransactionsDataModule` и SQLDelight-схема `Transactions.sq`.

В `presentation` собраны компоненты главного сценария:

- `MainComponent`
- `TransactionsComponent`
- `TransactionsContainer`
- `FiltersContainer`
- `SummaryContainer`

`TransactionsComponent` экспортируется в JS. React получает из него состояние, отправляет intents и открывает фильтры. Фильтры сделаны отдельным Decompose slot-компонентом.

## Связи
Модуль связан со статистикой, редакторами и синхронизацией. Поэтому изменения в транзакциях лучше проверять вместе с `editors`, `stats` и `sync`.
