plugins {
    id("base-conventions")
}

dependencies {
    implementation(project(":common-inject"))
    api("com.jsoniter:jsoniter:0.9.23")
    testImplementation("com.scofu:app-bootstrap-api:1.0-SNAPSHOT")
}

app {
    shadowFirstLevel()
}