plugins {
    id("java")
    kotlin("jvm") version "2.1.10"

	id("checkstyle") // CheckStyle (For package-info etc.)
    id("com.gradleup.shadow") version("8.3.0")
}

val checkstyleVersion: String by project
val minestomVersion: String by project
val logbackVersion: String by project
val jsonVersion: String by project

allprojects {
	apply(plugin = "java")
	apply(plugin = "checkstyle")
	apply(plugin = "com.gradleup.shadow")

	java {
		toolchain {
			languageVersion = JavaLanguageVersion.of(21)
		}
	}

	checkstyle {
		configFile = rootProject.file("config/checkstyle/checkstyle.xml")
	}

	dependencies {
		checkstyle("com.puppycrawl.tools:checkstyle:$checkstyleVersion")
		compileOnly("net.minestom:minestom-snapshots:$minestomVersion")
		compileOnly("ch.qos.logback:logback-classic:$logbackVersion")
		compileOnly("org.json:json:$jsonVersion")
	}

	repositories {
		mavenCentral()
	}
}
