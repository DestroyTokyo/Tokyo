package delta.cion.cherry.server.motd;

import delta.cion.cherry.server.CherryServer;
import delta.cion.cherry.server.event.events.PlayerJoinEvent;
import delta.cion.cherry.server.init.ServerBranding;
import net.minestom.server.entity.Player;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.ping.ResponseData;
import delta.cion.cherry.server.event.registration.DeltaEvent;
import net.minestom.server.utils.identity.NamedAndIdentified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class MOTDHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MOTDHandler.class);
	private static DeltaEvent<ServerListPingEvent> serverListPingEvent;

	private static final Consumer<ServerListPingEvent> DEFAULT_HANDLER = event -> {
		PlayerConnection connection = event.getConnection();
		if (connection != null) LOGGER.debug("Server pinged by {}", connection.getRemoteAddress());
		if (CherryServer.getLanStatus() && connection == null) LOGGER.debug("Server pinged by LAN");

		ResponseData response = new ServerMOTD()
			.setMOTDVersion(ServerBranding.getBrandName() + " 1.21.4")
			.setMOTDDescription(ServerBranding.getBrandName() + " Server")
			.setMOTDMaxPlayer(PlayerJoinEvent.getOnline().size())
			.setMOTDOnline(-1)
			.get();

		for (Player player : PlayerJoinEvent.getOnline())
			response.addEntry(NamedAndIdentified.of(player.getName(), player.getUuid()));
		event.setResponseData(response);
	};

	public static void registerCustomMOTD(Consumer<ServerListPingEvent> handler) {
		if (serverListPingEvent != null) serverListPingEvent.unregister();
		if (handler == null) {
			registerVanillaMOTD();
			return;
		}

		serverListPingEvent = new DeltaEvent<>(ServerListPingEvent.class, handler);
		serverListPingEvent.register();
	}

	public static void registerVanillaMOTD() {
		registerCustomMOTD(DEFAULT_HANDLER);
	}
}
