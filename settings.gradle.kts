rootProject.name = "Proov"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":app:androidApp")
include(":app:shared")

include(
    ":core:common",
    ":core:crypto",
    ":core:designsystem",
    ":core:localstorage",
    ":core:navigation",
    ":core:network",
    ":core:trigger",
    ":core:model"
)
include(
    ":presentation:home:api",
    ":presentation:home:impl",
)
