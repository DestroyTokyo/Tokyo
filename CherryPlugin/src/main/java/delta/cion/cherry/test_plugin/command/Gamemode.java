package delta.cion.cherry.test_plugin.command;

import delta.cion.cherry.api.command.DeltaCommand;
import delta.cion.cherry.api.locales.Localize;
import delta.cion.cherry.api.permission.PermissionManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class Gamemode extends DeltaCommand {

	public Gamemode() {
		super(new Command("gamemode"));

		ArgumentString gamemode = ArgumentType.String("gamemode");
		ArgumentString playerName = ArgumentType.String("player-name");

		gamemode.setSuggestionCallback((sender, context, suggestion) -> {
			for (GameMode gm : GameMode.values())
				suggestion.addEntry(new SuggestionEntry(gm.name().toLowerCase()));
		});

		playerName.setSuggestionCallback((sender, context, suggestion) -> {
			for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers())
				suggestion.addEntry(new SuggestionEntry(player.getUsername()));
		});

		playerName.setDefaultValue(""); // необязательный аргумент
		getCommand().addSyntax(this::setGameMode, gamemode, playerName);
	}

	private void setGameMode(CommandSender sender, CommandContext context) {
		if (isConsole(sender)) return;

		if (!PermissionManager.hasPermission((Player) sender, "gamemode")) {
			sender.sendMessage(Localize.getTranslate("no-permission", getCommand().getName()));
			return;
		}

		String gamemodeStr = context.get("gamemode");
		GameMode gameMode;
		try {
			gameMode = GameMode.valueOf(gamemodeStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			sender.sendMessage("This gamemode doesn't exist.");
			return;
		}

		String targetName = context.get("player-name");
		Player target;

		if (targetName != null && !targetName.isEmpty()) {
			target = MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(targetName);
			if (target == null) {
				sender.sendMessage("Player is offline");
				return;
			}
		} else target = (Player) sender;

		target.setGameMode(gameMode);
		sender.sendMessage("Gamemode updated for " + target.getUsername());
	}

	private static boolean isConsole(CommandSender sender) {
		if (sender instanceof Player) return false;
		sender.sendMessage("Command for players only!");
		return true;
	}
}
