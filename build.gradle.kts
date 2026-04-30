import com.vanniktech.maven.publish.KotlinJvm
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.DokkaTask
import com.vanniktech.maven.publish.JavadocJar
import org.jetbrains.kotlin.gradle.internal.backend.common.serialization.metadata.DynamicTypeDeserializer.id

val projectGroupId: String by project
val projectVersion: String by project
group = projectGroupId
version = projectVersion

plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.owasp.dependencycheck") version "8.2.1"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("com.vanniktech.maven.publish") version "0.33.0"
    id("com.github.ben-manes.versions") version "0.51.0"
}

repositories {
    mavenCentral()
}

dependencies {
    // ktor
    val versionKtor = "3.4.3"
    implementation("io.ktor:ktor-server-core:$versionKtor")
    implementation("io.ktor:ktor-server-netty:$versionKtor")
    implementation("io.ktor:ktor-server-auth:${versionKtor}")
    implementation("io.ktor:ktor-server-content-negotiation:${versionKtor}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${versionKtor}")
    implementation("io.ktor:ktor-server-websockets:${versionKtor}")


    // OpenAPI
    val versionOpenApiTools = "5.7.0"
    implementation("io.github.smiley4:ktor-openapi:${versionOpenApiTools}")
    implementation("io.github.smiley4:ktor-swagger-ui:${versionOpenApiTools}")
    implementation("io.github.smiley4:ktor-redoc:${versionOpenApiTools}")

    // schema-kenerator
    val schemaKeneratorVersion = "2.7.0"
    implementation("io.github.smiley4:schema-kenerator-core:${schemaKeneratorVersion}")
    implementation("io.github.smiley4:schema-kenerator-serialization:${schemaKeneratorVersion}")
    implementation("io.github.smiley4:schema-kenerator-swagger:${schemaKeneratorVersion}")

    // testing
    val versionKotest = "5.9.1"
    testImplementation("io.kotest:kotest-runner-junit5:$versionKotest")
    testImplementation("io.kotest:kotest-assertions-core:$versionKotest")
    testImplementation("io.mockk:mockk:1.14.6")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
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
    outputDirectory.set(file("$rootDir/documentation/docs/dokka/ktor-plus"))
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
    signAllPublications()
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