package delta.cion.cherry.test_plugin.event;

import delta.cion.cherry.api.locales.Localize;
import delta.cion.cherry.api.online.WhiteList;
import delta.cion.cherry.api.event.DeltaEvent;
import delta.cion.cherry.api.world.WorldRegistration;
import delta.cion.cherry.test_plugin.TestPlugin;
import delta.cion.cherry.test_plugin.util.InfoBook;
import delta.cion.cherry.test_plugin.world.BaseWorld;
import delta.cion.cherry.test_plugin.world.WorldGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerConnectionEvent {

	private static BaseWorld baseWorld;

	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerConnectionEvent.class);

	public static void init() {
		InstanceManager instanceManager = MinecraftServer.getInstanceManager();
		baseWorld = new BaseWorld("test", "base_world", instanceManager.createInstanceContainer());
		baseWorld.setWorldGenerator(new WorldGenerator());
		baseWorld.setWorldSupplier(LightingChunk::new);
	}

	public static DeltaEvent<AsyncPlayerConfigurationEvent> connectPlayer() {
		return new DeltaEvent<>(AsyncPlayerConfigurationEvent.class, event -> {
			Player player = event.getPlayer();
			String playerName = player.getUsername();

			if (WhiteList.getStatus() && isWhitelisted(player)) {
				LOGGER.info("Player {} [{}] whitelisted", playerName, player.getUuid());
			} else if (WhiteList.getStatus() && !isWhitelisted(player)) {
				LOGGER.info("Player {} [{}] is not whitelisted", playerName, player.getUuid());
				player.kick(Localize.getTranslate("not-whitelisted", playerName));
				return;
			}

			event.setSpawningInstance(baseWorld.getWorldContainer());
			player.setRespawnPoint(TestPlugin.getSpawnPosition());
			InfoBook.getBook(player);
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

	public static void close() {
		MinecraftServer.getInstanceManager().unregisterInstance(baseWorld.getWorldContainer());
	}
}
