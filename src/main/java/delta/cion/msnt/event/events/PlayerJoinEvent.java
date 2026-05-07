package delta.cion.msnt.event.events;

import delta.cion.msnt.event.DeltaEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.Generator;

import java.util.List;

public class PlayerJoinEvent {

	private static final List<String> whitelist = List.of("Citory_");


	private static final InstanceManager instanceManager = MinecraftServer.getInstanceManager();
	private static final InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

	private static final ComponentSerializer<Component, TextComponent, String>
		SERIALIZER = PlainTextComponentSerializer.plainText();

	public static void register() {
		instanceContainer.setGenerator(worldGenerator());
		buildEvent().register();
	}

	private static Generator worldGenerator() {
		return unit -> {
			Point worldStart = unit.absoluteStart();

			int x = (int) worldStart.x();
			int z = (int) worldStart.z();

			if (x == 0 && z == 0) unit.modifier().setBlock(x, 49, z, Block.BEDROCK);
		};
	}

	private static DeltaEvent<AsyncPlayerConfigurationEvent> buildEvent() {
		return new DeltaEvent<>(AsyncPlayerConfigurationEvent.class, event -> {
			Player player = event.getPlayer();
			String name = SERIALIZER.serialize(player.getName());
			if (!whitelist.contains(name)) player.kick("Sorry but you cannot connect to this server.\nMeowMeowMeow");
			event.setSpawningInstance(instanceContainer);
			player.setRespawnPoint(new Pos(0.5, 50.0, 0.5));
		});
	}

}
