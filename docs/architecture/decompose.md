# Навигация и Decompose

Изменено: 02.06.2026

В проекте Decompose используется как слой навигации и жизненного цикла компонентов. React отвечает за отрисовку, а Decompose управляет структурой экранов и их состоянием.

Хороший пример можно посмотреть в `TransactionsComponent`.

Там используются:

- `ComponentContext`
- `SlotNavigation<Unit>`
- `childSlot(...)`
- `ChildSlot<Unit, FiltersComponent>`
- `asJsSlot()`

## Как это выглядит

```text
ComponentContext
  -> Feature Component
  -> Child Stack / Child Slot
  -> Nested Component
  -> JS wrapper
  -> React UI
```

То есть React получает уже готовые компоненты и навигационное состояние, а не строит дерево экранов самостоятельно.

## Пример с фильтрами транзакций

Фильтры открываются через отдельный `ChildSlot`.

```text
TransactionsComponent
  -> FiltersComponent
```

Открытие выглядит примерно так:

```text
setIsFiltersOpen(true)
  -> filtersSlotNavigation.activate(Unit)
  -> pushUrlSegment("filters")
```

Закрытие работает в обратную сторону:

```text
setIsFiltersOpen(false)
  -> filtersSlotNavigation.dismiss()
  -> popUrlSegment("filters")
```

Из-за этого состояние навигации остаётся внутри Kotlin-кода и может синхронизироваться с URL браузера.

## Что обычно делает компонент

Большинство компонентов в проекте выполняют одинаковый набор задач:

- держат `ComponentContext`;
- экспортируют состояние в JavaScript;
- принимают intents от React;
- создают дочерние компоненты;
- связывают навигацию и MVI-контейнер.
