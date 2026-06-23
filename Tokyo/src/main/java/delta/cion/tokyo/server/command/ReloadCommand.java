package delta.cion.tokyo.server.command;

import delta.cion.tokyo.api.command.DeltaCommand;
import delta.cion.tokyo.api.locales.Localize;
import delta.cion.tokyo.api.permission.PermissionManager;
import delta.cion.tokyo.server.plugin.PluginManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReloadCommand extends DeltaCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReloadCommand.class);

	public ReloadCommand() {
		super(new Command("reload"));
		getCommand().addSyntax(this::execute);
	}

	private void execute(CommandSender sender, CommandContext context) {
		if (sender instanceof Player player && !PermissionManager.hasPermission(player, "server.reload")) {
			sender.sendMessage(Localize.getTranslate("no-permission", getCommand().getName())); return; }
		sender.sendMessage("Disabling plugins");
		PluginManager.disableAll();
		sender.sendMessage("Enabling plugins");
		PluginManager.enableAll();
		sender.sendMessage("Success plugins");
	}
}
