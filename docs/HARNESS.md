# Proov Harness

## Purpose

The work loop and verification gates that let an agent make changes repeatably. Layer rules are in [ARCHITECTURE.md](ARCHITECTURE.md).

## Work loop

For any change to code, settings, or tests:

1. **Align.** Confirm the target, what must not change, and the done condition. Confirm the needed files, tools, and permissions exist. Read `ARCHITECTURE.md` when touching a module or a layer. If the done condition needs a product decision or conflicts with itself, ask the user before implementing.
2. **Small change.** Make the smallest change that meets the done condition. Prefer existing patterns and public interfaces. For a new dependency, confirm the need and the impact.
3. **Verify.** Run every verification gate that matches the change. Re-run after a failure only when you have a new hypothesis and a change that follows from it.
4. **Record.** In the result, list the changed files, the verification run and its result, and any verification not run with the reason.

## Done, retry, escalation

- **Done:** the done condition is met, every applicable gate passes, and the change and results are recorded.
- **Retry:** only with a new cause hypothesis, explained by a failure log or a reproduction, and a change that follows from it.
- **Escalate:** stop and show the user the evidence and the decision needed when the same check fails twice without new evidence, when a needed file, tool, or permission is missing, or when a product, security, or hard-to-reverse decision is required.

## Verification gates

The dev machine is Windows; run Gradle as `.\gradlew.bat`.

| Change | Required verification |
| --- | --- |
| Any Kotlin or Gradle file | `.\gradlew.bat ktlintCheck` (run `ktlintFormat` to fix) |
| General Kotlin/Compose code | `.\gradlew.bat :app:androidApp:assembleDebug :app:shared:testAndroidHostTest :app:androidApp:lintDebug` |
| `core:*`, `build-logic`, Gradle, Manifest, any module dependency | The checks above, plus `.\gradlew.bat verifyModuleDependencies`, plus build and test of every affected module |
| UiState or ViewModel | Pin state transitions with `commonTest` unit tests; replace external dependencies with fakes |
| iOS-only code (`iosMain`, `app:iosApp`) | Cannot run on Windows. Report it as unverified and as needing macOS or CI |
| Core user flow | Verify on a prepared device or emulator, or with a UI test |

### CI and review

`.github/workflows/ci.yml` runs `ktlintCheck` + `verifyModuleDependencies` (job `check`) and the Android build, tests, and lint (job `build`) on pull requests to `develop`. Both must pass before merge. CodeRabbit (`.coderabbit.yaml`, English) comments on pull requests; it does not block merge.

### iOS status

If the Android gates pass, the work is done, but the result must state "iOS unverified: needs macOS or CI (GitHub Actions)". When `iosMain` or `app:iosApp` changed, show this as a separate warning.

### Required tests by layer

| Layer | Required | Not required |
| --- | --- | --- |
| `presentation:*` ViewModel | State transitions and side-effect emission in `commonTest`, with Repository and UseCase replaced by fakes | Composable unit tests |
| `domain:*` | Every UseCase that has a business rule, with plain inputs and outputs | UseCases that only forward a Repository call (they should not exist) |
| `data:*` | Error mapping to `ProovException` (Ktor `MockEngine`, `commonTest`) and DTO-to-model mapping | Tests of delegation or library behavior |

Keep a fake inside the module's `commonTest`. When a third module needs the same fake, move it to a `core:testing` module.

## Invariants

- Decide from evidence in the repository. Do not settle behavior or a contract from chat, personal notes, or guesses alone.
- Never ignore or weaken a failing check. If the cause is non-reproducibility, the environment, or a product decision, escalate with the evidence and the decision needed.
- Keep cleanups and refactors unrelated to the request in a separate change.
- Before an external-system change, secret access, or a hard-to-reverse action, confirm the current permission and approval process.

## Execution path

By default one agent runs the work loop to the end, sequentially. Split work only when the user asks for parallel or sub-agent work and the sub-tasks are independent.

- Give each sub-task its owned files or responsibility, inputs, done condition, and permissions.
- When one result feeds another task, start the next task only after the earlier result and its verification evidence exist.
- After merging results, run the verification gates again on the combined change.

## Feedback loop

When the same kind of problem repeats in review, bugs, or test failures:

1. Record the cause and how to reproduce it in a test or document.
2. If a tool can judge the rule, promote it to a unit test, lint rule, or Gradle check (as `verifyModuleDependencies` does).
3. If it needs judgment, add it to the nearest single-source document.
4. After adding the rule, run the check and confirm it catches a violation.
