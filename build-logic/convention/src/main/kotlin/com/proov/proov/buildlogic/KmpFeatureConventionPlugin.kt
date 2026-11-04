package com.proov.proov.buildlogic

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("proov.kmp.compose")
        // type-safe 라우트(@Serializable)에 필요하다
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        val designsystem = project(":core:designsystem")
        val navigation = project(":core:navigation")
        extensions.configure<KotlinMultiplatformExtension> {
            // composeResources 폴더의 Android 쪽 패키징에 필요하다
            (this as ExtensionAware).extensions
                .configure<KotlinMultiplatformAndroidLibraryTarget> {
                    androidResources { enable = true }
                }

            sourceSets.getByName("commonMain").dependencies {
                // implementation은 전이되지 않으므로 feature마다 core를 직접 의존한다
                implementation(designsystem)
                implementation(navigation)
                implementation(libs.findBundle("compose-feature").get())
            }
        }
    }
}
