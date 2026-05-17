package delta.cion.cherry;

import delta.cion.cherry.command.CommandInit;
import delta.cion.cherry.config.property.PropertiesInit;
import delta.cion.cherry.event.events.PlayerJoinEvent;
import delta.cion.cherry.init.ServerBranding;
import delta.cion.cherry.motd.MOTDHandler;
import delta.cion.cherry.plugin.PluginManager;
import delta.cion.cherry.сonsole.ConsoleHandler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.lan.OpenToLAN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CherryServer {

	private static final String DEFAULT_ADDRESS = "0.0.0.0";
	private static final int DEFAULT_PORT = 25565;
	private static final boolean OPEN_TO_LAN = true;

	private static final MinecraftServer SERVER = MinecraftServer.init();
	private static final GlobalEventHandler GLOBAL_EVENT_HANDLER = MinecraftServer.getGlobalEventHandler();

	private final Logger LOGGER = LoggerFactory.getLogger(CherryServer.class);

	private static boolean open_lan = OPEN_TO_LAN;
	private CherryServer() {}

	private void start() {
		initConfigs();

		MOTDHandler.registerVanillaMOTD();
		PlayerJoinEvent.register();

		PluginManager.init();
		setBranding();

		CommandInit commandInit = new CommandInit();
		new ConsoleHandler();
		commandInit.init();

		String address = DEFAULT_ADDRESS;
		int port = DEFAULT_PORT;

		SERVER.start(address, port);
		if (open_lan) OpenToLAN.open();
		LOGGER.info("Server started on {}:{}.", address, port);
		LOGGER.info("Server version: {}", MinecraftServer.VERSION_NAME);
	}

	public static void main(String[] args) {
		new CherryServer().start();
	}

	private void initConfigs() {
		try {
			PropertiesInit.buildConfig();
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
		return open_lan;
	}

	public static void stopServer() {
		PluginManager.disableAll();
		MinecraftServer.stopCleanly();

		System.exit(0);
	}
}
