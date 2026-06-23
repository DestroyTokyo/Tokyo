plugins {
	id("java")
}

val projectVersion: String by project

group = "delta.cion.tokyo.server"
version = projectVersion

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":TokyoAPI"))
}

tasks {

	jar {
		manifest {
			attributes["Main-Class"] = "delta.cion.tokyo.server.Server"
		}
	}

	withType<JavaCompile> {
		options.encoding = "UTF-8"
	}

	build {
		dependsOn(shadowJar)
	}

	shadowJar {
		dependsOn(":TokyoAPI:shadowJar")
		mergeServiceFiles()
		archiveClassifier.set("")
	}
}
