package com.proov.proov.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import proov.core.designsystem.generated.resources.Res
import proov.core.designsystem.generated.resources.pretendard_bold
import proov.core.designsystem.generated.resources.pretendard_medium
import proov.core.designsystem.generated.resources.pretendard_regular
import proov.core.designsystem.generated.resources.pretendard_semibold

/**
 * Figma 텍스트 스타일(DESIGN.md)을 그룹별로 묶은 토큰이다.
 * 속성 이름은 크기를 뜻하며 접두어(d, h, t, b, l, c)는 숫자 시작을 피하기 위한 것이다.
 * 사용처: `Proov.typography.title.t20`
 */
@Immutable
data class ProovTypography(
    val display: Display,
    val heading: Heading,
    val title: Title,
    val body: Body,
    val label: Label,
    val caption: Caption,
) {
    @Immutable
    data class Display(val d40: TextStyle)

    @Immutable
    data class Heading(val h32: TextStyle, val h28: TextStyle, val h24: TextStyle)

    @Immutable
    data class Title(val t22: TextStyle, val t20: TextStyle, val t18: TextStyle)

    @Immutable
    data class Body(val b18: TextStyle, val b16: TextStyle, val b14: TextStyle)

    @Immutable
    data class Label(val l16: TextStyle, val l14: TextStyle)

    @Immutable
    data class Caption(val c12: TextStyle)
}

@Composable
fun proovFontFamily(): FontFamily = FontFamily(
    Font(Res.font.pretendard_regular, FontWeight.Normal),
    Font(Res.font.pretendard_medium, FontWeight.Medium),
    Font(Res.font.pretendard_semibold, FontWeight.SemiBold),
    Font(Res.font.pretendard_bold, FontWeight.Bold),
)

fun proovTypography(fontFamily: FontFamily): ProovTypography {
    // 자간은 전 스타일 -2%로 같다
    fun style(size: Int, lineHeight: Int, weight: FontWeight) = TextStyle(
        fontFamily = fontFamily,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        fontWeight = weight,
        letterSpacing = (-0.02).em,
    )

    val regular = FontWeight.Normal
    val medium = FontWeight.Medium
    val semiBold = FontWeight.SemiBold
    val bold = FontWeight.Bold

    return ProovTypography(
        display = ProovTypography.Display(d40 = style(40, 52, bold)),
        heading = ProovTypography.Heading(
            h32 = style(32, 42, bold),
            h28 = style(28, 38, bold),
            h24 = style(24, 34, bold),
        ),
        title = ProovTypography.Title(
            t22 = style(22, 30, semiBold),
            t20 = style(20, 28, semiBold),
            t18 = style(18, 26, semiBold),
        ),
        body = ProovTypography.Body(
            b18 = style(18, 28, regular),
            b16 = style(16, 24, regular),
            b14 = style(14, 22, regular),
        ),
        label = ProovTypography.Label(
            l16 = style(16, 22, semiBold),
            l14 = style(14, 20, semiBold),
        ),
        caption = ProovTypography.Caption(c12 = style(12, 18, medium)),
    )
}
