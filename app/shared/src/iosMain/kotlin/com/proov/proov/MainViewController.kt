package com.proov.proov

import androidx.compose.ui.window.ComposeUIViewController
import com.proov.proov.ui.ProovRoot
import dev.zacsweers.metro.createGraph
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

// MainViewController는 여러 번 호출될 수 있으므로 그래프는 한 번만 만든다
private val appGraph: IosAppGraph by lazy { createGraph<IosAppGraph>() }

@OptIn(ExperimentalNativeApi::class)
fun MainViewController() = ComposeUIViewController(
    configure = { initLogging(isDebug = Platform.isDebugBinary) },
) { ProovRoot(appGraph) }
