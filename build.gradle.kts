plugins {
    id("java")
    kotlin("jvm") version "2.1.10"
    id("com.gradleup.shadow") version("8.3.0")
}

val minestomVersion: String by project
val logbackVersion: String by project
val jlineVersion: String by project
val jsonVersion: String by project

allprojects {
	apply(plugin = "java")
	apply(plugin = "com.gradleup.shadow")

	java {
		toolchain {
			languageVersion = JavaLanguageVersion.of(21)
		}
	}

	dependencies {
		compileOnly("net.minestom:minestom-snapshots:$minestomVersion")
		implementation("ch.qos.logback:logback-classic:${logbackVersion}")
		implementation("org.jline:jline:${jlineVersion}")
		implementation("org.json:json:${jsonVersion}")
	}

	repositories {
		mavenCentral()
	}
}
