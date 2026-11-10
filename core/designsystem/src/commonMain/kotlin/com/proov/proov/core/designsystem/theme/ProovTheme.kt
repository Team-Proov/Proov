package com.proov.proov.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
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

/** 사용처: `Proov.colors.primary.green500`, `Proov.typography.body.b16` */
object Proov {
    val colors: ProovColors
        @Composable @ReadOnlyComposable
        get() = LocalProovColors.current
    val typography: ProovTypography
        @Composable @ReadOnlyComposable
        get() = LocalProovTypography.current
}

@Composable
fun ProovTheme(content: @Composable () -> Unit) {
    val fontFamily = proovFontFamily()
    val typography = remember(fontFamily) { proovTypography(fontFamily) }
    val colors = remember { ProovColors() }

    CompositionLocalProvider(
        LocalProovColors provides colors,
        LocalProovTypography provides typography,
    ) {
        // ponytail: 메인 컬러 확정 전이라 primary 계열은 Material 기본값이다. 확정 후 연결한다.
        MaterialTheme(
            colorScheme = lightColorScheme(
                background = colors.neutral.n50,
                surface = colors.neutral.n0,
                onBackground = colors.neutral.n900,
                onSurface = colors.neutral.n900,
            ),
            content = content,
        )
    }
}
