package com.proov.proov.core.trigger.model

import androidx.compose.runtime.Immutable

@Immutable
data class SnackbarState(
    val message: String = "",
    val bottomPadding: Int = 30
)
