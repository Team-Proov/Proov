package com.kindl.proov.buildlogic

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("proov.kmp.compose")

        val designsystem = project(":core:designsystem")
        extensions.configure<KotlinMultiplatformExtension> {
            // composeResources 폴더의 Android 쪽 패키징에 필요하다
            (this as ExtensionAware).extensions
                .configure<KotlinMultiplatformAndroidLibraryTarget> {
                    androidResources { enable = true }
                }

            sourceSets.getByName("commonMain").dependencies {
                // implementation은 전이되지 않으므로 feature마다 core를 직접 의존한다
                implementation(designsystem)
                listOf(
                    "androidx-lifecycle-viewmodelCompose",
                    "compose-uiToolingPreview",
                    "androidx-lifecycle-runtimeCompose",
                    "compose-components-resources",
                ).forEach { implementation(libs.findLibrary(it).get()) }
            }
        }
    }
}
