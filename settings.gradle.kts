pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(
        RepositoriesMode.PREFER_SETTINGS
    )

    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "morphjump"

include(":core")
include(":desktop")
include(":android")