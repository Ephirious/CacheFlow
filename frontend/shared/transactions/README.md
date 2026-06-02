# Transactions Module

## Назначение

Основной бизнес-модуль приложения.

Отвечает за:

- хранение транзакций;
- создание и обновление операций;
- удаление операций;
- фильтрацию списка транзакций;
- отображение summary-информации;
- интеграцию с локальной БД;
- подготовку данных для синхронизации.

## Структура

```text
transactions/
├── data/
├── domain/
└── presentation/
```

## Domain

Подтвержденные usecases:

- GetTransactionUseCase
- GetTransactionsFlowUseCase
- GetFilteredTransactionsFlowUseCase
- UpsertTransactionUseCase
- DeleteTransactionUseCase

### TransactionsRepository

Подтвержденные операции:

- getTransactionsFlow(...)
- getFilteredTransactionsFlow(...)
- upsertTransaction(...)
- deleteTransaction(...)
- selectTransactionById(...)

Также содержит служебные методы миграции/инициализации БД.

## Data

Подтвержденные элементы:

- TransactionsRepositoryImpl
- TransactionsDataModule
- SQLDelight schema (`Transactions.sq`)

Data слой отвечает за локальное хранение и реактивные выборки данных.

## Presentation

Подтвержденные компоненты:

- TransactionsComponent
- TransactionsContainer
- FiltersContainer
- SummaryContainer
- MainComponent

### TransactionsComponent

Экспортируется в JS через `@JsExport`.

Предоставляет:

- observable state;
- action subscription;
- открытие/закрытие фильтров;
- отправку intents.

### Filters

Фильтры реализованы через Decompose ChildSlot и отдельный FiltersComponent.

## Связанные модели

Подтвержденные модели:

- Transaction
- TransactionFilters

## Зависимости

Модуль используется редакторами, статистикой и системой синхронизации.
