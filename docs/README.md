# Proov Docs

Read the row that matches your task before editing. Rows marked `planned` have no document yet; write it when that area appears.

| Working on | Read |
| --- | --- |
| Layer responsibilities, dependency direction, state and DI overview | [ARCHITECTURE.md](ARCHITECTURE.md) |
| Modules, adding a module, dependency rules, dependency policy | [architecture/modularization.md](architecture/modularization.md) |
| Navigation, `NavKey`, entries, tabs | [architecture/navigation.md](architecture/navigation.md) |
| Route, Screen, Contract (`UiState`, `SideEffect`, `Action`), `ScreenState`, ViewModel | [architecture/presentation.md](architecture/presentation.md) |
| UseCase, Repository interface, domain models | [architecture/domain-layer.md](architecture/domain-layer.md) |
| Repository implementation, DTO, mapping, storage | [architecture/data-layer.md](architecture/data-layer.md) |
| Failures, `ProovException`, `suspendRunCatching`, server error codes | [architecture/error-handling.md](architecture/error-handling.md) |
| Metro graph, scope, bindings, ViewModel injection | [architecture/di.md](architecture/di.md) |
| Composable placement, splitting, tokens, text, previews | [conventions/compose.md](conventions/compose.md) |
| Skipping, deferred state reads, recomposition cost | [conventions/compose-performance.md](conventions/compose-performance.md) |
| Status bar, navigation bar, tab bar, keyboard | [conventions/window-insets.md](conventions/window-insets.md) |
| Drawable and string names, `stringResource`, `contentDescription` | [conventions/resources.md](conventions/resources.md) |
| Colors, typography, theme | [../DESIGN.md](../DESIGN.md) |
| Verification gates, work loop, tests per layer | [HARNESS.md](HARNESS.md) |
| Server API contract (request, response, auth, error codes) | `server/` (planned; see [server/README.md](server/README.md)) |
| Device compatibility (`minSdk`, `targetSdk`, iOS minimum) | `COMPATIBILITY.md` (planned) |
| Performance measurement (image loading, jank, memory) | `PERFORMANCE.md` (planned; measurement procedure only) |
| Figma screen to code mapping | `FIGMA-MAP.md` (planned) |

| Folder | Holds |
| --- | --- |
| `architecture/` | How the code is organized |
| `conventions/` | How to write UI and resources |

## Writing docs

- One topic lives in one document. Link to it instead of repeating it.
- Write only decided rules. Proposals belong in issues and PRs.
- When a rule changes, update the document in the same change. Delete rules that no longer apply.
- Do not create empty documents. Add a `planned` row here until the area exists.
- Docs are written in English. Keep class, module, and command names verbatim. KDoc in code stays Korean.
- Repeated review or test failures should become a structural check (test, lint, Gradle verification) before they become a prose rule.
