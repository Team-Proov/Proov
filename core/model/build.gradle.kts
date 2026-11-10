plugins {
    alias(libs.plugins.proov.kmp.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // 모델의 공개 프로퍼티 타입(ImmutableList 등)으로 노출되므로 api로 전이한다
            api(libs.kotlinx.immutable)
            // @Immutable/@Stable만 필요하므로 Compose 런타임 전체 대신 어노테이션 아티팩트만 쓴다
            implementation(libs.androidx.compose.runtimeAnnotation)
            // RefreshTrigger의 SharedFlow가 공개 API에 노출된다
            api(libs.kotlinx.coroutines.core)
        }
    }
}
