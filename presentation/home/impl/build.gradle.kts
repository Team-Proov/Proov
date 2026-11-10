plugins {
    alias(libs.plugins.proov.kmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.presentation.home.api)
        }
    }
}
