plugins {
    id("com.scofu.common-build.base") version "1.0-SNAPSHOT"
}

dependencies {
    api(project(":common-api"))
    api("com.google.inject:guice:5.1.0")
}

app {
    shadowFirstLevel()
}