# Window insets

Principles that hold on Android and iOS. The app is always edge-to-edge (`enableEdgeToEdge()` on Android).

## Principles

1. **Edge-to-edge by default.** Content draws behind system bars. Add inset padding only to elements that must not be covered.
2. **Consume each inset once, at one place.** The highest level that knows the inset handles it (the Scaffold in `ProovApp`) and marks it with `consumeWindowInsets`. Children never apply an inset that is already consumed. On tab screens the bottom bar already consumes the navigation bar inset, so a screen must not call `navigationBarsPadding()` again.
3. **Lists and scroll areas.** Put the bottom inset in `contentPadding` at the end of the content. While scrolling, content shows behind the bar; at the end it is not covered.
4. **Keyboard.** An input screen applies `imePadding()` to its scroll container so the focused field is never hidden, and scrolls to the field on focus.
5. **Fixed bottom button.** It follows the keyboard and stays visible above it. Only the scroll area above the button shrinks; never pad the whole screen.
6. **Dismissing the keyboard.** Tapping outside the input area, or the done action, closes the keyboard.
7. **Bottom tab bar and keyboard.** When the keyboard opens, the tab bar stays behind it. Do not add `imePadding` for the tab bar height a second time.

## Screen types

| Screen | Top | Bottom | Keyboard |
| --- | --- | --- | --- |
| Tab screen (tab bar visible) | status bar, handled by the Scaffold | Consumed by the bottom bar | `imePadding()` on the scroll container |
| Other screen (no tab bar) | status bar | `navigationBarsPadding()` | `imePadding()` on the scroll container |
| Screen with a fixed bottom button | status bar | `navigationBarsPadding()` on the button area | Button area uses `imePadding()`; the scroll area above it shrinks |

| Don't | Do |
| --- | --- |
| `Modifier.navigationBarsPadding()` on a tab screen (the bottom bar already consumed it) | No bottom inset modifier on the tab screen; the bottom bar handles it |
| `Modifier.padding(bottom = 80.dp)` to keep a list clear of the bar | `LazyColumn(contentPadding = PaddingValues(bottom = ...))` |

Add focus-based scrolling helpers only when `imePadding` plus scrolling is not enough.
