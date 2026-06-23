
---

```txt
Базовый плагин должен содержать в себе plugin.properties
С main-class внутри. Это нужно банально, чтобы можно было определить, что вообще запускать.
```

---

```txt
build.gradle.kts должен содержать в себе:
	delta.cion.tokyo.api:tokyo_api:$ApiVersion
Обычно версия апишки совпадает с версией самого сервера.
Но это пока что. В дальнейшем всё может измениться.
Обратная совместимость сохраняется.

```
---

<h3 align="center">--==[ build.gradle.kts example ]==--</h3>

```kotlin
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
	// Take version from github or needed server version
	compileOnly("delta.cion.tokyo.api:tokyo_api:v1.0.0-predemo")
}

tasks {
	build {
		dependsOn(shadowJar)
	}

	withType<JavaCompile> {
		options.encoding = "UTF-8"
	}

	shadowJar {
		mergeServiceFiles()
		archiveClassifier.set("")
	}
}

```

---

<h3 align="center">--==[ Main class example ]==--</h3>

```java
package delta.cion.tokyo.test_plugin;

import plugin.delta.cion.tokyo.api.Plugin;

public class Main extends Plugin {

	// Set plugin name there
	public Main() {
		super("Tokyo-Test-Plugin");
	}

	// When plugin enabled
	@Override
	public void onEnable() {}

	// When plugin disabled
	@Override
	public void onDisable() {}
}

```

---

<h3 align="center">--==[ plugin.properties example ]==--</h3>

```properties
# Just a main class
main-class = delta.cion.tokyo.test_plugin.Main
```

---

<p align="center">
    <a href="#">
        <img src="https://img.shields.io/github/last-commit/DestroyTokyo/Tokyo?display_timestamp=committer&style=flat-square&color=000000"></a>
    <a href="#">
        <img src="https://img.shields.io/github/created-at/DestroyTokyo/Tokyo?style=flat-square&color=000000"></a>
</p>
