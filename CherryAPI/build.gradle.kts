plugins {
	id("java-library")
	id("maven-publish")
}

val projectVersion: String by project

val minestomVersion: String by project
val logbackVersion: String by project
val jsonVersion: String by project

group = "delta.cion.cherry.api"
version = projectVersion

dependencies {
	implementation("net.minestom:minestom-snapshots:$minestomVersion")
	implementation("ch.qos.logback:logback-classic:${logbackVersion}")
	implementation("org.json:json:${jsonVersion}")
}

tasks {

	build {
		dependsOn(shadowJar)
		dependsOn(publishToMavenLocal)
	}

	withType<JavaCompile> {
		options.encoding = "UTF-8"
	}

	shadowJar {
		mergeServiceFiles()
		archiveClassifier.set("")
	}

	processResources {
		filesMatching("version.properties") {
			expand("project.version" to project.version)
		}
	}

	register<Jar>("javadocJar") {
		dependsOn(javadoc)
		archiveClassifier.set("javadoc")
		from(javadoc.get().destinationDir)
	}

	register<Jar>("sourcesJar") {
		archiveClassifier.set("sources")
		from(sourceSets["main"].allSource)
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			groupId = group.toString()
			artifactId = "cherry_api"
			version = version

			val importString = "${groupId}:${artifactId}:${version}"
			println("String to import API: $importString")

			artifact(tasks["javadocJar"])
			artifact(tasks["sourcesJar"])
		}
	}
	repositories { mavenLocal() }
}

artifacts {
	add("archives", tasks["javadocJar"])
	add("archives", tasks["sourcesJar"])
}
