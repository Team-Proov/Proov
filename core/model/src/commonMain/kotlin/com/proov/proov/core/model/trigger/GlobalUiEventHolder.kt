package com.proov.proov.core.model.trigger

import androidx.compose.runtime.Stable
import com.proov.proov.core.model.trigger.DialogTrigger
import com.proov.proov.core.model.trigger.SnackbarState

@Stable
class GlobalUiEventHolder(
    val dialogTrigger: DialogTrigger,
    val showToast: (String) -> Unit,
    val showSnackbar: (SnackbarState) -> Unit,
)
