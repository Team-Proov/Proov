# Error handling

## Return type

Repositories return `kotlin.Result<T>`. There is no custom `Result` type.

## Exception types

`domain` defines a sealed `ProovException`. Add a subtype only when a screen reacts to it differently.

| Subtype | Meaning |
| --- | --- |
| `NetworkUnavailable` | The device cannot reach the server |
| `Unauthorized` | The token is missing or rejected |
| `Server(code)` | The server returned a business error code |
| `Unknown` | Anything else |

The mapping from server error codes to `Server(code)` is added to this table when `docs/server/` defines the codes.

## Flow of a failure

1. `data:*` catches transport and server failures and maps them to `ProovException`. Ktor types stay inside `data`.
2. The Repository returns `Result.failure(ProovException)`.
3. The ViewModel branches with `when` and turns the failure into `UiState` (an inline error state) or a side effect (toast, dialog). App-wide dialog policy reuses `DialogType` from `core:trigger`.
4. The UI turns the error type into text with a `StringResource`.

## Coroutines

- In `suspend` code use `suspendRunCatching` from `core:common`, not `runCatching`. Plain `runCatching` swallows `CancellationException`.
- `suspendRunCatching` rethrows `CancellationException`, but treats `TimeoutCancellationException` as a failure.
