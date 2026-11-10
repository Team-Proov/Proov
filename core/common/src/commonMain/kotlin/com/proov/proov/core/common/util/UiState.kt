package com.proov.proov.core.common.util
import androidx.compose.runtime.Stable

@Stable
sealed interface UiState<out T> {
    data object Empty : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>

    data class Failure(val handleAction: LoadErrorHandleAction) : UiState<Nothing>
}
