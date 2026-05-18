package delta.cion.cherry.server.command;


import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class CommandInit {

	private static final ArrayList<Command> COMMANDS = new ArrayList<>();
	private static boolean LOADED;

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandInit.class);

	public CommandInit() {
		if (isLOADED()) return;
		COMMANDS.add(new StopCommand());
	}

	private static boolean isLOADED() {
		if (LOADED) {
			LOGGER.warn("Unable to load commands twice");
			return true;
		}
		else return false;
	}

	public void init() {
		for (Command command : COMMANDS)
			MinecraftServer.getCommandManager().register(command);
		LOADED = true;
	}

}
