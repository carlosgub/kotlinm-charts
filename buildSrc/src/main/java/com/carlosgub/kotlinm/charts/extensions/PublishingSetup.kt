package com.netguru.multiplatform.charts.extensions

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.the
import java.util.*

fun Project.publishingSetup() {

    val gprBaseUrl = "https://maven.pkg.github.com"
    val gprRepoOwner = "carlosgub"
    val gprRepoId = "compose-multiplatform-charts"

    var properties: Properties? = null
    val secretPropsFile = project.rootProject.file("local.properties")
    if (secretPropsFile.exists()) {
        secretPropsFile.reader().use {
            properties = Properties().apply { load(it) }
        }
    }

    fun getGprUser(): String {
        return properties?.getProperty("gpr.user") ?: System.getenv("GPR_USER")
    }

    fun getGprKey(): String {
        return properties?.getProperty("gpr.key") ?: System.getenv("GPR_KEY")
    }
    val libs = project.the<LibrariesForLibs>()

    project.group = libs.versions.project.group.get()
    project.version = version



    project.extra["signing.keyId"] = properties?.getProperty("signing.keyId") ?: System.getenv("SIGNING_KEY_ID")
    project.extra["signing.password"] = properties?.getProperty("signing.password") ?: System.getenv("SIGNING_PASSWORD")
    project.extra["ossrhUsername"] = properties?.getProperty("ossrhUsername") ?: System.getenv("OSSRH_USERNAME")
    project.extra["ossrhPassword"] = properties?.getProperty("ossrhPassword") ?: System.getenv("OSSRH_PASSWORD")
    if (properties?.getProperty("signing.secretKeyRingFile") != null) {
        project.extra["signing.secretKeyRingFile"] = properties?.getProperty("signing.secretKeyRingFile")
    } else {
        project.extra["signing.secretKey"] = properties?.getProperty("signing.secretKey") ?: System.getenv("SIGNING_SECRET_KEY")
    }

    val javadocJar = tasks.register<Jar>("javadocJar", Jar::class.java) {
        archiveClassifier.set("javadoc")
        from(tasks.named("dokkaHtml"))
    }

    publishing {

        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("$gprBaseUrl/$gprRepoOwner/$gprRepoId")
                credentials {
                    username = getGprUser()
                    password = getGprKey()
                }
            }
        }

        val mavenArtifactId = project.name

        publications {
            register("gprRelease", MavenPublication::class) {
                groupId = "com.carlosgub.libraries"
                artifactId = mavenArtifactId
                version = "1.0.0"
                artifact(javadocJar)
            }
        }
    }
}
