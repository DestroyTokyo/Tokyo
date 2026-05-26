package delta.cion.cherry.test_plugin.command;

import delta.cion.cherry.api.command.DeltaCommand;
import delta.cion.cherry.api.locales.Localize;
import delta.cion.cherry.api.permission.PermissionManager;
import delta.cion.cherry.api.world.WorldRegistration;
import delta.cion.cherry.test_plugin.Main;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentIntRange;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.recipe.display.SlotDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCommand extends DeltaCommand {

	public GetCommand() {
		super(new Command("get"));

		ArgumentString  itemArg = ArgumentType.String("item");

		itemArg.setSuggestionCallback((sender, context, suggestion) -> {
			Material.values().forEach(material ->
				suggestion.addEntry(new SuggestionEntry(material.name().toLowerCase())));
		});

		ArgumentInteger itemCount = ArgumentType.Integer("item_count");
		itemCount.setDefaultValue(1);
		getCommand().addSyntax(this::getItem, itemArg, itemCount);

		Command removeCommand = new Command("remove");
		removeCommand.addSyntax(this::removeItem);
		getCommand().addSubcommand(removeCommand);
	}

	private void getItem(CommandSender sender, CommandContext context) {
		if (sender instanceof Player player && !PermissionManager.hasPermission(player, "get.item")) {
			sender.sendMessage(Localize.getTranslate("no-permission", getCommand().getName())); return; }

		if (isConsole(sender)) return;
		int itemCount = context.get("item_count");
		String itemName = context.get("item");

		Material material = Material.fromKey(itemName);

		if (itemCount == 0) itemCount = 1;
		if (material == null) {
			sender.sendMessage("Item with id ["+itemName+"] not found.");
			sender.sendMessage("Please check item name on Minecraft Wiki or Minestom docs.");
			return;
		}
		if (material.maxStackSize() == 1) itemCount = 1;

		ItemStack itemStack = ItemStack.of(material, itemCount);
		((Player) sender).getInventory().addItemStack(itemStack);
	}

	private void removeItem(CommandSender sender, CommandContext context) {
		if (sender instanceof Player player && !PermissionManager.hasPermission(player, "get.remove")) {
			sender.sendMessage(Localize.getTranslate("no-permission", getCommand().getName())); return; }

		if (isConsole(sender)) return;
		((Player) sender).setItemInHand(PlayerHand.MAIN, ItemStack.AIR);
	}

	private static boolean isConsole(CommandSender sender) {
		if (sender instanceof Player) return false;
		sender.sendMessage("Command for players only!");
		return true;
	}
}
