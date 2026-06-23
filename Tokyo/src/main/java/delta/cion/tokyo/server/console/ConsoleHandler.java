package delta.cion.tokyo.server.console;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConsoleHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleHandler.class);

	private static CommandSender CONSOLE_SENDER;
	private static String LAST_INPUT = "";
	private static boolean CREATED;

	public ConsoleHandler() {
		if (isInit()) return;
		CONSOLE_SENDER = new ConsoleSender();
		Thread consoleThread = new Thread(this::run);
		consoleThread.start();
		CREATED = true;
	}

	private boolean isInit() {
		if (CREATED) {
			LOGGER.warn("Cannot create second ConsoleHandler instance.");
			return true;
		}
		return false;
	}

	private void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				LOGGER.info("Console typed: {}", line);
				MinecraftServer.getCommandManager().execute(CONSOLE_SENDER, line.trim());
				saveInput(line);
			}
		} catch (IOException e) {
			LOGGER.error("Console read error", e);
		}
	}

	private static void saveInput(String string) {
		LAST_INPUT = string;
	}

	private static String getLastInput() {
		return LAST_INPUT;
	}
}
