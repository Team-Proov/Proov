package com.proov.proov.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Figma 변수 경로를 그대로 따르는 색상 토큰이다. 값의 기준은 DESIGN.md이다.
 * 용도(brand 등)는 정하지 않고 화면 코드가 직접 고른다. 사용처: `Proov.colors.primary.green500`
 */
@Immutable
data class ProovColors(
    val primary: ProovPrimaryColors = ProovPrimaryColors(),
    val neutral: ProovNeutralColors = ProovNeutralColors(),
    val system: ProovSystemColors = ProovSystemColors(),
)

/** Primary/Green, Primary/Yellow. Yellow/600은 Figma에 정의되어 있지 않다. */
@Immutable
data class ProovPrimaryColors(
    val green50: Color = Color(0xFFF0FDF2),
    val green100: Color = Color(0xFFDCF9E0),
    val green200: Color = Color(0xFFBDF4C5),
    val green300: Color = Color(0xFF89ED98),
    val green400: Color = Color(0xFF4FE661),
    val green500: Color = Color(0xFF32C949),
    val green600: Color = Color(0xFF24A33A),
    val green700: Color = Color(0xFF1F812F),
    val green800: Color = Color(0xFF1B6328),
    val green900: Color = Color(0xFF174A22),

    val yellow50: Color = Color(0xFFFFFDF0),
    val yellow100: Color = Color(0xFFFFF8C9),
    val yellow200: Color = Color(0xFFFFF19A),
    val yellow300: Color = Color(0xFFFFE97A),
    val yellow400: Color = Color(0xFFFFD84D),
    val yellow500: Color = Color(0xFFF2BA20),
    val yellow700: Color = Color(0xFF9B6A12),
    val yellow800: Color = Color(0xFF704D14),
)

/** Neutral/0 ~ 950. 속성 이름은 숫자로 시작할 수 없어 `n` 접두어를 붙인다. */
@Immutable
data class ProovNeutralColors(
    val n0: Color = Color(0xFFFFFFFF),
    val n50: Color = Color(0xFFFAFAF8),
    val n100: Color = Color(0xFFF2F2EF),
    val n200: Color = Color(0xFFE5E5E1),
    val n300: Color = Color(0xFFD0D0CB),
    val n400: Color = Color(0xFFA6A6A0),
    val n500: Color = Color(0xFF7A7A75),
    val n600: Color = Color(0xFF5B5B57),
    val n700: Color = Color(0xFF3F3F3C),
    val n800: Color = Color(0xFF292927),
    val n900: Color = Color(0xFF1B1C1B),
    val n950: Color = Color(0xFF0D0D0C),
)

/**
 * System/Success, Warning, Danger. 단계는 50, 200, 400, 600, 800만 있다.
 * Success는 Primary/Green과 값이 같지만(DESIGN.md 불일치 항목) 디자이너 확인 전까지 별도 토큰으로 둔다.
 */
@Immutable
data class ProovSystemColors(
    val success50: Color = Color(0xFFF0FDF2),
    val success200: Color = Color(0xFFBDF4C5),
    val success400: Color = Color(0xFF4FE661),
    val success600: Color = Color(0xFF24A33A),
    val success800: Color = Color(0xFF1B6328),

    val warning50: Color = Color(0xFFFFF6E8),
    val warning200: Color = Color(0xFFFFD6A3),
    val warning400: Color = Color(0xFFFF9F43),
    val warning600: Color = Color(0xFFD96800),
    val warning800: Color = Color(0xFF7A3A00),

    val danger50: Color = Color(0xFFFFF1F0),
    val danger200: Color = Color(0xFFFFC7C3),
    val danger400: Color = Color(0xFFFF6B61),
    val danger600: Color = Color(0xFFD92D20),
    val danger800: Color = Color(0xFF8A1C14),
)
