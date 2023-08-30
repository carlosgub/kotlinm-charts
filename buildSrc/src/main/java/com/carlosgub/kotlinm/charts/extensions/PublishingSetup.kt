package com.carlosgub.kotlinm.charts.extensions

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
    val gprRepoId = "kotlinm-charts"

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
    project.version = "1.0.2"

    val javadocJar = tasks.register<Jar>("javadocJar", Jar::class.java) {
        archiveClassifier.set("javadoc")
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
                version = "1.0.2"
                artifact(javadocJar)
            }
        }
    }
}
