package net.maunium.bukkit.MauKits.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.maunium.bukkit.MauKits.Kit;
import net.maunium.bukkit.MauKits.MauKits;

public class CommandKitIcon implements CommandExecutor {
	private MauKits plugin;
	
	public CommandKitIcon(MauKits plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length > 0) {
				if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)) {
					Kit k = plugin.getKit(args[0]);
					if (k != null) {
						k.setIcon(p.getItemInHand());
						p.sendMessage(plugin.stag + plugin.translate("gui.edit.seticon.success", k.getName()));
						p.setItemInHand(new ItemStack(Material.AIR));
					} else p.sendMessage(plugin.errtag + plugin.translate("kit.notfound", args[0]));
				} else p.sendMessage(plugin.errtag + plugin.translate("gui.edit.seticon.emptyhand"));
			} else sender.sendMessage(plugin.errtag + plugin.translate("gui.edit.seticon.help", label));
		} else sender.sendMessage(plugin.errtag + plugin.translate("ingameonly", label));
		return true;
	}
}
