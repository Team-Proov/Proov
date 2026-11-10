import com.proov.proov.buildlogic.DependencyManager
import com.proov.proov.buildlogic.PluginIds
import org.gradle.api.Plugin
import org.gradle.api.Project

class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(PluginIds.KMP_LIBRARY)
        pluginManager.apply(PluginIds.COMPOSE_MULTIPLATFORM)
        pluginManager.apply(PluginIds.KOTLIN_COMPOSE_COMPILER)

        DependencyManager.addComposeDependencies(this)
    }
}
