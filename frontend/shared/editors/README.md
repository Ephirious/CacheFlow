# Модуль editors

Изменено: 02.06.2026

`editors` отвечает за данные, которые потом используются в транзакциях: счета и категории. Здесь находятся сценарии создания, редактирования и мягкого удаления этих сущностей.

## Структура

```text
editors/
├── data/
├── domain/
└── presentation/
```

## Счета

Для счетов есть отдельный контракт `AccountsRepository`. Он отдаёт `Flow<List<Account>>`, умеет получать счёт по id, создавать, обновлять, мягко удалять и делать upsert.

Основные use case'ы:

- `CreateAccountUseCase`
- `EditAccountUseCase`
- `DeleteAccountUseCase`
- `GetAccountsFlowUseCase`

## Категории

Для категорий используется `CategoriesRepository`. Логика похожа на счета: потоковый список, получение по id, создание, обновление, мягкое удаление и upsert.

Основные use case'ы:

- `CreateCategoryUseCase`
- `EditCategoryUseCase`
- `DeleteCategoryUseCase`
- `GetCategoriesFlowUseCase`
- `GetCategoryByIdUseCase`

`Category` использует `CategoryType` из `dbEnums`.

## UI-слой

В presentation-слое есть контейнеры для форм:

- `CreateAccountContainer`
- `EditAccountContainer`
- `CreateCategoryContainer`
- `EditCategoryContainer`

Модуль часто используется из настроек, потому что именно там пользователь управляет счетами и категориями.
