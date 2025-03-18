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
    versionCatalogs {
        create("samplAppLibs"){
            from(files("gradle/sampleAppLibs.versions.toml"))
        }
    }
}

rootProject.name = "ChatSDK"
include(":app")
include(":chat-it-ui")
include(":chat-it")
