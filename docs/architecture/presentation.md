# Presentation

Rules for screens in `presentation:<x>:impl`. Navigation is in [navigation](navigation.md); Compose style is in [compose](../conventions/compose.md).

## Route and Screen

| Piece | Visibility | Responsibility |
| --- | --- | --- |
| `XxxRoute` | `internal` | Gets the ViewModel with `metroViewModel()`, collects `UiState` and side effects, and passes only callbacks to navigation. The destination is decided by `xxxEntry` |
| `XxxScreen` | `private` | Receives `uiState` and lambdas. Never takes a ViewModel, so previews and tests need no ViewModel |

- Every `XxxScreen` has a `@Preview` wrapped in `ProovTheme`.

| Don't | Do |
| --- | --- |
| `private fun HomeScreen(viewModel: HomeViewModel)` | `private fun HomeScreen(uiState: HomeUiState, onRefresh: () -> Unit)` |

## Actions

A user input that the screen sends to the ViewModel is an action. Direction matters: `XxxAction` goes UI to ViewModel, `XxxSideEffect` goes ViewModel to UI.

- Up to 3 actions: one lambda per action (`onRefresh`, `onItemClick`).
- 4 or more: define `sealed interface XxxAction` and pass one `onAction: (XxxAction) -> Unit` to the screen.
- `XxxRoute` receives the action and branches with `when`: call a ViewModel function, or call a navigation callback for an action that only navigates.
- Do not use an `Actions` interface with an anonymous `object` implementation. It hides which callbacks a child reads, and captured lambdas go stale when they are not remember keys.

| Don't | Do |
| --- | --- |
| `MyPageScreen(actions = object : MyPageActions { ... })` built in `remember` | `MyPageScreen(uiState = uiState, onAction = onAction)` with `sealed interface MyPageAction { data object TermsClick : MyPageAction; data object LogoutClick : MyPageAction }` |

## Contract file

Each screen has one `XxxContract.kt` that holds its top-level types: `XxxUiState`, `XxxSideEffect`, and `XxxAction` when the screen has one. Do not nest them in a wrapper object. If the file grows, split the file and keep the type names.

| Don't | Do |
| --- | --- |
| `object HomeContract { data class UiState(...); sealed interface SideEffect }` | `data class HomeUiState(...)` and `sealed interface HomeSideEffect` as separate top-level types in `HomeContract.kt` |

## UiState

- A ViewModel exposes one `UiState` as `StateFlow`.
- Dialog visibility is state: keep `isXxxDialogVisible` in `UiState`.
- Lists in `UiState` use `ImmutableList` so the screen can skip recomposition (see [compose-performance](../conventions/compose-performance.md)).
- Business state that must survive configuration change or process death belongs in `UiState`, not in `ScreenState`.

## Side effects

- One-off events (toast, navigation, snackbar) are `XxxSideEffect`, sent through a `Channel(BUFFERED)` and exposed with `receiveAsFlow()`. Never keep them in `UiState`.
- Screens collect side effects only while `STARTED` and call `LocalGlobalUiEventTrigger.current` for app-wide UI events (see `core:trigger` in [modularization](modularization.md)).

## ScreenState (UI-only state)

- Default: use `remember` / `rememberSaveable` in `Route` or `Screen` for one or two variables without logic.
- Create `XxxScreenState` with `rememberXxxScreenState()` when three or more UI-only variables belong together, or when UI logic (scroll, focus, `derivedStateOf`) makes the composable hard to read.
- Mark the class `@Stable` and keep values in Compose `State`. Whether the annotation is required is decided by measurement ([compose-performance](../conventions/compose-performance.md)).
- Do not name it `UiStateHolder`; it would be confused with `UiState`.

## ViewModel

- A ViewModel calls `Repository` interfaces or UseCases from `domain:<feature>`. It never uses `data:*`, Ktor, or storage directly.
- Turn a failed `Result` into `UiState` or a side effect with `when` on `ProovException` ([error-handling](error-handling.md)).
- User-visible text is not built in the ViewModel. Send a `StringResource` or an error type and let the UI resolve it ([resources](../conventions/resources.md)).
