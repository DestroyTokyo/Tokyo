package delta.cion.cherry.test_plugin.events;

import delta.cion.cherry.api.online.WhiteList;
import delta.cion.cherry.api.registration.DeltaEvent;
import delta.cion.cherry.test_plugin.Main;
import delta.cion.cherry.test_plugin.world.WorldGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerConnectionEvent {

	private static final InstanceManager instanceManager = MinecraftServer.getInstanceManager();
	private static final InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerConnectionEvent.class);

	public static void init() {
		instanceContainer.setGenerator(new WorldGenerator());
		instanceContainer.setChunkSupplier(LightingChunk::new);
	}

	public static DeltaEvent<AsyncPlayerConfigurationEvent> connectPlayer() {
		return new DeltaEvent<>(AsyncPlayerConfigurationEvent.class, event -> {
			Player player = event.getPlayer();
			String playerName = player.getUsername();

			if (WhiteList.getStatus() && isWhitelisted(player)) {
				LOGGER.info("Player {} [{}] whitelisted", playerName, player.getUuid());
			} else if (WhiteList.getStatus() && !isWhitelisted(player)) {
				LOGGER.info("Player {} [{}] is not whitelisted", playerName, player.getUuid());
				player.kick("Sorry, "+playerName+", but you cannot connect to this server.");
			}

			event.setSpawningInstance(instanceContainer);
			player.setRespawnPoint(Main.getSpawnPosition());
			LOGGER.info("Player {} [{}] connected", playerName, player.getUuid());
		});
	}

	public static DeltaEvent<PlayerDisconnectEvent> exitPlayer() {
		return new DeltaEvent<>(PlayerDisconnectEvent.class, event -> {
			Player player = event.getPlayer();
			LOGGER.info("Player {} [{}] disconnected", player.getUsername(), player.getUuid());});
	}

	private static boolean isWhitelisted(Player player) {
		if (WhiteList.isWhitelisted(player.getUsername())) return true;
		return WhiteList.isWhitelisted(player.getUuid());
	}
}
