package com.proov.proov.ui.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.proov.proov.presentation.home.api.Home
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * 백스택 저장·복원에 쓰는 설정. 리플렉션이 없는 iOS에서는 라우트를 명시해야 하므로,
 * presentation:*:api 모듈에 라우트를 추가하면 여기에도 등록한다.
 */
val ProovNavConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Home::class)
        }
    }
}
