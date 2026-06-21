package delta.cion.cherry.api.world;

import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * По сути можно обойтись и без этого, но тогда мир будет доступен только в рамках одного плагина.
 * Получить его извне будет невозможно.
 */
public class WorldRegistration {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorldRegistration.class);

	private static final Map<String, InstanceContainer> DELTA_WORLDS = new HashMap<>();

	/**
	 * Result - namespace:cool_world_name
	 * @param namespace namespace
	 * @param worldName world name
	 * @param world world
	 */
	public static void registerWorld(String namespace, String worldName, InstanceContainer world) {
		worldName = namespace+":"+worldName;
		if (!DELTA_WORLDS.containsKey(worldName)) DELTA_WORLDS.put(worldName, world);
	}

	/**
	 * @param worldName World name with namespace like namespace:world_name
	 */
	public static void unregisterWorld(String worldName) {
		if (!DELTA_WORLDS.containsKey(worldName)) return;

		InstanceContainer world = getWorld(worldName);
		assert world != null;

		boolean registered = MinecraftServer.getInstanceManager().getInstances().contains(world);
		if (registered) MinecraftServer.getInstanceManager().unregisterInstance(world);

		DELTA_WORLDS.remove(worldName);
	}

	/**
	 * @param worldName World name with namespace like namespace:world_name
	 */
	public static InstanceContainer getWorld(String worldName) {
		if (DELTA_WORLDS.containsKey(worldName)) return DELTA_WORLDS.get(worldName);
		LOGGER.warn("Trying to get unregistered world with name: [{}]", worldName);
		return null;
	}

}
