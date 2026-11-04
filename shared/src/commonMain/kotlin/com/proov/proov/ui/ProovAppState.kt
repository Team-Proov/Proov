package com.proov.proov.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.proov.proov.ui.navigation.MainTab
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Stable
class ProovAppState(
    val navController: NavHostController
) {
    val tabs: ImmutableList<MainTab> = MainTab.entries.toImmutableList()

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    /** 현재 목적지가 바텀 탭의 라우트이면 해당 탭, 아니면 null이다. null이면 바텀 바를 숨긴다. */
    val currentTab: MainTab?
        @Composable get() {
            val destination = currentDestination ?: return null
            return MainTab.find { destination.hasRoute(it::class) }
        }

    fun navigateToTab(tab: MainTab) {
        navController.navigate(tab.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

@Composable
fun rememberProovAppState(
    navController: NavHostController = rememberNavController(),
): ProovAppState = remember(navController) { ProovAppState(navController) }
