package delta.cion.cherry.test_plugin.world;

import delta.cion.cherry.api.world.WorldRegistration;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.utils.chunk.ChunkSupplier;

public class BaseWorld {

	private final String NAMESPACE;
	private final String WORLD_NAME;
	private final InstanceContainer WORLD_CONTAINER;

	public BaseWorld(String namespace, String name, InstanceContainer worldContainer) {
		this.NAMESPACE = namespace;
		this.WORLD_NAME = name;
		this.WORLD_CONTAINER = worldContainer;
		WorldRegistration.registerWorld(this.NAMESPACE, this.WORLD_NAME, this.WORLD_CONTAINER);
	}

	public String getWorldName() {
		return this.WORLD_NAME;
	}

	public InstanceContainer getWorldContainer() {
		return this.WORLD_CONTAINER;
	}

	public void setWorldGenerator(Generator worldGenerator) {
		this.WORLD_CONTAINER.setGenerator(worldGenerator);
	}

	public void setWorldSupplier(ChunkSupplier worldSupplier) {
		this.WORLD_CONTAINER.setChunkSupplier(worldSupplier);
	}
}
