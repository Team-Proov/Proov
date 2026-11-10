plugins {
    alias(libs.plugins.proov.kmp.compose)
}

kotlin {
    android {
        withHostTest {}
    }

    sourceSets {
        commonMain.dependencies {
            // Route가 NavKey를 상속하고 NavigationState가 백스택 타입을 노출하므로 공개 API로 둔다
            api(libs.navigation3.ui)
            api(libs.androidx.lifecycle.viewmodelNavigation3)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
