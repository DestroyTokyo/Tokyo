package delta.cion.cherry.test_plugin.world;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;

public class WorldGenerator implements Generator {

	@Override
	public void generate(@NotNull GenerationUnit unit) {
		Point worldStart = unit.absoluteStart();

		int x = (int) worldStart.x();
		int z = (int) worldStart.z();

		if (x == 0 && z == 0) unit.modifier().setBlock(x, 49, z, Block.BEDROCK);
	}

}
