package delta.cion.cherry.server.command;

import delta.cion.cherry.api.command.DeltaCommand;
import delta.cion.cherry.api.online.WhiteList;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;

import java.util.UUID;

public class WhitelistCommand extends DeltaCommand<Command> {

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
		sender.sendMessage("Whitelist reloaded");
	}

	private void enableWhitelist(CommandSender sender, CommandContext context) {
		WhiteList.setStatus(true);
		sender.sendMessage("Whitelist enabled");
	}

	private void disableWhitelist(CommandSender sender, CommandContext context) {
		WhiteList.setStatus(false);
		sender.sendMessage("Whitelist disabled");
	}

	private UUID parseUUID(String playerName) {
		try {
			return UUID.fromString(playerName);
		} catch (IllegalArgumentException ignored) {}
		return null;
	}
}
