package delta.cion.cherry.server.command.registration;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class DeltaCommand<T extends Command> implements AutoCloseable {

	private final T commandInstance;

	private static final Logger LOGGER = LoggerFactory.getLogger(DeltaCommand.class);

	public DeltaCommand(Class<T> commandClass, Consumer<T> configurator) {
		try {
			this.commandInstance = commandClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			LOGGER.error("Cannot create the command {}", commandClass.getName());
			throw new RuntimeException("Не удалось создать экземпляр команды " + commandClass.getName(), e);
		}
		configurator.accept(this.commandInstance);
	}
	public DeltaCommand(T commandInstance) {
		this.commandInstance = commandInstance;
	}

	public void register() {
		MinecraftServer.getCommandManager().register(commandInstance);
	}

	public void unregister() {
		MinecraftServer.getCommandManager().unregister(commandInstance);
	}

	public T getCommand() {
		return commandInstance;
	}

	@Override
	public void close() {
		unregister();
	}
}
