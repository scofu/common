plugins {
    id("common-build-conventions")
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation("io.papermc.paperweight.userdev:io.papermc.paperweight.userdev.gradle.plugin:1.3.5")
    implementation("xyz.jpenilla:run-paper:1.0.6")
    implementation("net.minecrell.plugin-yml.bukkit:net.minecrell.plugin-yml.bukkit.gradle.plugin:0.5.1")
    implementation("net.minecrell.plugin-yml.bungee:net.minecrell.plugin-yml.bungee.gradle.plugin:0.5.1")
}

gradlePlugin {
    plugins {
        register("base") {
            id = "com.scofu.common-build.base"
            implementationClass = "BaseConventionsPlugin"
        }
        register("bukkit") {
            id = "com.scofu.common-build.bukkit"
            implementationClass = "BukkitConventionsPlugin"
        }
        register("bungee") {
            id = "com.scofu.common-build.bungee"
            implementationClass = "BungeeConventionsPlugin"
        }
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}