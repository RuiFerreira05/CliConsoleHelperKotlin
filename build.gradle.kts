plugins {
    kotlin("jvm") version "2.4.20"
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

group = "com.serverofglass"
version = "1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

kotlin {
    jvmToolchain(11)
}

java {
    withSourcesJar()
}