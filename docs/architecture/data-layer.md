# Data layer

`data:<feature>` implements the Repository interfaces declared in `domain:<feature>`. It is created with the first feature that needs it.

## Contents

| Item | Rule |
| --- | --- |
| Repository implementation | `internal`. Binds to its domain interface with `@ContributesBinding(AppScope::class)` |
| DTO | `internal`, `@Serializable`. Never leaves the module |
| Mapper | Converts DTO to the domain model. Covered by tests |
| Storage access | Uses `core:localstorage` for general values and `core:crypto` for sensitive ones |

## Dependencies

- Allowed: its own `domain:<feature>`, `core:model`, `core:network`, `core:crypto`, `core:localstorage`, `core:common`.
- Never depended on by `presentation:*`. `app:shared` depends on it directly so Metro can collect its bindings.
- Ktor types do not cross the module boundary.

## Network calls

- Wrap suspend work with `suspendRunCatching`, never plain `runCatching` (see [error-handling](error-handling.md)).
- Convert transport failures and server error codes to `ProovException` in this layer.
- The request, response, auth, and error code contract comes from `docs/server/`. Do not start a server call before its spec exists there.

## Tests

Required: error mapping (use Ktor `MockEngine` in `commonTest`) and DTO-to-model mapping. Do not write tests that only check delegation or library behavior. See [HARNESS](../HARNESS.md#required-tests-by-layer).
