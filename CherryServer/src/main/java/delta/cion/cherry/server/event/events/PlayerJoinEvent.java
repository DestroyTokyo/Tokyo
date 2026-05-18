package delta.cion.cherry.server.event.events;

import delta.cion.cherry.server.event.registration.DeltaEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

// ВНИМАНИЕ НХ!
// ЭТО ЛИШЬ ПРИМЕР КЛАССА (Ну или тестовый класс)
// После проверки системы плагинов я его вырежу
public class PlayerJoinEvent {

	private static final List<String> whitelist = List.of("Citory_");
	private static final ArrayList<Player> online = new ArrayList<>();


	private static final InstanceManager instanceManager = MinecraftServer.getInstanceManager();
	private static final InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerJoinEvent.class);

	private static final ComponentSerializer<Component, TextComponent, String>
		SERIALIZER = PlainTextComponentSerializer.plainText();

	public static void register() {
		instanceContainer.setGenerator(worldGenerator());
		instanceContainer.setChunkSupplier(LightingChunk::new);
		connectPlayer().register();
		exitPlayer().register();
	}

	private static Generator worldGenerator() {
		return unit -> {
			Point worldStart = unit.absoluteStart();

			int x = (int) worldStart.x();
			int z = (int) worldStart.z();

			if (x == 0 && z == 0) unit.modifier().setBlock(x, 49, z, Block.BEDROCK);
		};
	}

	private static DeltaEvent<AsyncPlayerConfigurationEvent> connectPlayer() {
		return new DeltaEvent<>(AsyncPlayerConfigurationEvent.class, event -> {
			Player player = event.getPlayer();
			String name = SERIALIZER.serialize(player.getName());
			if (!whitelist.contains(name)) player.kick("Sorry but you cannot connect to this server.\nMeowMeowMeow");
			event.setSpawningInstance(instanceContainer);
			player.setRespawnPoint(new Pos(0.5, 50.0, 0.5));
			LOGGER.info("Player {} connected", name);
			online.add(player);
		});
	}

	private static DeltaEvent<PlayerDisconnectEvent> exitPlayer() {
		return new DeltaEvent<>(PlayerDisconnectEvent.class, event -> {
			Player player = event.getPlayer();
			String name = SERIALIZER.serialize(player.getName());
			LOGGER.info("Player {} disconnected", name);
			online.remove(player);
		});
	}

	public static ArrayList<Player> getOnline() {
		return online;
	}
}
