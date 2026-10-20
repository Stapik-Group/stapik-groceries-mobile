pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "stapik-groceries-mobile"
include(":app")

val commonDir = providers.gradleProperty("stapikCommonMobileDir").getOrElse("libs/stapik-common-mobile")
apply(from = "$commonDir/stapik-common-mobile.settings.gradle.kts")
