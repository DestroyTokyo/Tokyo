package delta.cion.cherry.server.command;

import delta.cion.cherry.server.CherryServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public class StopCommand extends Command {

	public StopCommand() {
		super("stop");
		addSyntax(this::execute);
	}

	private void execute(CommandSender sender, CommandContext context) {
		CherryServer.stopServer();
	}
}
