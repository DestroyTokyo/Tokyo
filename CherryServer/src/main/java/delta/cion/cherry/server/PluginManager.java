package delta.cion.cherry.server.plugin;

import java.util.UUID;

public final class PluginManager {

	private static final PluginLoader pluginLoader = new PluginLoader("plugins");

	private PluginManager() {}

	public static void init() {
		pluginLoader.loadExtensions();
	}

	public static void updateExtensions() {
		pluginLoader.updateExtensions();
	}

	public static void updateExtensionById(UUID id) {
		pluginLoader.updateExtensionById(id);
	}

	public static void enableById(UUID id) {
		pluginLoader.enableById(id);
	}

	public static void disableById(UUID id) {
		pluginLoader.disableById(id);
	}

	public static void enableAll() {
		pluginLoader.enableAll();
	}

	public static void disableAll() {
		pluginLoader.disableAll();
	}

	public static void unload(UUID id) {
		pluginLoader.unload(id);
	}

	public static int size() {
		return pluginLoader.size();
	}
}
