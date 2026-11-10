plugins {
    alias(libs.plugins.proov.kmp.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // 키가 Route를 상속하므로 공개 API로 노출한다
            api(projects.core.navigation)
        }
    }
}
