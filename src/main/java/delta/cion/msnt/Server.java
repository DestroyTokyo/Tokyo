package delta.cion.msnt;

import delta.cion.msnt.config.property.PropertiesInit;
import delta.cion.msnt.event.events.PlayerJoinEvent;
import delta.cion.msnt.motd.MOTDHandler;
import delta.cion.msnt.plugin.PluginManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

	private static final MinecraftServer minecraftServer = MinecraftServer.init();

	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

	private static final GlobalEventHandler GLOBAL_EVENT_HANDLER = MinecraftServer.getGlobalEventHandler();

	private static final String DEFAULT_ADDRESS = "0.0.0.0";
	private static final int DEFAULT_PORT = 25565;

	public static void main(String[] args) {
		initConfigs();
		PlayerJoinEvent.register();

		PluginManager.init();
		setBranding();

		String address = DEFAULT_ADDRESS;
		int port = DEFAULT_PORT;

		minecraftServer.start(address, port);
		LOGGER.info("Server started on {}:{}.", address, port);
		LOGGER.info("Server version: {}", MinecraftServer.VERSION_NAME);
		MOTDHandler.registerVanillaMOTD();
	}

	private static void initConfigs() {
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
		MinecraftServer.setBrandName("Citory's server");
	}
}
