import com.android.build.api.dsl.ApplicationExtension
import com.proov.proov.buildlogic.AppConfig
import com.proov.proov.buildlogic.DependencyManager
import com.proov.proov.buildlogic.PluginIds
import com.proov.proov.buildlogic.intVersion
import com.proov.proov.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/** namespace, applicationId, 버전 정보는 앱 고유 값이므로 모듈에서 선언한다. */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(PluginIds.ANDROID_APPLICATION)
        pluginManager.apply(PluginIds.KOTLIN_COMPOSE_COMPILER)
        // createGraphFactory를 쓰는 모듈이므로 컴파일러 플러그인이 필요하다
        pluginManager.apply(PluginIds.METRO)

        extensions.configure<ApplicationExtension> {
            compileSdk = libs.intVersion("android-compileSdk")
            defaultConfig {
                minSdk = libs.intVersion("android-minSdk")
                targetSdk = libs.intVersion("android-targetSdk")
            }
            packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
            buildTypes.getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            }
            compileOptions {
                sourceCompatibility = AppConfig.javaVersion
                targetCompatibility = AppConfig.javaVersion
            }
            buildFeatures.compose = true
        }
        extensions.configure<KotlinAndroidProjectExtension> {
            compilerOptions.jvmTarget.set(AppConfig.jvmTarget)
        }

        DependencyManager.addApplicationDependencies(this)
    }
}
