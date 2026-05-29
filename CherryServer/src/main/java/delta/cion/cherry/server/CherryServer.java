package delta.cion.cherry.server;

import delta.cion.cherry.api.Plugin;
import delta.cion.cherry.api.ServerBranding;
import delta.cion.cherry.api.locales.Localize;
import delta.cion.cherry.api.online.WhiteList;
import delta.cion.cherry.api.permission.PermissionHandler;
import delta.cion.cherry.server.command.*;
import delta.cion.cherry.server.config.property.PropertiesHandler;
import delta.cion.cherry.server.console.ConsoleHandler;
import delta.cion.cherry.server.console.LogbackConfig;
import delta.cion.cherry.server.motd.MOTDHandler;
import delta.cion.cherry.server.plugin.PluginManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.lan.OpenToLAN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class CherryServer {

	private static final boolean DEFAULT_OPEN_SERVER_TO_LAN = true;
	private static final boolean DEFAULT_DEBUG_STATUS = false;
	private static final boolean DEFAULT_WHITELIST_STATUS = false;
	private static final String DEFAULT_SERVER_ADDRESS = "0.0.0.0";
	private static final int DEFAULT_SERVER_PORT = 25565;

	private static final String DEFAULT_SERVER_LOCALE = "default";

	private static boolean openToLan = DEFAULT_OPEN_SERVER_TO_LAN;
	private static boolean debugStatus = DEFAULT_DEBUG_STATUS;
	private static String serverAddress = DEFAULT_SERVER_ADDRESS;
	private static int serverPort = DEFAULT_SERVER_PORT;

	private static String serverLocale = DEFAULT_SERVER_LOCALE;

	private static boolean whitelistStatus = DEFAULT_WHITELIST_STATUS;

	private static final MinecraftServer SERVER = MinecraftServer.init();
	private static final GlobalEventHandler GLOBAL_EVENT_HANDLER = MinecraftServer.getGlobalEventHandler();

	private final Logger LOGGER = LoggerFactory.getLogger(CherryServer.class);

	private CherryServer() {
	}

	private void start() {
		initConfigs();
		loadConfig();

		Localize.init(serverLocale);
		PermissionHandler.loadPermissions();

		Plugin.setGlobalEventHandler(GLOBAL_EVENT_HANDLER);
		WhiteList.setStatus(whitelistStatus);
		WhiteList.loadWhitelistFromFile();

		MOTDHandler.registerVanillaMOTD();

		PluginManager.init();
		setBranding();

		// Permissions
		new OpCommand().register();
		new DeopCommand().register();
		new PermissionCommand().register();

		// Server stop | plugins reload
		new StopCommand().register();
		new ReloadCommand().register();

		// Util
		new WhitelistCommand().register();

		// Not command bruh
		new ConsoleHandler();

		LogbackConfig.enableDebugLogs(debugStatus);
		SERVER.start(serverAddress, serverPort);
		if (openToLan) OpenToLAN.open();
		LOGGER.info("Server started on {}:{}.", serverAddress, serverPort);
		LOGGER.info("Minecraft version: {}", MinecraftServer.VERSION_NAME);
		LOGGER.info("Minecraft version: {}", ServerBranding.getServerVersion());
	}

	public static void main(String[] args) {
		new CherryServer().start();
	}

	private void initConfigs() {
		try {
			LOGGER.info("Load configs...");
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
		if (kickAll()) {
			PluginManager.disableAll();
			MinecraftServer.stopCleanly();
			System.exit(0);
		}
	}

	private static boolean kickAll() {
		MinecraftServer.getConnectionManager().getOnlinePlayers()
			.forEach(player -> player.kick(Localize.getTranslate("server-closed", printDate())));

		while(true) {
			if (MinecraftServer.getConnectionManager().getOnlinePlayers().isEmpty()) return true;
			else try { Thread.sleep(1000);} catch (InterruptedException ignored) {};
		}
	}

	private static String printDate() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[MM.dd.yyyy ~ HH:mm:ss]");
		return now.format(formatter);
	}

	private static void loadConfig() {
		Properties server_properties = PropertiesHandler.getProperties("server.properties");
		if (server_properties == null) return;
		serverPort = Integer.parseInt(server_properties.getProperty("server-port"));
		serverAddress = server_properties.getProperty("server-ip");

		debugStatus = Boolean.parseBoolean(server_properties.getProperty("debug-mode"));
		openToLan = Boolean.parseBoolean(server_properties.getProperty("open-lan"));

		serverLocale = String.valueOf(server_properties.getProperty("server-locale"));

		whitelistStatus = Boolean.parseBoolean(server_properties.getProperty("enable-whitelist"));
	}

	public static void setServerBranding() {
		MinecraftServer.setBrandName(ServerBranding.getBrandName());
		MOTDHandler.registerVanillaMOTD();
	}

}
