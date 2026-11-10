# Proov Architecture

Single source for layer responsibilities, dependency direction, state management, and DI principles. Module details are in [architecture/modularization.md](architecture/modularization.md).

## Layers

Proov follows Clean Architecture. Dependencies point inward: `presentation` and `data` depend on `domain`; `domain` depends on nothing platform-specific.

| Layer | Modules | Responsibility |
| --- | --- | --- |
| Presentation | `presentation:<feature>:api`, `presentation:<feature>:impl` | Screens, `UiState`, ViewModel, navigation keys and entries |
| Domain | `domain:<feature>` | Business rules: Repository interfaces, UseCases, feature models |
| Data | `data:<feature>` | Repository implementations, DTOs, mapping to domain models |
| Core | `core:*` | Shared building blocks: UI (`designsystem`, `navigation`, `trigger`), infrastructure (`network`, `crypto`, `localstorage`), shared models and utilities |
| App | `app:androidApp`, `app:iosApp`, `app:shared` | Entry points and the composition point |

`domain:<feature>` and `data:<feature>` are created when the first feature needs them. Their rules are in [domain-layer](architecture/domain-layer.md) and [data-layer](architecture/data-layer.md).

## Dependency direction

```
app:androidApp / app:iosApp -> app:shared -> presentation:*:impl -> domain:* -> core:model
                                          -> data:*              -> domain:*, core infra
```

- `presentation:*` depends on `domain:*` and UI `core:*` modules. It never depends on `data:*` or infrastructure `core:*` (`core:network`, `core:crypto`, `core:localstorage`).
- `domain:*` depends only on Kotlin stdlib, coroutines, `core:model`, and Metro annotation APIs. No Compose, Ktor, platform APIs, or other `domain:*` modules. A type shared by two features moves to `core:model` (Rule of Three, see [compose](conventions/compose.md#placement)).
- `data:*` depends on its own `domain:*`, `core:model`, and infrastructure modules. DTOs and implementations are `internal`.
- `core:*` never depends on `presentation:*`, `domain:*`, `data:*`, or `app:*`.
- Platform APIs are not used directly in `commonMain`; isolate them with `expect`/`actual` or an injected interface.
- `verifyModuleDependencies` enforces these rules where the modules exist. Extend it when `domain` and `data` modules are added.

## State management

MVVM. A ViewModel exposes one `UiState` as `StateFlow`; user actions call ViewModel functions; one-off events use a side-effect channel. Details are in [presentation](architecture/presentation.md).

## Dependency injection

Metro with `AppScope` only. Modules contribute bindings with `@ContributesTo` / `@ContributesBinding` / `@ContributesIntoMap`; the `@DependencyGraph` lives in `app:shared`. Details are in [di](architecture/di.md).

## Errors

Repositories return `kotlin.Result`; failures are `ProovException` subtypes defined in `domain`. Details are in [error-handling](architecture/error-handling.md).
