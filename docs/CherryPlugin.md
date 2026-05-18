
---

```txt
Базовый плагин должен содержать в себе plugin.properties
С main-class внутри. Это нужно банально, чтобы можно было определить, что вообще запускать.
```

---

<h3 align="center">--==[ Main class example ]==--</h3>

```java
package delta.cion.cherry.test_plugin;

import delta.cion.cherry.api.Plugin;

public class Main extends Plugin {

	// Set plugin name there
	public Main() {
		super("Cherry-Test-Plugin");
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

<p align="center">
    <a href="#">
        <img src="https://img.shields.io/github/last-commit/Nionim/MSNT?display_timestamp=committer&style=flat-square&color=000000"></a>
    <a href="#">
        <img src="https://img.shields.io/github/created-at/Nionim/MSNT?style=flat-square&color=000000"></a>
</p>
