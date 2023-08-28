plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm").version("1.9.0")
}

repositories {
    google()
    mavenCentral()
}
dependencies {
    // Required to access libs inside our plugin in buildSrc
    implementation(libs.plugin.android)
    implementation(libs.plugin.kotlin)
    implementation(gradleKotlinDsl())
    implementation(libs.plugin.poet)
}
