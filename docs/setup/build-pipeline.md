# Сборка frontend

Изменено: 02.06.2026

Сборка frontend состоит из двух частей.

Сначала собирается Kotlin/JS библиотека:

```bash
./gradlew buildK2ts
```

После этого React-приложение использует сгенерированные артефакты.

## Разработка

```bash
npm run start
```

Используется для повседневной работы над UI.

## Production build

```bash
npm run build
```

Сборка попадает в директорию `dist`.

## Проверка production-режима

```bash
npm run preview
```

В отличие от режима разработки здесь можно проверить работу Service Worker и offline-first механик.
