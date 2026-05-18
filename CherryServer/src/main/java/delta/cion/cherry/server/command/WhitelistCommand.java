package delta.cion.cherry.server.command;

import delta.cion.cherry.api.command.DeltaCommand;
import delta.cion.cherry.api.online.WhiteList;
import delta.cion.cherry.server.config.property.PropertiesHandler;
import delta.cion.cherry.server.console.LogbackConfig;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.io.*;
import java.util.Properties;
import java.util.UUID;

public class WhitelistCommand extends DeltaCommand<Command> {

	private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistCommand.class);

	private static final File SERVER_PROPERTIES_FILE = new File("server.properties");

	public WhitelistCommand() {
		super(new Command("whitelist"));

		Command addCommand = new Command("add");
		ArgumentString playerArg = ArgumentType.String("player");
		addCommand.addSyntax(this::addPlayer, playerArg);
		getCommand().addSubcommand(addCommand);

		Command removeCommand = new Command("remove");
		removeCommand.addSyntax(this::removePlayer, playerArg);
		getCommand().addSubcommand(removeCommand);

		Command reloadCommand = new Command("reload");
		reloadCommand.addSyntax(this::reload);
		getCommand().addSubcommand(reloadCommand);

		Command enableCommand = new Command("enable");
		enableCommand.addSyntax(this::enableWhitelist);
		getCommand().addSubcommand(enableCommand);

		Command disableCommand = new Command("disable");
		disableCommand.addSyntax(this::disableWhitelist);
		getCommand().addSubcommand(disableCommand);

		Command statusCommand = new Command("status");
		statusCommand.addSyntax(this::whitelistStatus);
		getCommand().addSubcommand(statusCommand);
	}

	private void addPlayer(CommandSender sender, CommandContext context) {
		String player = context.get("player");
		UUID uuid = parseUUID(player);

		if (uuid != null) {
			if (WhiteList.isWhitelisted(uuid)) return;
			WhiteList.addToWhitelist(uuid);
			sender.sendMessage("Player ["+player+"] added to whitelist");
		} else {
			if (WhiteList.isWhitelisted(player)) return;
			WhiteList.addToWhitelist(player);
			sender.sendMessage("Player ["+player+"] added to whitelist");
		}
	}

	private void removePlayer(CommandSender sender, CommandContext context) {
		String player = context.get("player");
		UUID uuid = parseUUID(player);

		if (uuid != null) {
			if (!WhiteList.isWhitelisted(uuid)) return;
			WhiteList.removeFromWhitelist(uuid);
			sender.sendMessage("Player ["+player+"] removed from whitelist");
		} else {
			if (!WhiteList.isWhitelisted(player)) return;
			WhiteList.removeFromWhitelist(player);
			sender.sendMessage("Player ["+player+"] removed from whitelist");
		}
	}

	private void reload(CommandSender sender, CommandContext context) {
		WhiteList.loadWhitelistFromFile();
		WhiteList.comparePlayerData();
		sender.sendMessage("Whitelist reloaded");
	}

	private void enableWhitelist(CommandSender sender, CommandContext context) {
		if (WhiteList.getStatus()) {
			sender.sendMessage("Whitelist already enabled!");
			return; }
		WhiteList.setStatus(true);
		setWhitelistStatus(true);
		sender.sendMessage("Whitelist enabled");
	}

	private void disableWhitelist(CommandSender sender, CommandContext context) {
		if (!WhiteList.getStatus()) {
			sender.sendMessage("Whitelist already disabled!");
			return; }
		WhiteList.setStatus(false);
		setWhitelistStatus(false);
		sender.sendMessage("Whitelist disabled");
	}

	private void whitelistStatus(CommandSender sender, CommandContext context) {
		int whitelistedPlayersCount = WhiteList.getWhitelist().size();
		sender.sendMessage("Whitelist status: ["+WhiteList.getStatus()+"].");
		sender.sendMessage("Whitelisted players count: "+whitelistedPlayersCount+".");
	}

	private UUID parseUUID(String playerName) {
		try {
			return UUID.fromString(playerName);
		} catch (IllegalArgumentException ignored) {}
		return null;
	}

	private void setWhitelistStatus(boolean status) {
		try (InputStream propertiesStream = new FileInputStream(SERVER_PROPERTIES_FILE)) {
			Properties server_properties = new Properties();
			server_properties.load(propertiesStream);

			server_properties.setProperty("enable-whitelist", String.valueOf(status));
			try (OutputStream propertiesOut = new FileOutputStream(SERVER_PROPERTIES_FILE)) {
				server_properties.store(propertiesOut, "server.properties updated");
			}
		} catch (IOException e) {
			LOGGER.error("Cannot save new whitelist status to server.properties", e);
		}
	}
}
