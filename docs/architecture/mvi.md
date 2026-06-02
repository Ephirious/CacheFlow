# MVI в frontend shared layer

Изменено: 02.06.2026

В shared-слое используется FlowMVI. UI отправляет события в Kotlin-компоненты, а компоненты уже передают их в store/container.

Хороший пример - `TransactionsComponent`. В нём видно основной контракт:

- `TransactionsState` - состояние экрана
- `TransactionsIntent` - событие от UI
- `TransactionsAction` - одноразовое действие
- `TransactionsContainer` - контейнер с логикой обработки
- `Store<TransactionsState, TransactionsIntent, TransactionsAction>` - FlowMVI store

## Как проходит событие

```text
React UI
  -> component.intent(...)
  -> FlowMVI container
  -> UseCase
  -> Repository
  -> State update
  -> jsState subscription
  -> React render
```

React не должен напрямую вызывать репозитории. Его задача - отправить intent и подписаться на состояние.

## Состояние и actions

State хранит то, что нужно экрану для отрисовки. Actions подходят для одноразовых событий: навигации, toast-сообщений и похожих эффектов.

Эти вещи лучше не смешивать. Если одноразовое событие положить в persistent state, UI может обработать его повторно после пересоздания компонента.

## Интеграция с JavaScript

Компоненты, которые вызываются из TypeScript, используют `@JsExport` и обёртки из utils:

- `JsValue<T>`
- `JsChildSlot<T>`
- `jsStateSubscribe(...)`
- `subscribeActions(...)`

Через это React получает текущее состояние, подписывается на изменения и отправляет intents обратно в Kotlin.

## Для новых экранов

Новый экран стоит строить по тому же принципу:

1. описать State, Intent и Action;
2. вынести бизнес-операции в use case'ы;
3. собрать FlowMVI container;
4. экспортировать компонент в JS только через понятный API;
5. не давать React доступ к репозиториям напрямую.
