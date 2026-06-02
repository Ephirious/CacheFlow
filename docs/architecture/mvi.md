# MVI

Frontend shared layer использует FlowMVI для presentation state management.

## Подтвержденные элементы

В `TransactionsComponent` используются:

- `Store<TransactionsState, TransactionsIntent, TransactionsAction>`;
- `TransactionsContainer` как factory для retained store;
- `intent(intent: TransactionsIntent)` для передачи событий из UI;
- `subscribeActions(onAction)` для подписки на one-shot actions;
- `jsState` для передачи state в TypeScript/React.

## Общий контракт

```text
Intent  # входящее действие от UI
State   # состояние экрана
Action  # одноразовое событие: navigation, toast, external effect
Store   # FlowMVI store/container
```

## Типичный поток

```text
React UI
   ↓
component.intent(...)
   ↓
FlowMVI container
   ↓
UseCase
   ↓
Repository
   ↓
State update
   ↓
jsState subscription
   ↓
React render
```

## JS interop

Компоненты, экспортируемые в TypeScript, используют `@JsExport` и JS-friendly wrappers:

- `JsValue<T>` для observable state;
- `JsChildSlot<T>` для Decompose child slots;
- `jsStateSubscribe(...)` для state subscription;
- `subscribeActions(...)` для actions collection.

## Правила для новых экранов

1. UI должен отправлять intent, а не вызывать repositories напрямую.
2. Business logic должна оставаться в usecases.
3. Container/store должен управлять state и action flow.
4. Одноразовые события не должны храниться в persistent state.
5. JS-facing API должен быть явно экспортирован через `@JsExport`, если он вызывается из TypeScript.

## Пример lifecycle

```text
Component created
   ↓
retainedStore(factory = container)
   ↓
UI subscribes to jsState
   ↓
UI sends intents
   ↓
Store emits state/actions
   ↓
Component disposed with Decompose lifecycle
```
