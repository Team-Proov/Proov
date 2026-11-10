package com.proov.proov.core.trigger.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/** [type]이 null이면 다이얼로그를 표시하지 않는다. */
@Immutable
data class DialogState(
    val type: DialogType? = null,
    val onConfirm: () -> Unit = {},
)

@Stable
class DialogTrigger(
    val show: (DialogType, () -> Unit) -> Unit,
    val dismiss: () -> Unit,
)
