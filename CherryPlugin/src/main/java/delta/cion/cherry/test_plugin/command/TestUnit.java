package delta.cion.cherry.test_plugin.command;

import delta.cion.cherry.api.command.DeltaCommand;
import delta.cion.cherry.api.world.WorldRegistration;
import delta.cion.cherry.test_plugin.Main;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUnit extends DeltaCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestUnit.class);

	private static LivingEntity TEST_UNIT;

	private static boolean SPAWNED;

	public TestUnit() {
		super(new Command("spawn"));

		ArgumentString mobArg = ArgumentType.String("unit");
		getCommand().addSyntax(this::spawnMob, mobArg);

		Command removeCommand = new Command("remove");
		removeCommand.addSyntax(this::killMob);
		getCommand().addSubcommand(removeCommand);
	}

	private void spawnMob(CommandSender sender, CommandContext context) {
		InstanceContainer world = getWorld();
		assert world != null;

		LOGGER.debug("Block spawned on {}", Main.getMobPosition());
		world.setBlock(Main.getMobPosition().withY(49), Block.BEDROCK);

		String mobName = context.get("unit");
		EntityType unitType = EntityType.fromKey(mobName);
		if (unitType == null) {
			sender.sendMessage("Cannot found unit with id ["+mobName+"].");
			sender.sendMessage("Try to check unit id in minecraft wiki or Minestom docs.");
			return;
		}

		TEST_UNIT = new LivingEntity(unitType);

		TEST_UNIT.setInstance(world, Main.getMobPosition());
		LOGGER.debug("Unit instance is {}, with Pos on {}", world, Main.getMobPosition());
		TEST_UNIT.lookAt(Main.getSpawnPosition().withY(51));
		LOGGER.debug("Unit look at {}", Main.getSpawnPosition().withY(51));
		sender.sendMessage("Test unit "+mobName+" spawned.");
	}

	private void killMob(CommandSender sender, CommandContext context) {
		InstanceContainer world = getWorld();
		assert world != null;
		if (TEST_UNIT == null) return;

		TEST_UNIT.remove();
		sender.sendMessage("Test unit "+TEST_UNIT+" removed.");
		TEST_UNIT = null;
		Pos mobPosition = Main.getMobPosition().withY(49);
		world.setBlock(mobPosition, Block.AIR);
	}

	private InstanceContainer getWorld() {
		InstanceContainer world = WorldRegistration.getWorld("base_world");
		if (world != null) return world;
		LOGGER.warn("Cannot spawn test unit. base_world not found.");
		return null;
	}
}
