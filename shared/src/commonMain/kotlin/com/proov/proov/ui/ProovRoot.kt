package com.proov.proov.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import coil3.compose.setSingletonImageLoaderFactory
import com.proov.proov.core.designsystem.theme.ProovTheme
import com.proov.proov.di.AppGraph
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory

/** 모든 플랫폼이 거치는 공통 진입점: DI, 이미지 로더, 테마를 주입하고 [ProovApp]을 그린다. */
@Composable
fun ProovRoot(appGraph: AppGraph) {
    setSingletonImageLoaderFactory { appGraph.imageLoader }

    CompositionLocalProvider(LocalMetroViewModelFactory provides appGraph.metroViewModelFactory) {
        ProovTheme {
            ProovApp()
        }
    }
}
