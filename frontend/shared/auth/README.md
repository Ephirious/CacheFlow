# Auth Module

## Назначение

Модуль отвечает за:

- регистрацию пользователя;
- подтверждение регистрации;
- повторную отправку кода подтверждения;
- авторизацию;
- выход из системы;
- получение профиля пользователя;
- интеграцию Ktor auth plugin;
- очистку локальных данных при logout.

## Структура

```text
auth/
├── data/
├── domain/
└── presentation/
```

## Domain

Подтвержденные usecases:

- LoginUseCase
- RegisterUseCase
- LogoutUseCase
- GetProfileUseCase
- ResendVerificationCodeUseCase

### AuthRepository

Domain слой содержит интерфейс `AuthRepository`.

Подтвержденные операции:

- register(...)
- verifyRegistration(...)
- resendVerificationCode(...)
- login(...)
- logout()
- getProfile()
- clearAllTables()

## Data

Подтвержденные реализации:

- AuthRepositoryImpl
- KtorAuthPluginImpl
- LogoutDataInternalUseCase
- AuthDataModule

Data слой реализует сетевое взаимодействие и интеграцию с клиентом авторизации.

## Presentation

Подтвержденные MVI контейнеры:

- LoginContainer
- RegistrationContainer

Presentation слой экспортирует состояние и действия в JS через общие interop-механизмы проекта.

## Зависимости

Согласно Gradle-конфигурации:

- shared.sync.domain
- shared.transactions.domain
- shared.coreValidation
- Ktor Client

## Ответственность модуля

Auth-модуль является входной точкой приложения и предоставляет данные о текущем пользователе для остальных feature-модулей.
