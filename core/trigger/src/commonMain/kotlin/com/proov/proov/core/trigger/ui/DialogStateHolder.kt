package com.proov.proov.core.trigger.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.proov.proov.core.trigger.model.DialogState
import com.proov.proov.core.trigger.model.DialogType

@Stable
class DialogStateHolder {
    var dialogState by mutableStateOf(DialogState())
        private set

    /** 이미 떠 있으면 새로 호출된 다이얼로그로 교체한다. */
    fun showDialog(type: DialogType, onConfirm: () -> Unit) {
        dialogState = DialogState(type = type, onConfirm = onConfirm)
    }

    fun dismissDialog() {
        dialogState = DialogState()
    }
}

@Composable
fun rememberDialogStateHolder(): DialogStateHolder = remember {
    DialogStateHolder()
}
