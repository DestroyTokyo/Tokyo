package delta.cion.msnt;

import delta.cion.msnt.config.property.ConfigBuilder;
import delta.cion.msnt.event.EventManager;
import delta.cion.msnt.plugin.PluginManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.ping.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Server {

	private static final MinecraftServer minecraftServer = MinecraftServer.init();
	private static final InstanceManager instanceManager = MinecraftServer.getInstanceManager();

	private static final InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

	private static final ComponentSerializer<Component, TextComponent, String> SERIALIZER = PlainTextComponentSerializer.plainText();

	private static final String DEFAULT_ADDRESS = "0.0.0.0";
	private static final int DEFAULT_PORT = 25565;

	public static void main(String[] args) {
		try {
			ConfigBuilder.buildConfig();
		} catch (Exception ignored) {}
		instanceContainer.setGenerator(unit -> {
			Point worldStart = unit.absoluteStart();

			int x = (int) worldStart.x();
			int z = (int) worldStart.z();

			if (x == 0 && z == 0) unit.modifier().setBlock(x, 49, z, Block.BEDROCK);
		});

		List<String> whitelist = List.of("Citory_");

		EventManager.onEvent(AsyncPlayerConfigurationEvent.class, event -> {
			Player player = event.getPlayer();
			String name = SERIALIZER.serialize(player.getName());
			if (!whitelist.contains(name)) player.kick("Sorry but you cannot connect to this server.\nMeowMeowMeow");
			event.setSpawningInstance(instanceContainer);
			player.setRespawnPoint(new Pos(0.5, 50.0, 0.5));
		});

		EventManager.onEvent(ServerListPingEvent.class, event -> {
			ResponseData responseData = new ResponseData();
			responseData.setMaxPlayer(1663);
			responseData.setVersion("MSNT server 1.21.4");
			responseData.setOnline(-1);
			responseData.setDescription("MSNT Server");
			event.setResponseData(responseData);
		});

		PluginManager.init();
		setBranding();
		String address = DEFAULT_ADDRESS;
		int port = DEFAULT_PORT;
		minecraftServer.start(address, port);
		LOGGER.info("Server started on {}:{}.", address, port);
		LOGGER.info("Server version: {}", MinecraftServer.VERSION_NAME);
	}

	private static void setBranding() {
		MinecraftServer.setBrandName("Citory's server");
	}
}
