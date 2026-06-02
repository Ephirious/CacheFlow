# Decompose

Frontend shared layer использует Decompose для component lifecycle и навигационных структур.

## Подтвержденные элементы

В `TransactionsComponent` используются:

- `ComponentContext` как базовый lifecycle/navigation context;
- `SlotNavigation<Unit>` для управления открытием фильтров;
- `childSlot(...)` для создания дочернего `FiltersComponent`;
- `Value<ChildSlot<Unit, FiltersComponent>>` как Decompose observable navigation state;
- `handleBackButton = false` для slot с фильтрами;
- JS wrapper `asJsSlot()` для передачи child slot в TypeScript.

## Роль Decompose

Decompose используется не как UI-фреймворк, а как слой управления компонентами:

```text
ComponentContext
   ↓
Root/Feature Component
   ↓
Child Stack / Child Slot
   ↓
Nested Component
   ↓
JS interop wrapper
   ↓
React UI
```

## Component contract

Компонент обычно выполняет несколько задач:

- хранит lifecycle context;
- экспортирует observable state в JS;
- принимает intents от TypeScript UI;
- подписывает UI на actions;
- создает дочерние компоненты;
- управляет navigation state.

## ChildSlot на примере фильтров

Для фильтров транзакций используется slot-based навигация:

```text
TransactionsComponent
   └── FiltersComponent через ChildSlot
```

Открытие фильтров:

```text
setIsFiltersOpen(true)
   ↓
filtersSlotNavigation.activate(Unit)
   ↓
pushUrlSegment("filters")
```

Закрытие фильтров:

```text
setIsFiltersOpen(false)
   ↓
filtersSlotNavigation.dismiss()
   ↓
popUrlSegment("filters")
```

## Правила расширения

1. Новый экран должен иметь явный Component contract.
2. Вложенные экраны должны создаваться через Decompose navigation primitives.
3. JS-facing navigation state должен оборачиваться в interop-типы из `utils.interop`.
4. Business logic не должна попадать в React-компоненты.
5. Component должен связывать navigation, MVI store и JS API.
