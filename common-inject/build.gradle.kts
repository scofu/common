plugins {
    id("base-conventions")
}

dependencies {
    api("com.google.inject:guice:5.1.0")
}

app {
    shadowFirstLevel()
}