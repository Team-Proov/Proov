# Domain layer

`domain:<feature>` holds business rules. It is created with the first feature that needs it (for example `domain:pledge`).

## Contents

| Item | Rule |
| --- | --- |
| Repository interface | Declares what the feature needs from data. Functions return `kotlin.Result<T>` or a `Flow` |
| UseCase | A class with an `operator fun invoke`. See "When to create a UseCase" |
| Feature model | A type used only inside this feature. A type used by more than one feature goes to `core:model` |

## Dependencies

- Allowed: Kotlin stdlib, coroutines, `core:model`, and Metro annotation APIs (`@Inject` and similar). Metro annotations are compile-time declarations, so no DI container code enters `domain`.
- Forbidden: Compose, Ktor, serialization of transport types, platform APIs, `expect`/`actual`, other `domain:*` modules.
- `@DependencyGraph` and `@Provides` modules live only in `app:shared`.
- Two features sharing a type is the signal to move it to `core:model` (Rule of Three, see [compose](../conventions/compose.md#placement)).

## When to create a UseCase

Create a UseCase only when one of these holds:

- It contains a business rule or validation.
- It combines two or more Repositories.
- Two or more screens use the same logic.

A ViewModel may call a Repository interface directly when the call is a plain pass-through. Do not create a UseCase that only forwards a Repository call.

## Text

The domain never builds user-visible text. Return a type (an error class, an enum) and let presentation map it to a `StringResource`.

## Tests

Test every UseCase that has a business rule with plain inputs and outputs in `commonTest`; no fakes are needed for pure logic. See [HARNESS](../HARNESS.md#required-tests-by-layer).
