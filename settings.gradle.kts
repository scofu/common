pluginManagement {
    includeBuild("common-build/build-logic")
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven {
            name = "scofu"
            url = uri("https://repo.scofu.com/repository/maven-snapshots")
            credentials(PasswordCredentials::class)
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven {
            name = "scofu"
            url = uri("https://repo.scofu.com/repository/maven-snapshots")
            credentials(PasswordCredentials::class)
        }
    }
}

rootProject.name = "common-parent"

sequenceOf(
    "common-build",
    "common-api",
    "common-inject",
    "common-json",
    "egg"
).forEach {
    include(it)
    project(":$it").projectDir = file(it)
}
