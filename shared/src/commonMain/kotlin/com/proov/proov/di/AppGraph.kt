package com.proov.proov.di

import coil3.ImageLoader
import com.proov.proov.core.network.monitor.NetworkMonitor
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

/**
 * 플랫폼 공통 그래프 계약. `@DependencyGraph`는 모든 기여 모듈을 볼 수 있는 `shared`의
 * `androidMain`/`iosMain`에 정의한다(Metro는 그래프 소유 모듈의 클래스패스에서 기여를 수집한다).
 */
interface AppGraph : ViewModelGraph {
    val imageLoader: ImageLoader
    val networkMonitor: NetworkMonitor
}
