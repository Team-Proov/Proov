import com.proov.proov.buildlogic.DependencyManager
import com.proov.proov.buildlogic.PluginIds
import org.gradle.api.Plugin
import org.gradle.api.Project

class KmpSerializationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply(PluginIds.KMP_LIBRARY)
        pluginManager.apply(PluginIds.KOTLIN_SERIALIZATION)

        DependencyManager.addSerializationDependencies(this)
    }
}
