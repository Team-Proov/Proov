package com.kindl.proov.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalProovColors = staticCompositionLocalOf<ProovColors> {
    error("ProovTheme 안에서만 Proov.colors를 쓸 수 있습니다")
}
private val LocalProovTypography = staticCompositionLocalOf<ProovTypography> {
    error("ProovTheme 안에서만 Proov.typography를 쓸 수 있습니다")
}

/** 사용처: `Proov.colors.brand`, `Proov.typography.body` */
object Proov {
    val colors: ProovColors
        @Composable @ReadOnlyComposable get() = LocalProovColors.current
    val typography: ProovTypography
        @Composable @ReadOnlyComposable get() = LocalProovTypography.current
}

@Composable
fun ProovTheme(
    content: @Composable () -> Unit
) {
    val fontFamily = proovFontFamily()
    val typography = remember(fontFamily) { proovTypography(fontFamily) }

    CompositionLocalProvider(
        LocalProovColors provides ProovLightColors,
        LocalProovTypography provides typography,
    ) {
        MaterialTheme(content = content)
    }
}
