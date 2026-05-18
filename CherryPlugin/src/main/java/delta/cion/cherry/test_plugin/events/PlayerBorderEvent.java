package delta.cion.cherry.test_plugin.events;

import delta.cion.cherry.api.registration.DeltaEvent;
import delta.cion.cherry.test_plugin.Main;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerMoveEvent;

public class PlayerBorderEvent {

	private static final int BORDER = 16;
	private static final int BORDER_HEIGHT_MIN = -1;

	public static DeltaEvent<PlayerMoveEvent> playerMoveEvent() {
		return new DeltaEvent<>(PlayerMoveEvent.class, event -> {
			Player player = event.getPlayer();
			int newX = event.getNewPosition().blockX();
			int newY = event.getNewPosition().blockY();
			int newZ = event.getNewPosition().blockZ();

			checkPosition(player, newX, newZ, newY);
		});
	}

	private static void checkPosition(Player player, int x, int z, int y) {
		if (Math.abs(x) > BORDER || Math.abs(z) > BORDER || y < BORDER_HEIGHT_MIN)
			player.teleport(Main.getSpawnPosition());
	}
}
