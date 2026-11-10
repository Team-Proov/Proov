# Dependency injection

Metro. Network uses Ktor ([modularization](modularization.md) for `core:network`).

## Graph and scope

- One scope: `AppScope`. Add another scope (for example a login-session scope) only when a feature needs it, and design it with that feature.
- `AppGraph` (common contract) extends `ViewModelGraph`. The `@DependencyGraph` implementations are in `app:shared` (`AndroidAppGraph` in `androidMain`, `IosAppGraph` in `iosMain`). Nothing else declares a graph.
- Dependency gaps and cycles are detected at compile time.

## Bindings

| Need | Use |
| --- | --- |
| Interface to implementation | `@ContributesBinding(AppScope::class)` on the implementation |
| Singleton | `@SingleIn(AppScope::class)` |
| Third-party object (`HttpClient`, `DataStore`) | `@Provides` in a `@ContributesTo(AppScope::class)` provider |
| ViewModel | `@Inject` + `@ViewModelKey` + `@ContributesIntoMap(AppScope::class)` |
| Platform-specific value (file path) | The platform graph provides it; common code only asks for it |

Every module that contributes bindings must be a direct dependency of `app:shared`, because Metro collects contributions from the graph owner's compile classpath.

## Layer rules

- `presentation:*` gets a ViewModel only through `metroViewModel()` from `metrox-viewmodel-compose`. It never references `app:shared`.
- `domain:*` may use Metro annotation APIs only (`@Inject` on UseCases). It declares no graph and no `@Provides`.
- `data:*` binds its implementations with `@ContributesBinding`.
