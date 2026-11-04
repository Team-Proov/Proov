package com.proov.proov.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import com.proov.proov.core.designsystem.theme.Proov
import com.proov.proov.presentation.home.navigation.Home
import com.proov.proov.presentation.home.navigation.homeNavGraph
import com.proov.proov.ui.navigation.MainTab
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ProovApp(
    appState: ProovAppState = rememberProovAppState(),
    viewModel: MainViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState is MainUiState.Loading) return

    // MainRouteTab 라우트에서만 currentTab이 null이 아니므로 바텀 바가 보인다
    val currentTab = appState.currentTab

    Scaffold(
        containerColor = Proov.colors.neutral.n50,
        bottomBar = {
            ProovBottomBar(
                isVisible = currentTab != null,
                tabs = appState.tabs,
                currentTab = currentTab,
                onTabClick = appState::navigateToTab,
            )
        },
    ) { padding ->
        NavHost(
            navController = appState.navController,
            startDestination = Home,
            modifier = Modifier.padding(padding),
        ) {
            homeNavGraph()
        }
    }
}

@Composable
private fun ProovBottomBar(
    isVisible: Boolean,
    tabs: ImmutableList<MainTab>,
    currentTab: MainTab?,
    onTabClick: (MainTab) -> Unit,
) {
    if (!isVisible) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Proov.colors.neutral.n0)
            .navigationBarsPadding()
            .padding(vertical = 12.dp),
    ) {
        tabs.forEach { tab ->
            // 아이콘이 생기기 전까지 contentDescription을 라벨로 대신 쓴다
            Text(
                text = tab.contentDescription,
                style = Proov.typography.caption.c12,
                color = if (tab == currentTab) Proov.colors.neutral.n900 else Proov.colors.neutral.n500,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabClick(tab) },
            )
        }
    }
}
