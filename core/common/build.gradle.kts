plugins {
    alias(libs.plugins.proov.kmp.compose)
    alias(libs.plugins.proov.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // implementation만 사용해 소비 모듈로 전이되지 않게 한다
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.compose.runtimeAnnotation)
        }
    }
}
