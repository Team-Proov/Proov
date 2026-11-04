package com.proov.proov.presentation.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.proov.proov.core.navigation.MainRouteTab
import com.proov.proov.presentation.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object Home : MainRouteTab

fun NavController.navigateToHome(navOptions: NavOptions? = null) =
    navigate(Home, navOptions)

fun NavGraphBuilder.homeNavGraph(

) {
    composable<Home> {
        HomeRoute()
    }
}
