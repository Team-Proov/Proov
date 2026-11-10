# Modularization

Single source for the module list, naming, dependency rules, and adding modules.

## Module tree

```
app/        androidApp, iosApp, shared
core/       common, crypto, designsystem, localstorage, model, navigation, network, trigger
presentation/<feature>/   api, impl
domain/<feature>          (created with the first feature that needs it)
data/<feature>            (created with the first feature that needs it)
```

`core:*` holds modules that more than one feature uses. `domain` and `data` are top-level groups, not part of `core`, so a path pattern (`:domain:*`, `:data:*`) is enough to enforce rules.

## Modules

| Module | Responsibility |
| --- | --- |
| `app:androidApp` | Android entry point (`MainActivity`) and Android-only settings |
| `app:iosApp` | iOS entry point. SwiftUI code lives only here. The Xcode build calls `:app:shared:embedAndSignAppleFrameworkForXcode` |
| `app:shared` | iOS Framework umbrella and the composition point. `commonMain` holds `ui` (`ProovRoot`, `ProovApp`, `ProovAppState`, `MainViewModel`, `MainTab`, `ProovNavConfig`) and `di` (`AppGraph`, ViewModel factory, image loader binding). Platform graphs (`AndroidAppGraph`, `IosAppGraph`) and the iOS entry (`MainViewController`) live in the platform source sets |
| `presentation:<x>:api` | `NavKey` types and `Navigator` extension functions only. No UI, resources, domain, or data code |
| `presentation:<x>:impl` | Screens, `UiState`, ViewModel, `xxxEntry` extensions on `EntryProviderScope` |
| `core:common` | Shared extensions (`extension`) and utilities (`util`, including `suspendRunCatching`). Depends only on Compose and Lifecycle KMP artifacts, declared `implementation`. No platform APIs, no other `core:*` |
| `core:model` | Models shared by presentation, domain, and data |
| `core:designsystem` | Theme (`ProovTheme`, `ProovColors`, `ProovTypography`), shared components, fonts |
| `core:navigation` | `Route`, `MainRouteTab`, `NavigationState`, `Navigator`. Rules are in [navigation.md](navigation.md) |
| `core:trigger` | App-wide UI events (toast, snackbar, dialog): `GlobalUiEventHolder`, `LocalGlobalUiEventTrigger`, `GlobalMessageHost`, `DialogType`. `ProovApp` provides the holder and shows one dialog slot. Screens collect side effects and call `LocalGlobalUiEventTrigger.current`. `DialogType` holds app-wide policy only; a feature-specific dialog is declared in that screen with `ProovDialog` |
| `core:localstorage` | General settings storage (DataStore Preferences, shared KMP implementation). `LocalStorageProvider` provides `DataStore<Preferences>`; only the file path (`@Named(PREFERENCES_FILE)`) comes from the platform graph. Sensitive values go to `core:crypto` |
| `core:crypto` | Secure storage for sensitive values such as tokens. `SecureStorage` (`put`/`get`/`remove`) and `SecureKey` (`ACCESS_TOKEN`, `REFRESH_TOKEN`; the fixed `id` is used for the file name, Keychain account, and GCM AAD) are in `commonMain`; implementations bind with `@ContributesBinding`. Android (`AndroidSecureStorage`) stores per-key files encrypted with a Keystore AES-256-GCM key (direct JCA, no Tink) in `noBackupFilesDir`; it recreates the key and retries once only on key-related exceptions, and clears all stored items when it recreates the key. iOS (`IosSecureStorage`) uses Keychain (`AfterFirstUnlockThisDeviceOnly`) and clears leftover items after reinstall. `get` deletes a permanently failed item and returns `null`; only transient errors propagate. The memory cache and the fallback on save failure belong to `TokenManager`, not this module |
| `core:network` | Ktor `HttpClient` creation and Metro `NetworkProvider`. The base URL comes from `BASE_URL` in `local.properties`. `NetworkMonitor` (`isOnline: Flow<Boolean>`) is common; `AndroidNetworkMonitor` and `iOSNetworkMonitor` bind with `@ContributesBinding` in their platform source sets. `ProovAppState.isOffline` exposes the result |

`core:network`, `core:crypto`, and `core:localstorage` are infrastructure: they set things up and do not fetch feature data. Only `data:*` and `app:shared` may depend on them.

## Dependency rules

The layer rules are in [ARCHITECTURE.md](../ARCHITECTURE.md#dependency-direction). Module-level rules:

- `@DependencyGraph` lives only in the platform source sets of `app:shared`. Metro collects contributions from the owning module's compile classpath, so `app:shared` must depend directly on every module that uses `@ContributesTo` / `@ContributesBinding` / `@ContributesIntoMap`.
- `presentation:*`, `domain:*`, `data:*`, and `core:*` never depend on `app:*`.
- `presentation:<x>:impl` modules never depend on each other. To navigate to another feature, depend on that feature's `api` and call `Navigator`.
- `presentation:<x>:api` depends only on `core:navigation` and serialization.
- `domain:*` modules never depend on each other.

## Adding a module

- Apply a convention plugin through its catalog alias, for example `alias(libs.plugins.proov.kmp.library)`: `proov.kmp.library`, `proov.kmp.compose`, `proov.kmp.serialization`, `proov.kmp.feature`, and `proov.android.application` for the app module. A feature `api` applies `proov.kmp.serialization`; an `impl` applies `proov.kmp.feature` and references its own `api`. A new plugin is registered in `build-logic` (`gradlePlugin`) and in the `[plugins]` section of `libs.versions.toml`.
- Plugin classes (entry points) sit at the root of `build-logic/convention/src/main/kotlin`. Implementation (`DependencyManager`, `AppConfig`, `Catalog`) is `internal` in `com.proov.proov.buildlogic`. Dependencies a plugin adds are grouped per feature in `DependencyManager`. SDK and JVM versions, namespace rules, and plugin ID constants live in `AppConfig.kt`. The JVM target is 11.
- Depend on modules through type-safe accessors, for example `implementation(projects.core.model)`. Inside `build-logic` accessors are unavailable, so only the core dependencies added by `DependencyManager.addFeatureDependencies` stay as path strings.
- Register the module in `settings.gradle.kts` and update the tables above. Extend `verifyModuleDependencies` if the module introduces a new rule.

## Dependency policy

Add a new dependency only with a stated reason and impact. Keep verified stable versions; do not upgrade just because a newer version exists.

## Lint and format

`ktlint` runs on every module through the convention plugins; rules are in the root `.editorconfig` (`intellij_idea` style, 120 columns, Composable function names allowed). Build-generated sources under `build/` are excluded.
