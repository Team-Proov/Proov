package com.proov.proov.core.trigger

import androidx.compose.runtime.staticCompositionLocalOf
import com.proov.proov.core.trigger.model.GlobalUiEventHolder

val LocalGlobalUiEventTrigger = staticCompositionLocalOf<GlobalUiEventHolder> {
    error("No GlobalUiEvent Trigger provided")
}
