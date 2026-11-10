plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.metro) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
}

// 모듈 의존 규칙(docs/architecture/modularization.md)을 구성 시점에 검사한다. 규칙이 늘면 아래 목록에 추가한다.
gradle.projectsEvaluated {
    val infra = setOf(":core:network", ":core:crypto", ":core:localstorage")
    val violations = subprojects.flatMap { module ->
        val deps = module.configurations
            .flatMap { it.dependencies.withType<ProjectDependency>() }
            .map { it.path }
            .toSet()
        deps.mapNotNull { dep ->
            val reason = when {
                module.path.startsWith(":presentation:") && dep in infra ->
                    "presentation은 인프라 모듈을 직접 참조할 수 없다"
                module.path.startsWith(":presentation:") && dep.startsWith(":app:") ->
                    "presentation은 app을 참조할 수 없다"
                module.path.startsWith(":core:") && (dep.startsWith(":presentation:") || dep.startsWith(":app:")) ->
                    "core는 presentation과 app을 참조할 수 없다"
                module.path.endsWith(":impl") && dep.endsWith(":impl") && module.path != dep ->
                    "impl은 다른 기능의 impl을 참조할 수 없다(api만 참조)"
                else -> null
            }
            reason?.let { "${module.path} -> $dep: $it" }
        }
    }.sorted()

    tasks.register("verifyModuleDependencies") {
        group = "verification"
        description = "모듈 의존 방향 규칙 위반을 검사한다"
        doLast {
            check(violations.isEmpty()) { "모듈 의존 규칙 위반:\n" + violations.joinToString("\n") }
        }
    }
}
