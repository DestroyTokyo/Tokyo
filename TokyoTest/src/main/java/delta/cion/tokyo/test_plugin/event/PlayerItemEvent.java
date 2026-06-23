package delta.cion.tokyo.test_plugin.event;

import delta.cion.tokyo.api.event.DeltaEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.OpenBookPacket;

public class PlayerItemEvent {

	public static DeltaEvent<PlayerUseItemEvent> playerDamageEvent() {
		return new DeltaEvent<>(PlayerUseItemEvent.class, event -> {
			if (event.getItemStack().material() != Material.WRITTEN_BOOK) return;
			event.getPlayer().sendPacket(new OpenBookPacket(event.getHand()));
		});
	}

	public static DeltaEvent<ItemDropEvent> itemDropEvent() {
		return new DeltaEvent<>(ItemDropEvent.class, event -> {
			event.setCancelled(true);
		});
	}
}
