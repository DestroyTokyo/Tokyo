plugins {
	id("java")
}

val projectVersion: String by project

group = "delta.cion.cherry.test_plugin"
version = projectVersion

repositories {
	mavenCentral()
}

dependencies {
	compileOnly(project(":CherryAPI"))
}

tasks {
	build {
		dependsOn(shadowJar)
	}

	withType<JavaCompile> {
		options.encoding = "UTF-8"
	}

	shadowJar {
		dependsOn(":CherryAPI:shadowJar")
		mergeServiceFiles()
		archiveClassifier.set("")
	}
}
