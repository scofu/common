import com.scofu.common.build.AppExtension
import gradle.kotlin.dsl.accessors._30870a47e380c7c13eee96805f88c56d.*
import gradle.kotlin.dsl.accessors._30870a47e380c7c13eee96805f88c56d.assemble
import gradle.kotlin.dsl.accessors._30870a47e380c7c13eee96805f88c56d.bukkit
import gradle.kotlin.dsl.accessors._30870a47e380c7c13eee96805f88c56d.publish
import gradle.kotlin.dsl.accessors._30870a47e380c7c13eee96805f88c56d.reobfJar
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.the

plugins {
    id("base-conventions")
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
    id("net.minecrell.plugin-yml.bukkit")
}

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
}

val app = the<AppExtension>()
// Default plugin.yml
bukkit {
    version = project.version as String
    description = project.description
    apiVersion = "1.18"
    authors = listOf("jesper@scofu.com")
    main = app.mainClass.getOrElse(".")
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    // Implicitly changes the jar to the re-obfuscated one and removes the 'dev' classifier.
    register("publishReObfuscated") {
        dependsOn(reobfJar)
        finalizedBy(publish)
        group = "Publishing"
        description = "Publishes the re-obfuscated jar in a *hacky* way."
        doFirst {
            publishing {
                publications {
                    named<MavenPublication>("mavenJava") {
                        artifactId = artifactId + "-reobf"
                    }
                }
            }
            project.tasks.named<Jar>("jar") {
                archiveClassifier.set("")
                from(reobfJar)
            }
        }
    }
}

