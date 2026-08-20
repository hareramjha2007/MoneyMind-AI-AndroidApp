pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MoneyMind AI"
include(":app")
include(":core-domain")
include(":core-data")
include(":core-ui")
include(":feature-home")
include(":feature-insights")
include(":feature-goals")
include(":feature-coach")
include(":feature-profile")
