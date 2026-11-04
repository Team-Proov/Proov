import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

plugins {
    alias(libs.plugins.proov.kmp.library)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
}

// 서버 주소는 추적하지 않는 local.properties의 BASE_URL로 주입한다
val baseUrl = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.reader()?.use(::load)
}.getProperty("BASE_URL", "")

buildkonfig {
    packageName = "com.proov.proov.core.network"
    defaultConfigs {
        buildConfigField(STRING, "BASE_URL", baseUrl)
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // HttpClient가 공개 API에 노출되므로 api로 전이한다
            api(libs.ktor.client.core)
            implementation(libs.bundles.ktor.common)
            implementation(libs.kermit)
            implementation(libs.kotlinx.serialization.json)
        }
        // 엔진은 HttpClient()가 클래스패스에서 자동으로 고르므로 expect/actual이 필요 없다
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
