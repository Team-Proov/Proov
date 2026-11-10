package com.proov.proov.buildlogic

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

/** convention plugin이 모듈에 추가하는 의존성을 기능별로 한곳에서 관리한다. */
internal object DependencyManager {

    fun addComposeDependencies(project: Project) = project.commonMain {
        implementation(project.libs.findBundle("compose-common").get())
    }

    fun addSerializationDependencies(project: Project) = project.commonMain {
        implementation(project.libs.findLibrary("kotlinx-serialization-json").get())
    }

    fun addFeatureDependencies(project: Project) = project.commonMain {
        // implementation은 전이되지 않으므로 feature마다 core를 직접 의존한다
        // build-logic은 별도 빌드라 projects 접근자를 쓸 수 없어 경로 문자열이 남는다
        implementation(project.project(":core:designsystem"))
        implementation(project.project(":core:navigation"))
        implementation(project.libs.findBundle("compose-feature").get())
    }

    fun addApplicationDependencies(project: Project) = project.dependencies {
        add("implementation", project.libs.findLibrary("androidx-activity-compose").get())
        add("implementation", project.libs.findLibrary("compose-uiToolingPreview").get())
        add("debugImplementation", project.libs.findLibrary("compose-uiTooling").get())
    }

    private fun Project.commonMain(block: KotlinDependencyHandler.() -> Unit) =
        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.getByName("commonMain").dependencies(block)
        }
}
