Это Kotlin Multiplatform проект с UI на TypeScript

* [/shared](): здесь пошарен код между всеми таргетами в проекте.

* [/webApp](./webApp) содержит React-приложение. Оно использует Kotlin/JS библиотеку сбилженную
  через [/k2ts](./k2ts) модуль.

* [/k2ts](./k2ts) содержит настройки для билда библиотеки под JS таргет, занимается инициализацией


### Внутренняя документация

- ~[Взаимодействие WebApp и ServiceWorker. (кэширование, пуши, синхронизация)](docs/WebApp_ServiceWorker.md)~ Устарело, т.к. ServiceWorker был переписан.

### Билд и запуск

Чтобы сбилдить и запустить веб-приложение, следуйте следующим шагам:

1. Скачайте IntelliJ IDEA, установите плагин Kotlin Multiplatform
2. Откройте этот проект (в IDEA) и установите JAVA 17
3. Скачайте [Node.js](https://nodejs.org/en/download) (содержит `npm`)
4. Сбилдите Kotlin/JS код (модуль k2ts):
    - на macOS/Linux
      ```shell
      ./gradlew buildK2ts
      ```
    - на Windows
      ```shell
      .\gradlew.bat buildK2ts
      ```
5. Запустите приложение
   ```shell
   npm install
   npm run start
   ```

> [!NOTE]
> **Про сборку**
> - **Kotlin:** `buildK2ts` билдит JS библиотеку Kotlin логики с биндингами на TS _(Production для лучшей стабильности)_
> - **TS** : `npm run start`, `npm run build`, `npm run preview`
>    - `start` – используется для реактивной разработки, т.е. поддерживает hotReload основной логики (`k2ts`) и других файлов _(UI)_.\
     Не поддерживает offline-first, т.к. кэширование отключено. _(порт 8080)_
>    - `build` – билдит проект в папочку `dist`
>    - `preview` – выполняет `build`, а потом хостится, имитируя поведение настоящего прода. Можно устанавливать как PWA. _(порт 4173)_

> Не забывайте прописывать `npm install`!! Если при компиляции Kotlin жалуется на yarn.lock – сносите папку
`kotlin-js-store` _(не лучшая практика в проде, но...)_
