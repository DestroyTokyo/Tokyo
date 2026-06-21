package delta.cion.cherry.api.plugin;

import net.minestom.server.event.GlobalEventHandler;

import java.io.*;

public abstract class Plugin {

	private PluginStatus status = PluginStatus.DISABLED;

	private static GlobalEventHandler GLOBAL_EVENT_HANDLER;

	private final String id;
	private final String name;
	private final String version;

	private final File pluginDir;

	public Plugin(String id, String name, String version) {

		this.id = id;
		this.name = name;
		this.version = version;

		this.pluginDir = new File("plugins", this.id);

	}

	/**
	 * Plugin disabling
	 */
	public void onEnable() {}
	public void sysINIT() {
		this.onEnable();
		this.status = PluginStatus.ENABLED;
	}

	/**
	 * Plugin enabling
	 */
	public void onDisable() {}
	public final void sysDISABLE() {
		this.onDisable();
		this.status = PluginStatus.DISABLED;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getVersion() {
		return this.version;
	}

	public PluginStatus getStatus() {
		return status;
	}

	public File getPluginDirectory() {
		return this.pluginDir;
	}

	public void saveFromResources(String path, String fileName) {
		if (path == null) path = "";

		File parentFile = new File(path);
		if (parentFile.exists() && !parentFile.isDirectory())
			throw new RuntimeException("Cannot write file from plugin assets. Needed save-path is a file. Plugin: "+this.id);

		if (!parentFile.exists()) parentFile.mkdirs();

		try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
			if (inputStream == null) throw new FileNotFoundException("Cannot found "+fileName+" in "+this.id);
			byte[] bytes = inputStream.readAllBytes();
			try (OutputStream outputStream = new FileOutputStream(new File(path, fileName))) {
				outputStream.write(bytes);
			}
		} catch (IOException exception) {
			throw new RuntimeException("Cannot write file from plugin assets. Plugin: "+this.id);
		}
	}

	public void saveFromResources(String fileName) {
		saveFromResources("", fileName);
	}

	public void saveFromResources(File parentDir, String fileName) {
		saveFromResources(parentDir.getPath(), fileName);
	}

	// System methods

	public static void setGlobalEventHandler(GlobalEventHandler handler) {
		if (GLOBAL_EVENT_HANDLER != null) return;
		GLOBAL_EVENT_HANDLER = handler;
	}

	public static GlobalEventHandler getGlobalEventHandler() {
		return GLOBAL_EVENT_HANDLER;
	}

}
