package delta.cion.tokyo.server.command;

import delta.cion.tokyo.api.command.DeltaCommand;
import delta.cion.tokyo.api.locales.Localize;
import delta.cion.tokyo.api.permission.PermissionManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpCommand  extends DeltaCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReloadCommand.class);

	public OpCommand() {
		super(new Command("op"));
		ArgumentString playerArg = ArgumentType.String("player");
		getCommand().addSyntax(this::execute, playerArg);
	}

	private void execute(CommandSender sender, CommandContext context) {
		if (sender instanceof Player player && !PermissionManager.hasPermission(player, "server.op")) {
			sender.sendMessage(Localize.getTranslate("no-permission", getCommand().getName())); return; }
		String player = context.get("player");
		PermissionManager.addPermission(player, "*");
	}
}
