# Editors Module

## Назначение

Модуль отвечает за пользовательские сценарии создания, редактирования и удаления справочников, которые используются в финансовых операциях.

Подтвержденные области:

- счета (`Account`);
- категории (`Category`).

## Структура

```text
editors/
├── data/
├── domain/
└── presentation/
```

## Domain

### Account usecases

Подтвержденные usecases:

- CreateAccountUseCase
- EditAccountUseCase
- DeleteAccountUseCase
- GetAccountsFlowUseCase

### Category usecases

Подтвержденные usecases:

- CreateCategoryUseCase
- EditCategoryUseCase
- DeleteCategoryUseCase
- GetCategoriesFlowUseCase
- GetCategoryByIdUseCase

## Repositories

### AccountsRepository

Подтвержденные операции:

- getAccountsFlow(onlyActive)
- getAccountById(id)
- insertAccount(name, stringAmount, color)
- updateAccount(id, name, color)
- softDelete(id)
- softDeleteAccount(id)
- upsertAccount(id, name, color, stringAmount)

### CategoriesRepository

Подтвержденные операции:

- getCategoriesFlow(onlyActive)
- getCategoryById(id)
- insertCategory(name, emoji, type)
- updateCategory(id, name, emoji)
- softDelete(id)
- softDeleteCategory(id)
- upsertCategory(id, name, emoji, type)

## Data

Подтвержденные элементы:

- EditorsDataModule
- CategoriesRepositoryImpl

Data слой реализует repository interfaces и работает с локальным хранилищем.

## Presentation

Подтвержденные MVI контейнеры и компоненты:

- CreateAccountContainer
- EditAccountContainer
- CreateCategoryContainer
- EditCategoryContainer

Модуль используется страницами настроек и экранами редактирования справочников.

## Модели

Подтвержденные domain-модели:

- Account
- Category

`Category` использует `CategoryType` из `dbEnums`.

## Инварианты

- Удаление справочников реализуется через soft-delete операции.
- Потоковые методы возвращают `Flow<List<...>>`, что позволяет UI реактивно обновляться при изменениях в локальном хранилище.
