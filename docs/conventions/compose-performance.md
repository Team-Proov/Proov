# Compose performance

Rules for writing composables so that Compose skips work. Measurement procedure belongs in `PERFORMANCE.md` (planned). Apply the defaults below; add anything beyond them only after measuring.

## Skipping and donut-hole

- Pass a child only the values it reads, not the whole `UiState`. When the parent recomposes with a new `UiState` but a child's inputs are equal, Compose skips that child.
- A narrow parameter list is the main reason to [split composables](compose.md#splitting).
- Pass behavior as lambdas. Do not pass a ViewModel or a large object to a child.
- Lists in `UiState` use `ImmutableList`. Elsewhere a plain `List` is the default.

| Don't | Do |
| --- | --- |
| `PledgeCard(uiState = uiState)` (child reads one field of the whole state) | `PledgeCard(title = uiState.title, onClick = onClick)` |
| `PledgeList(viewModel = viewModel)` | `PledgeList(items = uiState.items, onItemClick = onItemClick)` |

## Stability

- Strong skipping is on, so unstable parameters are compared by instance. Prefer immutable data classes (`val` properties, `ImmutableList`) so equality checks hit.
- Use `@Stable` or `@Immutable` only when measurement (compiler reports, layout inspector recomposition counts) shows a real recomposition problem.

## Deferred state reads

Read fast-changing state (scroll offset, animation value) as late as possible, in layout or draw instead of composition.

- Use the lambda form of modifiers: `Modifier.offset { }`, `Modifier.graphicsLayer { }`, `Modifier.drawBehind { }`.
- Wrap derived values in `derivedStateOf` when the output changes less often than the input (for example "is scrolled past the first item").
- Pass `() -> T` instead of `T` to a child that only needs the value in layout or draw.

| Don't | Do |
| --- | --- |
| `Modifier.offset(y = scrollState.value.dp)` (recomposes on every scroll frame) | `Modifier.offset { IntOffset(0, scrollState.value) }` |
| `Header(scrollOffset = scrollState.value)` | `Header(scrollOffset = { scrollState.value })` |

## Lists

- Give `LazyColumn` / `LazyRow` items a stable `key`.
- Put the bottom inset in `contentPadding`, not as padding around the list ([window-insets](window-insets.md)).

## Measure first

If a screen janks or recomposes too often, measure before changing code: recomposition counts, then a profile. Record the finding and the fix in the change.
