package com.proov.proov.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.proov.proov.core.navigation.NavigationState
import com.proov.proov.core.navigation.Navigator
import com.proov.proov.core.navigation.rememberNavigationState
import com.proov.proov.core.network.monitor.NetworkMonitor
import com.proov.proov.ui.navigation.MainTab
import com.proov.proov.ui.navigation.ProovNavConfig
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Stable
class ProovAppState(
    val navigationState: NavigationState,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
) {
    val navigator = Navigator(navigationState)

    val isOffline: StateFlow<Boolean> = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val tabs: ImmutableList<MainTab> = MainTab.entries.toImmutableList()

    /** 화면에 보이는 키가 탭의 루트이면 해당 탭, 아니면 null이다. null이면 바텀 바를 숨긴다. */
    val currentTab: MainTab?
        get() = navigationState.currentKey.let { key -> MainTab.entries.find { it.route == key } }

    /** 탭 전환은 바텀 바에서만 호출한다. 이미 선택된 탭이면 그 탭의 루트로 돌아간다. */
    fun navigateToTab(tab: MainTab) = navigator.navigate(tab.route)
}

@Composable
fun rememberProovAppState(
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): ProovAppState {
    val navigationState = rememberNavigationState(
        configuration = ProovNavConfig,
        startKey = MainTab.entries.first().route,
        topLevelKeys = MainTab.entries.map { it.route }.toSet(),
    )
    return remember(networkMonitor, navigationState, coroutineScope) {
        ProovAppState(
            networkMonitor = networkMonitor,
            navigationState = navigationState,
            coroutineScope = coroutineScope,
        )
    }
}
