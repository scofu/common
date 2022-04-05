plugins {
    id("base-conventions")
}

dependencies {
    implementation(project(":common-inject"))
    api("com.jsoniter:jsoniter:0.9.23")
}

app {
    shadowFirstLevel()
}