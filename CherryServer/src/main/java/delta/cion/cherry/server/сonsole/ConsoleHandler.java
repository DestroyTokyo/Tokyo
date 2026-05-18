package delta.cion.cherry.server.сonsole;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ConsoleHandler {

	private static LineReader READER;

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleHandler.class);

	private static CommandSender CONSOLE_SENDER;
	private static String LAST_INPUT = "";
	private static boolean CREATED;

	public ConsoleHandler() {
		if (isInit()) return;
		try {
			Terminal TERMINAL = TerminalBuilder.builder().dumb(true).build();
			READER = LineReaderBuilder.builder().terminal(TERMINAL).build();
			CONSOLE_SENDER = new ConsoleSender();
			Thread CONSOLE_THREAD = new Thread(this::run);
			CONSOLE_THREAD.start();
			CREATED = true;
		} catch (IOException exception) {
			LOGGER.error("Cannot start ConsoleHandler.", exception);
		}
	}

	private boolean isInit() {
		if (CREATED) {
			LOGGER.warn("Cannot create second ConsoleHandler instance.");
			return true;
		} else return false;
	}

	private void run() {
		while (true) {
			String line = READER.readLine();
			if (line.isEmpty()) continue;
			LOGGER.info("Console typed: {}", line);
			MinecraftServer.getCommandManager().execute(CONSOLE_SENDER, line.trim());
			saveInput(line);
		}
	}

	private static void saveInput(String string) {
		LAST_INPUT = string;
	}

	private static String getLastInput() {
		return LAST_INPUT;
	}

}
