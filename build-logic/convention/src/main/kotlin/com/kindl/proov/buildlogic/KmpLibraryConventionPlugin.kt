package com.kindl.proov.buildlogic

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.kotlin.multiplatform.library")

        extensions.configure<KotlinMultiplatformExtension> {
            iosArm64()
            iosSimulatorArm64()

            // kotlin { android { } } 는 KMP extension 안에 중첩된 extension이다
            (this as ExtensionAware).extensions
                .configure<KotlinMultiplatformAndroidLibraryTarget> {
                    // :core:designsystem -> com.kindl.proov.core.designsystem
                    namespace = "com.kindl.proov" + path.replace(":", ".").replace("-", "_")
                    compileSdk = libs.version("android-compileSdk").toInt()
                    minSdk = libs.version("android-minSdk").toInt()
                    compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
                }
        }
    }
}
