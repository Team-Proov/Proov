plugins {
    alias(libs.plugins.proov.kmp.compose)
}

kotlin {
    // 타깃 선언은 convention plugin이 하고, iOS Framework는 우산 모듈인 여기서만 만든다
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    android {
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }
        commonMain.dependencies {
            // AppGraph, MainTab의 공개 타입이므로 androidApp/iosApp까지 api로 전이한다
            api(projects.core.navigation)
            api(libs.metrox.viewmodel)
            api(libs.coil)
            // 그래프를 소유한 모듈이므로 기여 모듈을 모두 직접 의존해야 한다(Metro는 클래스패스에서 기여를 수집)
            implementation(projects.core.crypto)
            implementation(projects.core.designsystem)
            implementation(projects.core.localstorage)
            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(projects.core.trigger)
            implementation(projects.presentation.home)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.kermit)
            implementation(libs.bundles.compose.feature)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
