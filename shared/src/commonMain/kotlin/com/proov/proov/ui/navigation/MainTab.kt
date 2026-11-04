package com.proov.proov.ui.navigation

import com.proov.proov.core.navigation.MainRouteTab
import com.proov.proov.core.navigation.Route
import com.proov.proov.presentation.home.navigation.Home

// 탭을 추가하려면 presentation 모듈의 MainRouteTab 라우트를 여기에 등록한다
enum class MainTab(
    val route: MainRouteTab,
    val contentDescription: String,
) {
    HOME(
        route = Home,
        contentDescription = "홈"
    );

    companion object {
        fun find(predicate: (Route) -> Boolean): MainTab? {
            return MainTab.entries.find { predicate(it.route) }
        }

        fun contains(predicate: (Route) -> Boolean): Boolean {
            return MainTab.entries.any { predicate(it.route) }
        }
    }
}
