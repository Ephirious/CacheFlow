Это Kotlin Multiplatform проект с UI на TypeScript

* /shared: здесь код пошарен между всеми таргетами в проекте.

* [/webApp](./webApp) содержит веб React приложение. Оно использует Kotlin/JS библиотеку сбилженную
  через [k2ts](./k2ts) модуль.

* [k2ts](./k2ts) содержит настройки для билда библиотеки под JS таргет

### Билд и запуск
Чтобы сбилдить и запустить веб-приложение, следуйте следующим шагам:
1. Скачайте [Node.js](https://nodejs.org/en/download) (содержит `npm`)
2. Сбилдите Kotlin/JS код (модуль k2ts):
   - на macOS/Linux
     ```shell
     ./gradlew :k2ts:jsBrowserProductionLibraryDistribution
     ```
   - на Windows
     ```shell
     .\gradlew.bat :k2ts:jsBrowserProductionLibraryDistribution
     ```
3. Запустите приложение
   ```shell
   npm install
   npm run start
   ```