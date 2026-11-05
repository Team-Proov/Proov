import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.proov.proov.buildlogic.DependencyManager
import com.proov.proov.buildlogic.PluginIds
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(PluginIds.KMP_COMPOSE)
        // type-safe 라우트(@Serializable)에 필요하다
        pluginManager.apply(PluginIds.KMP_SERIALIZATION)

        extensions.configure<KotlinMultiplatformExtension> {
            // composeResources 폴더의 Android 쪽 패키징에 필요하다
            (this as ExtensionAware).extensions
                .configure<KotlinMultiplatformAndroidLibraryTarget> {
                    androidResources { enable = true }
                }
        }
        DependencyManager.addFeatureDependencies(this)
    }
}
