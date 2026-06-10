package delta.cion.cherry.api.command;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class DeltaCommand implements AutoCloseable {

	private final Command commandInstance;

	private static final Logger LOGGER = LoggerFactory.getLogger(DeltaCommand.class);

	public DeltaCommand(Class<Command> commandClass, Consumer<Command> configurator) {
		try {
			this.commandInstance = commandClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			LOGGER.error("Cannot create the command {}", commandClass.getName());
			throw new RuntimeException("Cannot create command instance " + commandClass.getName(), e);
		}
		configurator.accept(this.commandInstance);
	}
	public DeltaCommand(Command commandInstance) {
		this.commandInstance = commandInstance;
	}

	public void register() {
		MinecraftServer.getCommandManager().register(commandInstance);
	}

	public void unregister() {
		MinecraftServer.getCommandManager().unregister(commandInstance);
	}

	public Command getCommand() {
		return commandInstance;
	}

	public void registerPermission() {

	}

	public void registerSubPermissions() {

	}

	@Override
	public void close() {
		unregister();
	}
}
