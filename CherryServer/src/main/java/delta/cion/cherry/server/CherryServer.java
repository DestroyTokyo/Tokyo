package delta.cion.cherry.server;

import delta.cion.cherry.api.Plugin;
import delta.cion.cherry.server.command.ReloadCommand;
import delta.cion.cherry.server.command.StopCommand;
import delta.cion.cherry.server.config.property.PropertiesHandler;
import delta.cion.cherry.server.console.ConsoleHandler;
import delta.cion.cherry.server.event.events.PlayerJoinEvent;
import delta.cion.cherry.server.init.ServerBranding;
import delta.cion.cherry.server.motd.MOTDHandler;
import delta.cion.cherry.server.plugin.PluginManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.lan.OpenToLAN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class CherryServer {

	private static final boolean DEFAULT_OPEN_SERVER_TO_LAN = true;
	private static final String DEFAULT_SERVER_ADDRESS = "0.0.0.0";
	private static final int DEFAULT_SERVER_PORT = 25565;

	private static boolean openToLan = DEFAULT_OPEN_SERVER_TO_LAN;
	private static String serverAddress = DEFAULT_SERVER_ADDRESS;
	private static int serverPort = DEFAULT_SERVER_PORT;

	private static final MinecraftServer SERVER = MinecraftServer.init();
	private static final GlobalEventHandler GLOBAL_EVENT_HANDLER = MinecraftServer.getGlobalEventHandler();

	private final Logger LOGGER = LoggerFactory.getLogger(CherryServer.class);

	private CherryServer() {}

	private void start() {
		initConfigs();
		loadConfig();

		Plugin.setGlobalEventHandler(GLOBAL_EVENT_HANDLER);

		MOTDHandler.registerVanillaMOTD();
		PlayerJoinEvent.register();

		PluginManager.init();
		setBranding();

		new StopCommand().register();
		new ReloadCommand().register();
		new ConsoleHandler();

		SERVER.start(serverAddress, serverPort);
		if (openToLan) OpenToLAN.open();
		LOGGER.info("Server started on {}:{}.", serverAddress, serverPort);
		LOGGER.info("Server version: {}", MinecraftServer.VERSION_NAME);
	}

	public static void main(String[] args) {
		new CherryServer().start();
	}

	private void initConfigs() {
		try {
			PropertiesHandler.buildConfig();
		} catch (Exception exception) {
			LOGGER.error(exception.toString());
			LOGGER.error("Cannot init some or all configs. Check your permissions and try to restart this server");
			System.exit(100);
		}
	}

	public static GlobalEventHandler getGlobalEventHandler() {
		return GLOBAL_EVENT_HANDLER;
	}

	private static void setBranding() {
		MinecraftServer.setBrandName(ServerBranding.getBrandName());
	}

	public static boolean getLanStatus() {
		return openToLan;
	}

	public static void stopServer() {
		PluginManager.disableAll();
		MinecraftServer.stopCleanly();

		System.exit(0);
	}

	private static void loadConfig() {
		Properties server_properties = PropertiesHandler.getProperties("server.properties");
		if (server_properties == null) return;
		serverAddress = server_properties.getProperty("server-ip");
		serverPort = Integer.parseInt(server_properties.getProperty("server-port"));
		openToLan = Boolean.getBoolean(server_properties.getProperty("open-lan"));
	}
}
