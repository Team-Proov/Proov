plugins {
    id("proov.kmp.compose")
}

kotlin {
    android {
        // 폰트 같은 Compose Resources를 Android 라이브러리에 패키징하려면 필요
        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Res 클래스 생성과 Font(Res.font.xxx) 사용에 필요
            implementation(libs.compose.components.resources)
        }
    }
}
