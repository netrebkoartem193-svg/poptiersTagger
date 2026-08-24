plugins {
    id("fabric-loom") version "1.8.13"
    id("maven-publish")
    kotlin("jvm") version "2.0.0"
}

version = "1.0.0"
group = "me.senseiwells"

base {
    archivesName.set("poptiers")
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://jitpack.io")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings("net.fabricmc:yarn:1.21.4+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.10")

    // Fabric API & Kotlin Language Adapter
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.114.0+1.21.4")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.13.0+kotlin.2.1.10")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to "1.21.4",
            "loader_version" to "0.16.10"
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}
