plugins {
	id("java")
}

val projectVersion: String by project

group = "delta.cion.tokyo.test_plugin"
version = projectVersion

repositories {
	mavenCentral()
}

dependencies {
	compileOnly(project(":TokyoAPI"))
}

tasks {
	build {
		dependsOn(shadowJar)
	}

	withType<JavaCompile> {
		options.encoding = "UTF-8"
	}

	shadowJar {
		dependsOn(":TokyoAPI:shadowJar")
		mergeServiceFiles()
		archiveClassifier.set("")
	}
}
