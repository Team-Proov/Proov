package com.proov.proov.ui

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface MainUiState {
    data object Loading : MainUiState
    data object Ready : MainUiState
}

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // 비동기 작업이 없어 즉시 Ready. 로그인 세션 확인 등이 생기면 여기서 전이한다
        _uiState.value = MainUiState.Ready
    }
}
