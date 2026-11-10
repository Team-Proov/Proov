# Resources

Compose Multiplatform resources (`composeResources`). Each module generates its own `Res` class, accessed as `Res.string.xxx`, `Res.drawable.xxx`.

## Ownership

| Resource | Location |
| --- | --- |
| Used by one feature | That feature's `presentation:<x>:impl` `composeResources` |
| Used by several features: icons, fonts | `core:designsystem` |
| App-wide dialog text | `core:trigger` (`DialogType`) |

Promote a resource on its third feature use (Rule of Three, see [compose](compose.md#placement)). Strings are mostly feature-specific and stay in the feature module.

## Names

Lowercase snake_case.

| Kind | Format | Example |
| --- | --- | --- |
| Icon | `ic_<name>` | `ic_arrow_left.xml` |
| Illustration | `img_<name>` | `img_empty_pledge.png` |
| Background | `bg_<name>` | `bg_card.png` |
| String key | `<screen>_<purpose>` | `home_title`, `pledge_create_button` |
| Shared string key | `common_<purpose>` | `common_confirm` |
| Accessibility text | `<screen>_<purpose>_a11y` | `home_tab_pledge_a11y` |

Prefer vector icons. Use raster images only for illustrations and photos. Today the app is Korean only, so keep the default `values/strings.xml`; add qualifier folders such as `values-en` when a language is added.

## Text

Scope: user-visible text only. Logs, keys, test data, and preview samples are exempt.

| Don't | Do |
| --- | --- |
| `Text("기록이 없어요.")` | `Text(stringResource(Res.string.record_empty_title))` |
| `contentDescription = "닫기"` | `contentDescription = stringResource(Res.string.record_close_a11y)` |

- Outside composables (ViewModel, `domain`), do not build text. Pass a `StringResource` or an error type and resolve it in the UI. This keeps `domain` free of Compose.

## contentDescription

- Meaningful icons, images, and icon-only buttons must have one, from a string resource whose key ends with `_a11y`.
- Decorative images, and icons next to text that says the same thing, use `null` so screen readers do not read it twice.
