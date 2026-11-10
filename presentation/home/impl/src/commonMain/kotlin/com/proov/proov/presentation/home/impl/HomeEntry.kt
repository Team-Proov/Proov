package com.proov.proov.presentation.home.impl

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.proov.proov.core.navigation.Navigator
import com.proov.proov.presentation.home.api.Home

fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
    entry<Home> {
        HomeRoute()
    }
}
