package delta.cion.tokyo.test_plugin.world;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;

public class WorldGenerator implements Generator {

	private static final int CORRIDOR_HALF_WIDTH = 2;
	private static final int INTERIOR_HALF_WIDTH = 1;

	private static final int WORLD_MIN_Y = 48;
	private static final int WORLD_MAX_Y = 53;

	private static final int FLOOR_Y = 49;
	private static final int CEIL_Y = 53;

	private static final int LANTERN_Y = 51;
	private static final int LANTERN_STEP = 2;

	@Override
	public void generate(@NotNull GenerationUnit unit) {
		Point worldStart = unit.absoluteStart();
		Point worldEnd = unit.absoluteEnd();

		int startX = worldStart.blockX();
		int startY = worldStart.blockY();
		int startZ = worldStart.blockZ();

		int endX = worldEnd.blockX();
		int endY = worldEnd.blockY();
		int endZ = worldEnd.blockZ();

		for (int x = startX; x < endX; x++) {
			for (int z = startZ; z < endZ; z++) {
				final boolean inCorridor = isCorridorShape(x, z);

				for (int y = startY; y < endY; y++) {
					if (!inCorridor) unit.modifier().setBlock(x, y, z, Block.BEDROCK);
					else {
						if (isVoidLayer(y))
							unit.modifier().setBlock(x, y, z, Block.BEDROCK);
						else if (isFloorOrCeiling(y))
							unit.modifier().setBlock(x, y, z, Block.STONE_BRICKS);
						else if (isWallLayer(y)) {
							final boolean interior = isInteriorSpace(x, z);
							if (interior) unit.modifier().setBlock(x, y, z, Block.AIR);
							else {
								if (isLanternPosition(x, y, z))
									unit.modifier().setBlock(x, y, z, Block.SEA_LANTERN);
								else unit.modifier().setBlock(x, y, z, Block.STONE_BRICKS);
							}
						} else unit.modifier().setBlock(x, y, z, Block.BEDROCK);
					}
					if (Math.abs(x) == 2 && Math.abs(z) == 2)
						unit.modifier().setBlock(x, y, z, Block.DARK_OAK_LOG);
				}
			}
		}
	}

	private static boolean isCorridorShape(int x, int z) {
		return Math.abs(x) <= CORRIDOR_HALF_WIDTH || Math.abs(z) <= CORRIDOR_HALF_WIDTH;
	}

	private static boolean isInteriorSpace(int x, int z) {
		return Math.abs(x) <= INTERIOR_HALF_WIDTH || Math.abs(z) <= INTERIOR_HALF_WIDTH;
	}

	private static boolean isVoidLayer(int y) {
		return y < WORLD_MIN_Y || y > WORLD_MAX_Y;
	}

	private static boolean isFloorOrCeiling(int y) {
		return y == FLOOR_Y || y == CEIL_Y;
	}

	private static boolean isWallLayer(int y) {
		return y > FLOOR_Y && y < CEIL_Y; // y = 50, 51, 52
	}

	private static boolean isLanternPosition(int x, int y, int z) {
		return y == LANTERN_Y && (x % LANTERN_STEP == 0 && z % LANTERN_STEP == 0);
	}
}
