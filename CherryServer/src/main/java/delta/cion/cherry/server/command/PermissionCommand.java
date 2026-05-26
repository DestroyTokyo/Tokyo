package delta.cion.cherry.server.command;

import delta.cion.cherry.api.command.DeltaCommand;
import delta.cion.cherry.api.locales.Localize;
import delta.cion.cherry.api.online.WhiteList;
import delta.cion.cherry.api.permission.PermissionManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PermissionCommand extends DeltaCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReloadCommand.class);

	public PermissionCommand() {
		super(new Command("perm"));
		ArgumentString playerArg = ArgumentType.String("player");
		ArgumentString permArg = ArgumentType.String("permission");

		Command addCommand = new Command("add");
		addCommand.addSyntax(this::addPermission, playerArg);
		getCommand().addSubcommand(addCommand);

		Command removeCommand = new Command("remove");
		removeCommand.addSyntax(this::removePermission, playerArg);
		getCommand().addSubcommand(removeCommand);
	}

	private void addPermission(CommandSender sender, CommandContext context) {
		if (sender instanceof Player player && !PermissionManager.hasPermission(player, "permission.add")) {
			sender.sendMessage(Localize.getTranslate("no-permission", getCommand().getName())); return; }

		String player = context.get("player");
		String permission = context.get("permission");
		PermissionManager.addPermission(player, permission);
	}

	private void removePermission(CommandSender sender, CommandContext context) {
		if (sender instanceof Player player && !PermissionManager.hasPermission(player, "permission.remove")) {
			sender.sendMessage(Localize.getTranslate("no-permission", getCommand().getName())); return; }

		String player = context.get("player");
		String permission = context.get("permission");
		PermissionManager.removePermission(player, permission);
	}
}
