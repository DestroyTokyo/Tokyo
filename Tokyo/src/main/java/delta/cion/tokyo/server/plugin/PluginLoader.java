package delta.cion.tokyo.server.plugin;

import delta.cion.tokyo.api.plugin.Plugin;
import delta.cion.tokyo.api.plugin.PluginStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

class PluginLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(PluginLoader.class);
	private static final File pluginFolder = new File("plugins");

	private final Map<String, Plugin> plugins = new HashMap<>();
	private final Map<File, Long> pluginTimestamps = new HashMap<>();
	private final Map<String, ClassLoader> pluginClassLoaders = new HashMap<>();
	private final Map<String, List<String>> pluginDependencies = new HashMap<>();

	private ClassLoader apiClassLoader;

	public PluginLoader() {
		if (pluginFolder.exists()) return;
		if (pluginFolder.mkdirs()) LOGGER.info("plugins dir created");
	}

	public void loadPlugins() {
		loadApiPlugins();

		for (File jarFile : getJarFiles()) {
			if (isApiPlugin(jarFile)) continue;
			loadJarPlugin(jarFile);
		}

		LOGGER.info("Total plugins loaded: {}", plugins.size());
		enableAll();
	}

	public void updatePlugins() {
		for (File jarFile : getRemovedPlugins()) removePlugin(jarFile);
		for (File jarFile : getModifiedPlugins()) reloadPlugin(jarFile);
		for (File jarFile : getNewPlugins())
			if (!isApiPlugin(jarFile)) loadJarPlugin(jarFile);
		LOGGER.info("Plugins updated");
	}

	public void updatePlugin(String id) {
		if (plugins.get(id) == null)
			throw new RuntimeException("Cannot update plugin " + id + ": not loaded");

		File pluginFile = findJarFileByPluginId(id);
		if (pluginFile == null)
			throw new RuntimeException("Cannot find jar file for plugin " + id);

		long lastModified = pluginFile.lastModified();
		long modified = pluginTimestamps.getOrDefault(pluginFile, 0L);

		if (lastModified > modified) reloadPlugin(pluginFile);
		else LOGGER.info("Plugin {} is up to date", id);
	}

	public void enableAll() {
		List<String> sorted = topologicalSort(new ArrayList<>(plugins.keySet()));
		for (String id : sorted) enablePlugin(id);
	}

	public void disableAll() {
		List<String> sorted = topologicalSort(new ArrayList<>(plugins.keySet()));
		Collections.reverse(sorted);
		for (String id : sorted) disablePlugin(id);
	}

	public void enablePlugin(String id) {
		Plugin plugin = plugins.get(id);
		if (plugin == null) {
			LOGGER.warn("Cannot enable plugin {}: not loaded", id);
			return;
		}
		if (plugin.getStatus() == PluginStatus.ENABLED) {
			LOGGER.debug("Plugin {} already enabled", id);
			return;
		}

		List<String> dependencies = pluginDependencies.getOrDefault(id, Collections.emptyList());
		for (String depend : dependencies) {
			if (!plugins.containsKey(depend)) {
				LOGGER.error("Cannot enable {}: missing required dependency {}", id, depend);
				return;
			}
			if (plugins.get(depend).getStatus() != PluginStatus.ENABLED) {
				LOGGER.info("Enabling dependency {} for {}", depend, id);
				enablePlugin(depend);
			}
		}

		try {
			plugin.sysINIT();
			LOGGER.info("Plugin {} has been enabled", id);
		} catch (Exception e) {
			LOGGER.error("Failed to enable plugin {}: {}", id, e.getMessage());
		}
	}

	public void disablePlugin(String id) {
		Plugin plugin = plugins.get(id);
		if (plugin == null) {
			LOGGER.warn("Cannot disable plugin {}: not loaded", id);
			return;
		}
		if (plugin.getStatus() == PluginStatus.DISABLED) {
			LOGGER.debug("Plugin {} already disabled", id);
			return;
		}

		for (Map.Entry<String, List<String>> entry : pluginDependencies.entrySet()) {
			String dependent = entry.getKey();
			if (entry.getValue().contains(id)) {
				Plugin depPlugin = plugins.get(dependent);
				if (depPlugin != null && depPlugin.getStatus() == PluginStatus.ENABLED) {
					LOGGER.warn("Cannot disable {} because it's required by {}", id, dependent);
					return;
				}
			}
		}

		try {
			plugin.sysDISABLE();
			LOGGER.info("Plugin {} has been disabled", id);
		} catch (Exception e) {
			LOGGER.error("Failed to disable plugin {}: {}", id, e.getMessage());
		}
	}

	public void unload(String id) {
		Plugin plugin = plugins.remove(id);
		if (plugin != null) {
			try {
				if (plugin.getStatus() == PluginStatus.ENABLED) disablePlugin(plugin.getId());
				ClassLoader classLoader = pluginClassLoaders.remove(id);
				if (classLoader instanceof URLClassLoader) ((URLClassLoader) classLoader).close();

				pluginDependencies.remove(id);

				pluginTimestamps.entrySet().removeIf(entry ->
					entry.getKey().getName().contains(id) || entry.getKey().getName().contains(plugin.getName())
				);
				LOGGER.info("Plugin {} has been unloaded", id);
			} catch (Exception e) {
				LOGGER.error("Cannot unload plugin {}: {}", id, e.getMessage());
			}
		} else {
			LOGGER.warn("Cannot unload plugin {}. It is not loaded", id);
		}
	}

	public int size() {
		return plugins.size();
	}

	private void loadApiPlugins() {
		List<File> apiJars = new ArrayList<>();
		for (File jar : getJarFiles()) {
			if (isApiPlugin(jar)) apiJars.add(jar);
		}
		if (apiJars.isEmpty()) return;

		LOGGER.info("Loading {} API plugin(s)", apiJars.size());
		List<URL> urls = new ArrayList<>();
		for (File jar : apiJars) {
			try {
				urls.add(jar.toURI().toURL());
			} catch (Exception e) {
				LOGGER.error("Invalid URL for {}", jar.getName(), e);
			}
		}
		apiClassLoader = new PluginClassLoader(
			urls.toArray(new URL[0]),
			PluginLoader.class.getClassLoader(),
			null
		);

		for (File jar : apiJars) {
			loadApiPluginIntoLoader(jar, apiClassLoader);
		}
	}

	private boolean isApiPlugin(File jar) {
		try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()}, null)) {
			Properties props = new Properties();
			try (InputStream in = cl.getResourceAsStream("plugin.properties")) {
				if (in == null) return false;
				props.load(in);
				return "true".equalsIgnoreCase(props.getProperty("api-share"));
			}
		} catch (Exception e) {
			return false;
		}
	}

	private void loadApiPluginIntoLoader(File jar, ClassLoader loader) {
		try {
			Properties props = new Properties();
			try (InputStream in = loader.getResourceAsStream("plugin.properties")) {
				if (in == null) {
					LOGGER.warn("No plugin.properties in jar {}", jar.getName());
					return;
				}
				props.load(in);
			}

			String mainClass = props.getProperty("main-class");
			String pluginId = props.getProperty("plugin-id");
			String pluginName = props.getProperty("plugin-name", pluginId);
			String pluginVersion = props.getProperty("plugin-version", "unknown");

			if (mainClass == null || pluginId == null) {
				LOGGER.warn("Missing main-class or plugin-id in {}", jar.getName());
				return;
			}

			Class<?> clazz = Class.forName(mainClass, true, loader);
			Object instance = clazz.getDeclaredConstructor(
				String.class, String.class, String.class
			).newInstance(pluginId, pluginName, pluginVersion);

			if (!(instance instanceof Plugin plugin)) {
				LOGGER.error("{} main class does not extend Plugin", pluginName);
				return;
			}

			plugins.put(pluginId, plugin);
			pluginTimestamps.put(jar, jar.lastModified());
			pluginClassLoaders.put(pluginId, loader);

			List<String> depend = parseDependencyList(props.getProperty("dependencies"));
			pluginDependencies.put(pluginId, depend);

			LOGGER.info("Loaded plugin: {} {}", pluginId, pluginVersion);
		} catch (Exception e) {
			LOGGER.error("Failed to load plugin from {}: {}", jar.getName(), e.getMessage());
		}
	}

	private void loadJarPlugin(File jarFile) {
		Plugin plugin = loadPluginFromJar(jarFile);
		if (plugin == null) return;
		String id = plugin.getId();
		if (plugins.containsKey(id)) {
			LOGGER.warn("Plugin with id {} already loaded, skipping {}", id, jarFile.getName());
			return;
		}
		plugins.put(id, plugin);
		pluginTimestamps.put(jarFile, jarFile.lastModified());
		LOGGER.info("Loaded plugin: {} v{} from {}", id, plugin.getVersion(), jarFile.getName());
	}

	private Plugin loadPluginFromJar(File jar) {
		PluginClassLoader classLoader = null;
		try {
			String jarName = jar.getName();
			Properties props = new Properties();

			try (URLClassLoader tempLoader = new URLClassLoader(new URL[]{jar.toURI().toURL()}, null)) {
				try (InputStream in = tempLoader.getResourceAsStream("plugin.properties")) {
					if (in == null) {
						LOGGER.warn("Cannot find plugin.properties in {}", jarName);
						return null;
					}
					props.load(in);
				}
			}

			String mainClass = props.getProperty("main-class");
			if (mainClass == null) {
				LOGGER.error("No main-class in {}", jarName);
				return null;
			}

			String pluginId = props.getProperty("plugin-id");
			if (pluginId == null) {
				LOGGER.error("No plugin-id in {}", jarName);
				return null;
			}

			String pluginName = props.getProperty("plugin-name", pluginId);
			String pluginVersion = props.getProperty("plugin-version", "unknown");
			List<String> depend = parseDependencyList(props.getProperty("dependencies"));
			pluginDependencies.put(pluginId, depend);

			if (hasCycle(pluginId, depend, new HashSet<>(), new HashSet<>())) {
				LOGGER.error("Cycle detected involving plugin {}! It may not work correctly.", pluginId);
			}

			ClassLoader parent = PluginLoader.class.getClassLoader();
			classLoader = new PluginClassLoader(
				new URL[]{jar.toURI().toURL()},
				parent, apiClassLoader
			);

			Class<?> pluginClass = Class.forName(mainClass, true, classLoader);
			Object instance = pluginClass.getDeclaredConstructor(
				String.class, String.class, String.class
			).newInstance(pluginId, pluginName, pluginVersion);

			if (!(instance instanceof Plugin)) {
				LOGGER.error("Main class not extend Plugin: {}", mainClass);
				return null;
			}

			pluginClassLoaders.put(pluginId, classLoader);
			return (Plugin) instance;
		} catch (Exception e) {
			LOGGER.error("Cannot load {}: {}", jar.getName(), e.getMessage());
			closeQuietly(classLoader);
			return null;
		}
	}

	private List<String> parseDependencyList(String value) {
		if (value == null || value.trim().isEmpty())
			return Collections.emptyList();
		return Arrays.stream(value.split(","))
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.collect(Collectors.toList());
	}

	private List<String> topologicalSort(List<String> pluginIds) {
		Map<String, Set<String>> graph = new HashMap<>();
		for (String id : pluginIds) {
			graph.put(id, new HashSet<>(pluginDependencies.getOrDefault(id, Collections.emptyList())));
		}

		List<String> result = new ArrayList<>();
		Set<String> visited = new HashSet<>();
		Set<String> tempMark = new HashSet<>();

		for (String id : pluginIds) {
			if (!visited.contains(id)) {
				if (dfs(id, graph, visited, tempMark, result)) {
					LOGGER.error("Cycle dependencies in plugin list detected.");
				}
			}
		}
		return result;
	}

	private boolean dfs(String node, Map<String, Set<String>> graph,
						Set<String> visited, Set<String> tempMark,
						List<String> result) {
		if (tempMark.contains(node)) return true;
		if (visited.contains(node)) return false;
		tempMark.add(node);
		for (String dep : graph.getOrDefault(node, Collections.emptySet())) {
			if (!plugins.containsKey(dep)) continue;
			if (dfs(dep, graph, visited, tempMark, result)) return true;
		}
		tempMark.remove(node);
		visited.add(node);
		result.add(node);
		return false;
	}

	private boolean hasCycle(String node, List<String> deps, Set<String> visited, Set<String> stack) {
		if (stack.contains(node)) return true;
		if (visited.contains(node)) return false;
		visited.add(node);
		stack.add(node);
		for (String dep : deps) {
			List<String> subDeps = pluginDependencies.getOrDefault(dep, Collections.emptyList());
			if (hasCycle(dep, subDeps, visited, stack)) return true;
		}
		stack.remove(node);
		return false;
	}

	private void reloadPlugin(File jarFile) {
		String pluginId = findPluginIdByJarFile(jarFile);
		if (pluginId != null) {
			disablePlugin(pluginId);
			unload(pluginId);
		}
		if (isApiPlugin(jarFile)) {
			loadApiPlugins();
			for (File f : getJarFiles())
				if (!isApiPlugin(f)) loadJarPlugin(f);
		} else {
			loadJarPlugin(jarFile);
		}
		LOGGER.info("Reloaded plugin: {}", jarFile.getName());
	}

	private void removePlugin(File jarFile) {
		String pluginId = findPluginIdByJarFile(jarFile);
		if (pluginId == null) return;
		unload(pluginId);
		LOGGER.info("Removed plugin: {}", jarFile.getName());
	}

	private String findPluginIdByJarFile(File jarFile) {
		for (Map.Entry<String, Plugin> entry : plugins.entrySet()) {
			Plugin p = entry.getValue();
			String jarName = jarFile.getName();
			if (jarName.contains(p.getId()) || jarName.contains(p.getName())) {
				return entry.getKey();
			}
		}
		return null;
	}

	private File findJarFileByPluginId(String id) {
		for (File file : getJarFiles())
			if (file.getName().contains(id)) return file;
		return null;
	}

	private Set<File> getNewPlugins() {
		Set<File> all = getJarFiles();
		all.removeAll(pluginTimestamps.keySet());
		return all;
	}

	private Set<File> getRemovedPlugins() {
		Set<File> removed = new HashSet<>(pluginTimestamps.keySet());
		removed.removeAll(getJarFiles());
		return removed;
	}

	private Set<File> getModifiedPlugins() {
		Set<File> modified = new HashSet<>();
		for (File file : getJarFiles()) {
			Long stored = pluginTimestamps.get(file);
			if (stored != null && file.lastModified() > stored)
				modified.add(file);
		}
		return modified;
	}

	private Set<File> getJarFiles() {
		File[] files = pluginFolder.listFiles((dir, name) -> name.endsWith(".jar"));
		if (files == null) return Collections.emptySet();
		return new HashSet<>(Arrays.asList(files));
	}

	private void closeQuietly(ClassLoader cl) {
		if (cl instanceof URLClassLoader) {
			try {
				((URLClassLoader) cl).close();
			} catch (IOException ignored) {}
		}
	}
}
