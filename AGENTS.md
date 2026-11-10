# Proov Agent Guide

Proov (Prove + Move) is a Compose Multiplatform app for Android and iOS. Users turn goals into pledges and build an achievement rate through verification.

## Rules

- The nearest `AGENTS.md` wins. This file wins over any document under `docs/`.
- Do not decide open questions yourself. Ask the user when a decision is not recorded in the repo.
- Before editing, read the matching row in [docs/README.md](docs/README.md). Follow [docs/HARNESS.md](docs/HARNESS.md) for implementation, debugging, tests, and review.
- Colors, typography, and theme come from `ProovTheme`, `ProovColors`, `ProovTypography` as defined in [DESIGN.md](DESIGN.md). Never hardcode values.
- Server work follows `docs/server/`. If the spec is unclear or conflicts, ask the user.
- Before changing `minSdk`/`targetSdk`, iOS minimum, or dependency support, or before performance work, read the matching planned document in `docs/README.md` if it exists.

## Project

- Kotlin Multiplatform. Put shared UI and logic in `commonMain` first. Shared build settings live in `build-logic` convention plugins.
- Follow the Kotlin official code style. `ktlint` enforces it.
- Add dependencies only with a stated reason and impact. Keep verified stable versions; do not bump just because a newer one exists.

## Commands

The dev machine is Windows, so use `.\gradlew.bat`.

```
.\gradlew.bat ktlintFormat ktlintCheck
.\gradlew.bat verifyModuleDependencies
.\gradlew.bat :app:androidApp:assembleDebug :app:shared:testAndroidHostTest :app:androidApp:lintDebug
```

## Comments

KDoc goes only on public API and is written in Korean as a noun phrase that explains how to use it. Use `//` only for short labels and `TODO`. Do not write comments that repeat the code.

## Security

- Secrets, keys, and local values go in untracked files such as `local.properties`.
- Never read, print, or commit `local.properties`, keystores, `google-services.json`, or `GoogleService-Info.plist`. `.claude/settings.json` denies reading them.

## Git

- Commit and push only when the user asks explicitly.
- Branch: `type/#issue-english-description` (`feat`, `fix`, `chore`, `mod`). Example: `feat/#12-pledge-create`
- Commit: Conventional Commits with a Korean title; the prefix matches the branch type. Example: `feat: 공약 생성 화면 추가`
- Issues and PRs follow the templates in `.github`.

## Finishing

Update the code, tests, and related docs together. When a new rule or decision appears, update its single source document.
