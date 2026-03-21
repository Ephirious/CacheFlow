Это Kotlin Multiplatform проект с UI на TypeScript

* [/shared](): здесь пошарен код между всеми таргетами в проекте.

* [/webApp](./webApp) содержит React-приложение. Оно использует Kotlin/JS библиотеку сбилженную
  через [/k2ts](./k2ts) модуль.

* [/k2ts](./k2ts) содержит настройки для билда библиотеки под JS таргет, занимается инициализацией

* [/k2ts-service](./k2ts-service) – ServiceWorker, написанный на Kotlin

### Внутренняя документация
- [Взаимодействие WebApp и ServiceWorker. (кэширование, пуши, синхронизация)](docs/WebApp_ServiceWorker.md)

### Билд и запуск

Чтобы сбилдить и запустить веб-приложение, следуйте следующим шагам:

1. Скачайте IntelliJ IDEA, установите плагин Kotlin Multiplatform
2. Откройте этот проект (в IDEA) и установите JAVA 17
3. Скачайте [Node.js](https://nodejs.org/en/download) (содержит `npm`)
4. Сбилдите Kotlin/JS код (модули k2ts и k2ts-service):
    - на macOS/Linux
      ```shell
      ./gradlew buildAll
      ```
    - на Windows
      ```shell
      .\gradlew.bat buildAll
      ```
5. Запустите приложение
   ```shell
   npm install
   npm run buildStart
   ```

> [!NOTE]
> **Про сборку**
> - **Kotlin:** `buildAll` билдит клиент и сервис. Можно разделить с помощью `buildClient` и `buildService`
> - **TS** :`buildStart` запускает по очереди `build`, а потом `start`. Это нужно, чтобы Vite подхватил код ServiceWorker в папку `public`.\
> Если ServiceWorker не был изменён можно обойтись обычным `start` _(но при первом запуске обязательно нужно сбилдить!!)_
>
> HotReload работает только для k2ts, т.к. k2ts-service добавляется в виде статического файла (для этого и нужен build в vite)
>
> Не забывайте прописывать `npm install`!! Если при компиляции Kotlin жалуется на yarn.lock – сносите папку `kotlin-js-store` _(не лучшая практика в проде, но...)_
