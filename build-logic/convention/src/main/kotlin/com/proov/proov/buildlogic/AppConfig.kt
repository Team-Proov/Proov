package com.proov.proov.buildlogic

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/** 모든 모듈이 공유하는 빌드 상수와 규칙. */
internal object AppConfig {
    private const val JAVA_VERSION = 11

    val javaVersion: JavaVersion = JavaVersion.toVersion(JAVA_VERSION)
    val jvmTarget: JvmTarget = JvmTarget.fromTarget(JAVA_VERSION.toString())

    /** `:core:designsystem` -> `com.proov.proov.core.designsystem` */
    fun namespaceOf(projectPath: String): String =
        "com.proov.proov" + projectPath.replace(":", ".").replace("-", "_")
}

/** convention plugin이 pluginManager.apply에 넘기는 플러그인 ID. */
internal object PluginIds {
    const val KMP_LIBRARY = "proov.kmp.library"
    const val KMP_COMPOSE = "proov.kmp.compose"
    const val KMP_SERIALIZATION = "proov.kmp.serialization"

    const val KOTLIN_MULTIPLATFORM = "org.jetbrains.kotlin.multiplatform"
    const val KOTLIN_SERIALIZATION = "org.jetbrains.kotlin.plugin.serialization"
    const val KOTLIN_COMPOSE_COMPILER = "org.jetbrains.kotlin.plugin.compose"
    const val ANDROID_APPLICATION = "com.android.application"
    const val ANDROID_KMP_LIBRARY = "com.android.kotlin.multiplatform.library"
    const val COMPOSE_MULTIPLATFORM = "org.jetbrains.compose"
    const val METRO = "dev.zacsweers.metro"
    const val KTLINT = "org.jlleitschuh.gradle.ktlint"
}
