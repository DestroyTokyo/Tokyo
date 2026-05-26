package delta.cion.cherry.server.command;

import delta.cion.cherry.api.command.DeltaCommand;
import delta.cion.cherry.api.locales.Localize;
import delta.cion.cherry.api.permission.PermissionManager;
import delta.cion.cherry.server.CherryServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;

public class StopCommand extends DeltaCommand {

	public StopCommand() {
		super(new Command("stop"));
		getCommand().addSyntax(this::execute);
	}

	private void execute(CommandSender sender, CommandContext context) {
		if (sender instanceof Player player && !PermissionManager.hasPermission(player, "server.stop")) {
			sender.sendMessage(Localize.getTranslate("no-permission", getCommand().getName())); return; }
		else CherryServer.stopServer();
	}
}
