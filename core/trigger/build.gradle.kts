plugins {
    alias(libs.plugins.proov.kmp.serialization)
    alias(libs.plugins.proov.kmp.compose)
    alias(libs.plugins.proov.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.designsystem)
        }
    }
}

