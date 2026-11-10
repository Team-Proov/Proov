package com.proov.proov.core.common.util

val <T> UiState<T>.successData: T?
    get() = (this as? UiState.Success)?.data
