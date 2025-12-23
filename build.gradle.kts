import com.vanniktech.maven.publish.KotlinJvm
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.DokkaTask
import com.vanniktech.maven.publish.JavadocJar

val projectGroupId: String by project
val projectVersion: String by project
group = projectGroupId
version = projectVersion

plugins {
    kotlin("jvm") version "2.2.21"
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.owasp.dependencycheck") version "8.2.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
    id("com.vanniktech.maven.publish") version "0.33.0"
    id("com.github.ben-manes.versions") version "0.51.0"
}

repositories {
    mavenCentral()
}

dependencies {

}

kotlin {
    jvmToolchain(11)
}

detekt {
    ignoreFailures = false
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/detekt/detekt.yml")
}
tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        md.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
    }
}

tasks.withType<DokkaTask>().configureEach {
    outputDirectory.set(file("$rootDir/docs/dokka/ktor-openapi"))
}

mavenPublishing {
    val projectGroupId: String by project
    val projectArtifactId: String by project
    val projectVersion: String by project
    val projectBaseScmUrl: String by project
    val projectBaseScmConnection: String by project
    val projectLicenseName: String by project
    val projectLicenseUrl: String by project
    val projectDeveloperName: String by project
    val projectDeveloperUrl: String by project

    configure(KotlinJvm(JavadocJar.Dokka("dokkaHtml"), true))
    publishToMavenCentral(automaticRelease = true)
//    signAllPublications()
    coordinates(projectGroupId, projectArtifactId, projectVersion)
    pom {
        name.set("Ktor-Plus")
        description.set("Kotlin library that provides enhanced functionality on top of Ktor.")
        url.set(projectBaseScmUrl)
        licenses {
            license {
                name.set(projectLicenseName)
                url.set(projectLicenseUrl)
                distribution.set(projectLicenseUrl)
            }
        }
        scm {
            url.set(projectBaseScmUrl)
            connection.set(projectBaseScmConnection)
        }
        developers {
            developer {
                id.set(projectDeveloperName)
                name.set(projectDeveloperName)
                url.set(projectDeveloperUrl)
            }
        }
    }
}