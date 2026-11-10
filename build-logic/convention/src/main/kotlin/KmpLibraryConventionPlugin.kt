import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.proov.proov.buildlogic.AppConfig
import com.proov.proov.buildlogic.PluginIds
import com.proov.proov.buildlogic.intVersion
import com.proov.proov.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(PluginIds.KOTLIN_MULTIPLATFORM)
        pluginManager.apply(PluginIds.ANDROID_KMP_LIBRARY)
        // 어노테이션이 조용히 무시되는 일을 막기 위해 모든 모듈에 적용한다
        pluginManager.apply(PluginIds.METRO)
        pluginManager.apply(PluginIds.KTLINT)

        extensions.configure<KotlinMultiplatformExtension> {
            iosArm64()
            iosSimulatorArm64()

            // kotlin { android { } } 는 KMP extension 안에 중첩된 extension이다
            (this as ExtensionAware).extensions
                .configure<KotlinMultiplatformAndroidLibraryTarget> {
                    namespace = AppConfig.namespaceOf(path)
                    compileSdk = libs.intVersion("android-compileSdk")
                    minSdk = libs.intVersion("android-minSdk")
                    compilerOptions.jvmTarget.set(AppConfig.jvmTarget)
                }
        }
    }
}
