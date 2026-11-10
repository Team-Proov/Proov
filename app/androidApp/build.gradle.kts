plugins {
    alias(libs.plugins.proov.android.application)
}

android {
    namespace = "com.proov.proov"

    defaultConfig {
        applicationId = "com.proov.proov"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(projects.app.shared)
}
