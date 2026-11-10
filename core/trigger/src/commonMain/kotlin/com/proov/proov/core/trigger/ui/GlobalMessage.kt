package com.proov.proov.core.trigger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.proov.proov.core.designsystem.theme.Proov
import com.proov.proov.core.trigger.model.DialogTrigger
import com.proov.proov.core.trigger.model.GlobalUiEventHolder
import com.proov.proov.core.trigger.model.SnackbarState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val TOAST_DURATION_MILLIS = 2000L
private const val SNACKBAR_DURATION_MILLIS = 2000L

/** 같은 문구를 연속으로 띄워도 상태가 바뀌도록 값이 아닌 인스턴스로 구분한다. */
internal class ToastMessage(val text: String)

@Stable
class GlobalMessageState {
    val snackbarHostState = SnackbarHostState()
    internal var toast by mutableStateOf<ToastMessage?>(null)

    /** 표시 중인 스낵바. 없으면 null이며, 토스트의 하단 여백 계산에도 쓰인다. */
    internal var snackbar by mutableStateOf<SnackbarState?>(null)
}

@Composable
fun rememberGlobalMessageState(): GlobalMessageState = remember { GlobalMessageState() }

@Composable
fun rememberGlobalUiEventHolder(state: GlobalMessageState, dialogStateHolder: DialogStateHolder): GlobalUiEventHolder {
    val coroutineScope = rememberCoroutineScope()
    return remember(state, dialogStateHolder, coroutineScope) {
        GlobalUiEventHolder(
            dialogTrigger = DialogTrigger(
                show = dialogStateHolder::showDialog,
                dismiss = dialogStateHolder::dismissDialog,
            ),
            showToast = { state.toast = ToastMessage(it) },
            showSnackbar = { snackbar ->
                coroutineScope.launch {
                    state.snackbarHostState.currentSnackbarData?.dismiss()
                    state.snackbar = snackbar

                    val job = launch {
                        state.snackbarHostState.showSnackbar(
                            message = snackbar.message,
                            duration = SnackbarDuration.Indefinite,
                        )
                    }
                    job.invokeOnCompletion {
                        // 같은 문구가 연속될 수 있으므로 값이 아닌 인스턴스로 비교한다
                        if (state.snackbar === snackbar) state.snackbar = null
                    }
                    delay(SNACKBAR_DURATION_MILLIS.milliseconds)
                    job.cancel()
                }
            },
        )
    }
}

/** Scaffold의 snackbarHost 슬롯에 넣는다. 토스트와 스낵바를 같은 위치에 표시한다. */
@Composable
fun GlobalMessageHost(state: GlobalMessageState, modifier: Modifier = Modifier) {
    val toast = state.toast
    LaunchedEffect(toast) {
        if (toast != null) {
            delay(TOAST_DURATION_MILLIS.milliseconds)
            state.toast = null
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = (state.snackbar?.bottomPadding ?: 0).dp),
    ) {
        toast?.let {
            Text(
                text = it.text,
                style = Proov.typography.caption.c12,
                color = Proov.colors.neutral.n0,
                modifier = Modifier
                    .background(Proov.colors.neutral.n900, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
        SnackbarHost(hostState = state.snackbarHostState)
    }
}
