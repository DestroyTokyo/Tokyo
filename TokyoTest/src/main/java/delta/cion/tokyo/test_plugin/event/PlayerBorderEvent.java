package delta.cion.tokyo.test_plugin.event;

import delta.cion.tokyo.api.event.DeltaEvent;
import delta.cion.tokyo.api.permission.PermissionManager;
import delta.cion.tokyo.test_plugin.TestPlugin;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerMoveEvent;

public class PlayerBorderEvent {

	private static final int BORDER = 16;
	private static final int BORDER_HEIGHT_MIN = -1;

	public static DeltaEvent<PlayerMoveEvent> playerMoveEvent() {
		return new DeltaEvent<>(PlayerMoveEvent.class, event -> {
			Player player = event.getPlayer();
			if (PermissionManager.hasPermission(player, "bypass.border")) return;

			int newX = event.getNewPosition().blockX();
			int newY = event.getNewPosition().blockY();
			int newZ = event.getNewPosition().blockZ();

			checkPosition(player, newX, newZ, newY);
		});
	}

	private static void checkPosition(Player player, int x, int z, int y) {
		if (Math.abs(x) > BORDER || Math.abs(z) > BORDER || y < BORDER_HEIGHT_MIN)
			player.teleport(TestPlugin.getSpawnPosition());
	}

	public static DeltaEvent<PlayerBlockBreakEvent> playerBlockBreakEvent() {
		return new DeltaEvent<>(PlayerBlockBreakEvent.class, event -> {
			event.setCancelled(true);
		});
	}

	public static DeltaEvent<PlayerBlockPlaceEvent> playerBlockPlaceEvent() {
		return new DeltaEvent<>(PlayerBlockPlaceEvent.class, event -> {
			event.setCancelled(true);
		});
	}
}
