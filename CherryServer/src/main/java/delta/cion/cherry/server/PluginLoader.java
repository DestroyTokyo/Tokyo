package delta.cion.cherry.server.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URLClassLoader;
import java.util.*;

public class PluginLoader {

	private final File pluginFolder;
	private final Map<UUID, Plugin> plugins = new HashMap<>();
	private final Map<File, Long> extensionTimestamps = new HashMap<>();

	private final Logger LOGGER = LoggerFactory.getLogger(PluginLoader.class);

	public PluginLoader(String pathName) {
		this.pluginFolder = new File(pathName);
	}

	public void loadExtensions() {
		if (!pluginFolder.exists()) pluginFolder.mkdirs();


		for (File jarFile : getNewExtensions()) loadJarExtension(jarFile);
		LOGGER.info("Total plugins loaded: {}", plugins.size());
		enableAll();
	}

	private void loadJarExtension(File jarFile) {
		Plugin plugin = isValidExtension(jarFile);
		if (plugin == null) return;
		plugins.put(plugin.getId(), plugin);
		extensionTimestamps.put(jarFile, jarFile.lastModified());
		plugin.onLoad();
		LOGGER.info("Loaded plugin: {}", plugin.getId());
	}

	private Plugin isValidExtension(File jarFile) {
		try {
			URLClassLoader classLoader = new URLClassLoader(
				new java.net.URL[] { jarFile.toURI().toURL() },
				Plugin.class.getClassLoader()
			);

			Properties properties = new Properties();
			try (java.io.InputStream in = classLoader.getResourceAsStream("plugin.properties")) {
				if (in == null) {
					LOGGER.warn("No plugin.properties found in {}", jarFile.getName());
					return null;
				}
				properties.load(in);
			}

			String mainClassName = properties.getProperty("main-class");
			if (mainClassName == null) {
				LOGGER.warn("Missing 'main-class' in {}", jarFile.getName());
				return null;
			}

			Class<?> clazz = Class.forName(mainClassName, true, classLoader);
			Object instance = clazz.getDeclaredConstructor().newInstance();

			if (instance instanceof Plugin) return (Plugin) instance;
			else {
				LOGGER.error("Class {} does not implement Plugin", mainClassName);
				return null;
			}
		} catch (Exception ex) {
			LOGGER.error("Invalid plugin JAR: {}: {}", jarFile.getName(), ex.getMessage());
			return null;
		}
	}

	public void updateExtensions() {
		LOGGER.info("Checking for plugin updates...");
		for (File jarFile : getRemovedExtensions()) removeExtension(jarFile);
		for (File jarFile : getModifiedExtensions()) reloadExtension(jarFile);
		for (File jarFile : getNewExtensions()) loadJarExtension(jarFile);
		LOGGER.info("Plugin update check completed.");
	}

	private void removeExtension(File jarFile) {
		UUID id = getExtensionIdByFile(jarFile);
		if (id == null) return;
		disableById(id);
		unload(id);
		LOGGER.info("Removed plugin: {}", jarFile.getName());
	}

	private void reloadExtension(File jarFile) {
		UUID id = getExtensionIdByFile(jarFile);
		LOGGER.info("Modified plugin detected: {}. Reloading...", jarFile.getName());

		if (id != null) {
			disableById(id);
			unload(id);
		}
		loadJarExtension(jarFile);
	}

	private UUID getExtensionIdByFile(File jarFile) {
		for (Map.Entry<UUID, Plugin> entry : plugins.entrySet()) {
			String simpleName = entry.getValue().getClass().getSimpleName();
			if (jarFile.getName().contains(simpleName)) return entry.getKey();
		}
		return null;
	}

	private Set<File> getNewExtensions() {
		Set<File> jarFiles = getJarFiles();
		jarFiles.removeAll(extensionTimestamps.keySet());
		return jarFiles;
	}

	private Set<File> getRemovedExtensions() {
		Set<File> removed = new HashSet<>(extensionTimestamps.keySet());
		removed.removeAll(getJarFiles());
		return removed;
	}

	private Set<File> getModifiedExtensions() {
		Set<File> modified = new HashSet<>();
		for (File file : getJarFiles()) {
			Long storedTimestamp = extensionTimestamps.get(file);
			if (storedTimestamp != null && file.lastModified() > storedTimestamp) modified.add(file);
		}
		return modified;
	}

	private Set<File> getJarFiles() {
		File[] files = pluginFolder.listFiles((dir, name) -> name.endsWith(".jar"));
		Set<File> result = new HashSet<>();
		if (files != null) Collections.addAll(result, files);
		return result;
	}

	public void enableById(UUID id) {
		Plugin ext = plugins.get(id);
		if (ext != null) {
			ext.onEnable();
			ext.updateStatus(Plugin.Status.ENABLED);
		}
	}

	public void disableById(UUID id) {
		Plugin ext = plugins.get(id);
		if (ext != null) {
			ext.onDisable();
			ext.updateStatus(Plugin.Status.DISABLED);
		}
	}

	public void enableAll() {
		for (Plugin ext : plugins.values()) ext.onEnable();
	}

	public void disableAll() {
		for (Plugin ext : plugins.values()) {
			ext.onDisable();
		}
	}

	public void unload(UUID id) {
		Plugin removed = plugins.remove(id);
		if (removed != null) removed.onDisable();
		extensionTimestamps.entrySet().removeIf(entry ->
			entry.getKey().getName().contains(id.toString()));
	}

	public void updateExtensionById(UUID id) {
		for (File jarFile : getModifiedExtensions()) {
			if (jarFile.getName().contains(id.toString())) {
				reloadExtension(jarFile);
				break;
			}
		}
	}

	public int size() {
		return plugins.size();
	}
}
