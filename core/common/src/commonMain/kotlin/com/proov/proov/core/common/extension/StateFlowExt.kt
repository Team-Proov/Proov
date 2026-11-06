package com.proov.proov.core.common.extension

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import com.proov.proov.core.common.util.UiState

inline fun <T> MutableStateFlow<UiState<T>>.updateSuccess(
    crossinline onUpdate: (T) -> T
) {
    update { currentState ->
        if (currentState is UiState.Success) {
            currentState.copy(data = onUpdate(currentState.data))
        } else {
            currentState
        }
    }
}
