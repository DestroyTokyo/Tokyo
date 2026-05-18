plugins {
	id("java")
}

val projectVersion: String by project

group = "delta.cion.cherry.server"
version = projectVersion

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":CherryAPI"))
}

tasks {

	jar {
		manifest {
			attributes["Main-Class"] = "delta.cion.cherry.CherryServer"
		}
	}

	withType<JavaCompile> {
		options.encoding = "UTF-8"
	}

	build {
		dependsOn(shadowJar)
	}

	shadowJar {
		dependsOn(":CherryAPI:shadowJar")
		mergeServiceFiles()
		archiveClassifier.set("")
	}
}
