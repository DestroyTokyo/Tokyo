package delta.cion.cherry.server.command;

import delta.cion.cherry.api.Plugin;
import delta.cion.cherry.api.command.DeltaCommand;
import delta.cion.cherry.server.CherryServer;
import delta.cion.cherry.server.plugin.PluginManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReloadCommand extends DeltaCommand<Command> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReloadCommand.class);

	public ReloadCommand() {
		super(new Command("reload"));
		getCommand().addSyntax(this::execute);
	}

	private void execute(CommandSender sender, CommandContext context) {
		sender.sendMessage("Disabling plugins");
		PluginManager.disableAll();
		sender.sendMessage("Enabling plugins");
		PluginManager.enableAll();
		sender.sendMessage("Success plugins");
	}
}
