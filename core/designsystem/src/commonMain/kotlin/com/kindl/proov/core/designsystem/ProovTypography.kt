package com.kindl.proov.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import proov.core.designsystem.generated.resources.Res
import proov.core.designsystem.generated.resources.pretendard_bold
import proov.core.designsystem.generated.resources.pretendard_extrabold
import proov.core.designsystem.generated.resources.pretendard_medium
import proov.core.designsystem.generated.resources.pretendard_regular

@Immutable
data class ProovTypography(
    val h1: TextStyle,
    val subtitle: TextStyle,
    val headerTitle: TextStyle,
    val sectionTitle: TextStyle,
    val cardTitle: TextStyle,
    val cardTitleSmall: TextStyle,
    val body: TextStyle,
    val listItem: TextStyle,
    val input: TextStyle,
    val inputLabel: TextStyle,
    val caption: TextStyle,
    val error: TextStyle,
    val badge: TextStyle,
    val tabLabel: TextStyle,
    val display: TextStyle,
)

@Composable
fun proovFontFamily(): FontFamily = FontFamily(
    Font(Res.font.pretendard_regular, FontWeight.Normal),
    Font(Res.font.pretendard_medium, FontWeight.Medium),
    Font(Res.font.pretendard_bold, FontWeight.Bold),
    Font(Res.font.pretendard_extrabold, FontWeight.Black),
)

fun proovTypography(fontFamily: FontFamily): ProovTypography {
    fun style(
        size: Float,
        weight: FontWeight,
        tracking: Float = 0f,
        lineHeight: Float? = null,
    ) = TextStyle(
        fontFamily = fontFamily,
        fontSize = size.sp,
        fontWeight = weight,
        letterSpacing = tracking.em,
        lineHeight = lineHeight?.let { (size * it).sp } ?: TextUnit.Unspecified,
    )

    val regular = FontWeight.Normal
    val medium = FontWeight.Medium
    val bold = FontWeight.Bold
    val black = FontWeight.Black

    return ProovTypography(
        h1 = style(26f, black, -0.03f, 1.3f),
        subtitle = style(15f, bold, lineHeight = 1.5f),
        headerTitle = style(17f, black),
        sectionTitle = style(17f, black, -0.01f),
        cardTitle = style(17f, black, -0.02f, 1.35f),
        cardTitleSmall = style(15f, black, -0.02f, 1.35f),
        body = style(14f, medium, lineHeight = 1.5f),
        listItem = style(14.5f, bold),
        input = style(15f, regular),
        inputLabel = style(16f, black, -0.01f, 1.35f),
        caption = style(12f, medium, lineHeight = 1.5f),
        error = style(12f, bold),
        badge = style(11f, black),
        tabLabel = style(11f, bold),
        display = style(34f, black, -0.03f, 1f),
    )
}
