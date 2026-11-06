package com.proov.proov.core.common.util
sealed interface LoadErrorHandleAction {
    data object Retry : LoadErrorHandleAction

    data object Back : LoadErrorHandleAction

    data object NotFound : LoadErrorHandleAction
}
