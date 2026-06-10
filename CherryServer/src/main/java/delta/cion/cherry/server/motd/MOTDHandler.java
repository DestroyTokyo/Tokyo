package delta.cion.cherry.server.motd;

import delta.cion.cherry.api.ServerBranding;
import delta.cion.cherry.server.CherryServer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.ping.ResponseData;
import delta.cion.cherry.api.event.DeltaEvent;
import net.minestom.server.utils.identity.NamedAndIdentified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collection;
import java.util.function.Consumer;

public class MOTDHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(MOTDHandler.class);
	private static DeltaEvent<ServerListPingEvent> serverListPingEvent;

	private static final Path SERVER_ICON = Path.of("server-icon.png");

	private static final Consumer<ServerListPingEvent> DEFAULT_HANDLER = event -> {
		PlayerConnection connection = event.getConnection();
		if (connection != null) LOGGER.debug("Server pinged by {}", connection.getRemoteAddress());
		if (CherryServer.getLanStatus() && connection == null) LOGGER.debug("Server pinged by LAN");

		Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();

		ResponseData response = new ServerMOTD()
			.setMOTDVersion(ServerBranding.getBrandName() + " 1.21.4")
			.setMOTDDescription(ServerBranding.getBrandName() + " Server")
			.setMOTDMaxPlayer(players.size())
			.setMOTDOnline(-1)
			.get();


		for (Player player : players)
			response.addEntry(NamedAndIdentified.of(player.getName(), player.getUuid()));
		if (buildServerIcon() != null) response.setFavicon(buildServerIcon());
		event.setResponseData(response);
	};

	private static String buildServerIcon() {
		if (!SERVER_ICON.toFile().exists()) return null;
		try {
			byte[] data = Files.readAllBytes(SERVER_ICON);
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));

			if (image == null) return null;
			if (image.getWidth() != image.getHeight()
				|| image.getHeight() > 64) return null;

			String base64 = Base64.getEncoder().encodeToString(data);
			return "data:image/png;base64," + base64;
		} catch (IOException e) {
			LOGGER.warn("Incorrect image {}", SERVER_ICON);
			return null;
		}
	}

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
