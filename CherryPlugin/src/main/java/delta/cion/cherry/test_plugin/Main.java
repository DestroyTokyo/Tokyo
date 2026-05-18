package delta.cion.cherry.test_plugin;

import delta.cion.cherry.api.Plugin;
import delta.cion.cherry.api.registration.DeltaEvent;
import delta.cion.cherry.test_plugin.events.PlayerBorderEvent;
import delta.cion.cherry.test_plugin.events.PlayerConnectionEvent;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerMoveEvent;

public class Main extends Plugin {

	private static final Pos SPAWN_POSITION = new Pos(0.5, 50.0, 0.5);

	private static DeltaEvent<PlayerMoveEvent> WORLD_BORDER;

	private static DeltaEvent<AsyncPlayerConfigurationEvent> CONNECTION_EVENT;
	private static DeltaEvent<PlayerDisconnectEvent> DISCONNECT_EVENT;

	public Main() {
		super("Cherry-Test-Plugin");
	}

	@Override
	public void onEnable() {
		WORLD_BORDER = PlayerBorderEvent.playerMoveEvent();
		WORLD_BORDER.register();

		PlayerConnectionEvent.init();
		CONNECTION_EVENT = PlayerConnectionEvent.connectPlayer();
		DISCONNECT_EVENT = PlayerConnectionEvent.exitPlayer();
		CONNECTION_EVENT.register();
		DISCONNECT_EVENT.register();
	}

	@Override
	public void onDisable() {
		WORLD_BORDER.unregister();
		CONNECTION_EVENT.unregister();
		DISCONNECT_EVENT.unregister();
	}

	public static Pos getSpawnPosition() {
		return SPAWN_POSITION;
	}
}
