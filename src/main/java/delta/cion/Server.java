package delta.cion;

import delta.cion.event.EventManager;
import delta.cion.plugin.PluginManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

	private static final MinecraftServer minecraftServer = MinecraftServer.init();
	private static final InstanceManager instanceManager = MinecraftServer.getInstanceManager();

	private static final InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

	public static void main(String[] args) {

		instanceContainer.setGenerator(unit -> {
			Point worldStart = unit.absoluteStart();

			int x = (int) worldStart.x();
			int z = (int) worldStart.z();

			if (x == 0 && z == 0) unit.modifier().setBlock(x, 49, z, Block.BEDROCK);
		});

		EventManager.onEvent(AsyncPlayerConfigurationEvent.class, event -> {
			Player player = event.getPlayer();
			event.setSpawningInstance(instanceContainer);
			player.setRespawnPoint(new Pos(0.5, 50.0, 0.5));
		});

		PluginManager.init();
		setBranding();
		int port = 25565;
		minecraftServer.start("0.0.0.0", port);
		LOGGER.info("Server started on {} port.\nServer version: {}", port, MinecraftServer.VERSION_NAME);
	}

	private static void setBranding() {
		MinecraftServer.setBrandName("Citory's server");
	}
}
