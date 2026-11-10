# Navigation

## Approach

Navigation 3 (JetBrains `navigation3-ui`). Routes are `@Serializable` objects or classes that implement `NavKey`. Screens register with `entry<Route> { }` in `EntryProviderScope`, and `NavDisplay` in `app:shared` draws them.

Navigation 3 was chosen because it uses the same model on Android and iOS and allows size-based layout (`SceneStrategy`) later.

## Core types (`core:navigation`)

| Type | Role |
| --- | --- |
| `Route : NavKey` | Marker for every route |
| `MainRouteTab : Route` | Marker for top-level routes shown in the bottom tab bar |
| `NavigationState` | Holds tab order (`topLevelStack`) and per-tab back stacks (`subStacks`). `rememberNavigationState` restores it after process death |
| `Navigator` | The only entry point for moving between screens. Provides `navigate(key)` and `goBack()` |

## Module structure

Each feature with a key has `presentation:<x>:api` and `presentation:<x>:impl`. Dependency rules are in [modularization](modularization.md#dependency-rules).

| Module | Content |
| --- | --- |
| `api` | `NavKey` implementations and `Navigator` extension functions only |
| `impl` | Screens, ViewModel, `xxxEntry(navigator)` extension |

- To go to another feature, call `navigator.navigate(key)` with a key from that feature's `api`. The extension that builds the key (`Navigator.navigateToXxx(id)`) lives in that `api`.

## Rules

- Navigation decisions are made only inside `xxxEntry`. A screen (`XxxRoute`) only receives callbacks.
- Features never touch the back stack directly; they use `Navigator`. Tab switching is called only from the bottom bar.
- `ProovApp` in `app:shared` assembles `NavDisplay` and registers each feature's `xxxEntry` in `entryProvider`. The tab list (`MainTab`) and tab label resources also live in `app:shared` under `ui.navigation`.
- Register every new route in `ProovNavConfig` in `app:shared`. iOS has no reflection, so an unregistered route breaks back stack save and restore.
- Dialogs and bottom sheets are not `NavKey`s; they are `UiState` (`isXxxDialogVisible`).
- A `NavKey` carries an id or a small value only. Never put a list or a large object in it; the next screen loads data from the data layer.
- The `entryDecorators` order is `rememberSaveableStateHolderNavEntryDecorator`, then `rememberViewModelStoreNavEntryDecorator`; `NavigationState.toEntries` guarantees it. A screen's `ViewModel` is cleared when its back stack entry is removed.

## `Navigator` behavior

| Call | Behavior |
| --- | --- |
| `navigate(root key of the current tab)` | Pops the current tab's back stack to its root |
| `navigate(root key of another tab)` | Switches tabs. Each tab keeps its back stack |
| `navigate(any other key)` | Pushes onto the current tab's back stack. If it already exists, moves it to the top |
| `goBack()` | Inside a tab, goes to the previous screen. At the root of a non-start tab, goes to the previous tab. At the root of the start tab, does nothing |

## Out of scope

- Deep links and external `Intent` delivery (such as the Kakao login callback): design them with the first consumer.
- Adaptive layout (`SceneStrategy`, list-detail): introduce with the first consumer. Mark a screen's role with the `entry` `metadata`.
- ViewModels that take a key argument: introduce with the first consumer using Metro `assistedMetroViewModel`.
- Passing results between screens (`ResultEventBus`): introduce with the first consumer.
