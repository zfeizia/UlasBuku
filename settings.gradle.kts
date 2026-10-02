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

// Remove ANDROID_PREFS_ROOT from ProcessEnvironment to prevent AGP AndroidLocationsException when both ANDROID_PREFS_ROOT and ANDROID_USER_HOME are injected by the IDE
try {
    val processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment")
    
    val theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment")
    theEnvironmentField.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    val map = theEnvironmentField.get(null) as MutableMap<String, String>
    map.remove("ANDROID_PREFS_ROOT")

    val theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment")
    theCaseInsensitiveEnvironmentField.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    val ciMap = theCaseInsensitiveEnvironmentField.get(null) as MutableMap<String, String>
    ciMap.remove("ANDROID_PREFS_ROOT")
} catch (_: Exception) {
    // ignore
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "UlasBuku"
include(":app")
