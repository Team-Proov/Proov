package com.proov.proov.core.common.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@Composable
fun <T> Flow<T>.collectSideEffect(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit,
) {
    val lifecycle = lifecycleOwner.lifecycle

    LaunchedEffect(this, lifecycle) {
        flowWithLifecycle(lifecycle, minActiveState)
            .collect(action)
    }
}

/**
 * 500L delay로 throttle 방지 Side Effect 수신 확장함수
 * */
@Composable
fun <T> Flow<T>.collectSingleEvent(
    throttleTime: Long = 500L,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    action: suspend (T) -> Unit,
) {
    val lifecycle = lifecycleOwner.lifecycle
    val currentAction by rememberUpdatedState(action)

    LaunchedEffect(this, lifecycleOwner) {
        var lastEmitMark: TimeMark? = null

        flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect { value ->
            val elapsed = lastEmitMark?.elapsedNow()
            if (elapsed == null || elapsed > throttleTime.milliseconds) {
                lastEmitMark = TimeSource.Monotonic.markNow()
                currentAction(value)
            }
        }
    }
}
