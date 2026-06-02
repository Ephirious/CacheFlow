# Локальная разработка

Изменено: 02.06.2026

## Что понадобится

- Java 17
- Node.js
- npm

Для работы с Kotlin-кодом удобно использовать IntelliJ IDEA с Kotlin Multiplatform Plugin.

## Сборка Kotlin/JS

```bash
cd frontend
./gradlew buildK2ts
```

## Установка зависимостей

```bash
npm install
```

## Запуск frontend
Обычный запуск
```bash
npm run start
```

Для проверки поведения, близкого к production (Service Worker и offline-first):

```bash
npm run build
npm run preview
```

## Если возникают проблемы с Kotlin/JS

В проекте используется `kotlin-js-store`. Если Gradle начинает жаловаться на lock-файлы или зависимости JavaScript, сначала проверьте состояние этой директории и актуальность npm-зависимостей.
