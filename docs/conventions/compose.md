# Compose

## Placement

A composable lives with the feature that uses it. Promote it only when the reuse is proven (Rule of Three).

| Situation | Location |
| --- | --- |
| Used by one screen | Private in that screen's file, or a file in the feature's `component/<screen>/` once it grows |
| Used by several screens of one feature | `internal` in the feature's `component/` folder |
| Used by a third feature | Move to `core:designsystem` |
| Defined as a shared component in the Figma design system (button, dialog, input field, chip) | Start in `core:designsystem` |

Rule of Three: the first two features may each keep their own copy. Abstract on the third use, when three real cases show the right API. Presentation modules cannot depend on each other, so promotion is also how a type or composable crosses a feature boundary (`core:designsystem` for UI, `core:model` for models).

A component that takes fixed content (Figma never changes the items) takes values, not a slot.

## Splitting

Do not keep a whole screen in one file or one composable. Split when:

- A section, card, or list item has one clear purpose.
- The block reads only a few values, so its parameters narrow to a few immutable values and lambdas.
- The screen function is hard to read in one pass.

Splitting improves readability and encapsulation, and a narrow parameter list lets Compose skip the child when the parent recomposes ([compose-performance](compose-performance.md)).

| Don't | Do |
| --- | --- |
| One `HomeScreen` of 400 lines with header, list, and card inline | `HomeScreen` composes `HomeHeader`, `PledgeList`, and `PledgeCard`, each with its own narrow parameters |

## Parameters

Order: required parameters, `modifier: Modifier = Modifier`, optional parameters, `content`. Do not document `modifier` in `@param`.

| Don't | Do |
| --- | --- |
| `fun PledgeCard(modifier: Modifier = Modifier, title: String, onClick: () -> Unit)` | `fun PledgeCard(title: String, onClick: () -> Unit, modifier: Modifier = Modifier)` |

## Tokens

Colors, typography, and theme come from `ProovTheme`, `ProovColors`, `ProovTypography` ([DESIGN.md](../../DESIGN.md)). Never hardcode a color, text style, or font.

## Text

Text that must keep its meaning stays on one line in a fixed cell; use an auto-size or ellipsis rule instead of letting it wrap. Everything else may wrap. All user-visible text uses `stringResource` ([resources](resources.md)).

## Previews

Every screen-level composable has a `@Preview` wrapped in `ProovTheme`, with representative state passed in (the screen takes `uiState`, so no ViewModel is needed).
