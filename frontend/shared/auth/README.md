# Модуль auth

Изменено: 02.06.2026

`auth` отвечает за вход пользователя в приложение и за всё, что нужно вокруг сессии: регистрацию, подтверждение регистрации, логин, logout, получение профиля и очистку локальных данных при выходе.

## Что есть в модуле

```text
auth/
├── data/
├── domain/
└── presentation/
```

`domain` задаёт контракт авторизации через `AuthRepository`. В нём есть операции `register`, `verifyRegistration`, `resendVerificationCode`, `login`, `logout`, `getProfile` и `clearAllTables`.

Основные use case'ы:

- `LoginUseCase`
- `RegisterUseCase`
- `LogoutUseCase`
- `GetProfileUseCase`
- `ResendVerificationCodeUseCase`

`data` содержит реализацию работы с авторизацией:

- `AuthRepositoryImpl`
- `KtorAuthPluginImpl`
- `LogoutDataInternalUseCase`
- `AuthDataModule`

`presentation` сейчас представлен контейнерами для логина и регистрации:

- `LoginContainer`
- `RegistrationContainer`

## Зависимости

По Gradle-модулю `auth:domain` видно, что авторизация связана с:

- `shared:sync:domain`
- `shared:transactions:domain`
- `shared:core-validation`
- Ktor Client

Это важно: logout не ограничивается только удалением токена. Модуль также знает о локальных данных, которые надо сбросить при смене пользователя.
