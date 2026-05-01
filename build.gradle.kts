plugins {
    id("java")
    kotlin("jvm") version "2.1.10"
    id("com.gradleup.shadow") version("8.3.0")
}

val minestomVersion: String by project
val serverVersion: String by project

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.minestom:minestom-snapshots:$minestomVersion")
    compileOnly("ch.qos.logback:logback-classic:1.5.18")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "delta.cion.Server"
        }
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
    }
}