package delta.cion.tokyo.server.plugin;

public final class PluginManager {

	private static final PluginLoader pluginLoader = new PluginLoader();

	private PluginManager() {}

	public static void init() {
		pluginLoader.loadPlugins();
	}

	public static void updatePlugins() {
		pluginLoader.updatePlugins();
	}

	public static void updatePlugin(String id) {
		pluginLoader.updatePlugin(id);
	}

	public static void enableAll() {
		pluginLoader.enableAll();
	}

	public static void enablePlugin(String id) {
		pluginLoader.enablePlugin(id);
	}

	public static void disableAll() {
		pluginLoader.disableAll();
	}

	public static void unload(String id) {
		pluginLoader.unload(id);
	}

	public static int size() {
		return pluginLoader.size();
	}

}
