# Settings Module

## Назначение

Модуль отвечает за настройки приложения и страницы управления конфигурацией.

## Структура

```text
settings/
├── data/
├── domain/
└── presentation/
```

## Domain

Подтвержденные usecases:

- GetThemeUseCase

### SettingsRepository

Подтвержденные операции:

- setTheme(theme)
- getTheme()

Для темы используется модель `AppTheme`.

## Data

Подтвержденные элементы:

- SettingsRepositoryImpl
- SettingsDataModule

Data слой отвечает за хранение пользовательских настроек.

## Presentation

Подтвержденные компоненты:

- SettingsContainer
- RealSettingsComponent
- AccountsComponent

Модуль агрегирует страницы настроек и предоставляет интерфейсы управления справочниками приложения.

## Ответственность

- управление темой приложения;
- конфигурация пользовательского окружения;
- интеграция со страницами счетов и связанных настроек.
