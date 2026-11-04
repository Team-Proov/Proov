plugins {
    `kotlin-dsl`
}

dependencies {
    // compileOnly: plugin 클래스는 사용하는 프로젝트의 classpath에서 실제 구현을 가져온다
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.compose.compilerGradlePlugin)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "proov.kmp.library"
            implementationClass = "com.proov.proov.buildlogic.KmpLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = "proov.kmp.compose"
            implementationClass = "com.proov.proov.buildlogic.KmpComposeConventionPlugin"
        }
        register("kmpFeature") {
            id = "proov.kmp.feature"
            implementationClass = "com.proov.proov.buildlogic.KmpFeatureConventionPlugin"
        }
    }
}
