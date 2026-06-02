# Модуль settings

Изменено: 02.06.2026

## Структура

```text
settings/
├── data/
├── domain/
└── presentation/
```

## Тема приложения

Сейчас в domain-слое есть `GetThemeUseCase` и контракт `SettingsRepository`.

Репозиторий умеет:

- `setTheme(theme)`
- `getTheme()`

Для темы используется модель `AppTheme`.

## Реализация и UI

В `data` находятся:

- `SettingsRepositoryImpl`
- `SettingsDataModule`

В `presentation` используются:

- `SettingsContainer`
- `RealSettingsComponent`
- `AccountsComponent`

`settings` не только хранит параметры приложения, но и собирает часть экранов управления. Например, через него пользователь попадает к настройкам счетов.
