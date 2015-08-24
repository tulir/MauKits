package net.maunium.bukkit.MauKits.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.maunium.bukkit.MauKits.MauKits;
import net.maunium.bukkit.MauKits.Configuration.Kit;
import net.maunium.bukkit.MauKits.Configuration.KitGuiItem;

public class CommandGui implements CommandExecutor {
	private MauKits plugin;
	
	public CommandGui(MauKits plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length > 1) {
				if (args[0].equalsIgnoreCase("icon")) {
					if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)) {
						Kit k = plugin.getKit(args[1]);
						if (k != null) {
							k.setIcon(p.getItemInHand());
							p.sendMessage(plugin.stag + plugin.translate("seticon.success", k.getName()));
							p.setItemInHand(new ItemStack(Material.AIR));
						} else p.sendMessage(plugin.errtag + plugin.translate("kit.notfound", args[0]));
					} else p.sendMessage(plugin.errtag + plugin.translate("seticon.emptyhand"));
				} else if (args[0].equalsIgnoreCase("position")) {
					Kit k = plugin.getKit(args[1]);
					if (k != null) {
						if (args.length > 2 && args[2].equalsIgnoreCase("automatic")) {
							k.removeCustomPosition();
							p.sendMessage(plugin.stag + plugin.translate("setposition.removed", k.getName()));
						} else if (args.length > 3) {
							int guiX = Integer.parseInt(args[2]) - 1;
							int guiY = Integer.parseInt(args[3]) - 1;
							k.setCustomPosition(guiX, guiY);
							p.sendMessage(plugin.stag + plugin.translate("setposition.set", k.getName(), k.getCustomGuiX() + 1, k.getCustomGuiY()) + 1);
						} else help(p, label);
					} else p.sendMessage(plugin.errtag + plugin.translate("kit.notfound", args[0]));
				} else if (args.length > 2 && args[0].equalsIgnoreCase("extra")) {
					if (args[1].equalsIgnoreCase("remove")) {
						int id;
						try {
							id = Integer.parseInt(args[2]);
						} catch (NumberFormatException e) {
							p.sendMessage(plugin.errtag + plugin.translate("extra.args.remove"));
							return true;
						}
						
						if (plugin.removeExtraItem(id)) p.sendMessage(plugin.stag + plugin.translate("extra.removed", id));
						else p.sendMessage(plugin.errtag + plugin.translate("extra.idnotfound"));
					} else {
						int guiX, guiY;
						try {
							guiX = Integer.parseInt(args[1]) - 1;
							guiY = Integer.parseInt(args[2]) - 1;
						} catch (NumberFormatException e) {
							p.sendMessage(plugin.errtag + plugin.translate("extra.args.add"));
							return true;
						}
						
						if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)) {
							KitGuiItem kgi = new KitGuiItem(p.getItemInHand(), guiX, guiY);
							p.sendMessage(plugin.stag + plugin.translate("extra.added", plugin.addExtraItem(kgi), guiX, guiY));
							p.setItemInHand(new ItemStack(Material.AIR));
						} else p.sendMessage(plugin.errtag + plugin.translate("extra.noiteminhand"));
					}
				} else help(p, label);
			} else help(p, label);
		} else sender.sendMessage(plugin.errtag + plugin.translate("ingameonly", label));
		return true;
	}
	
	public void help(Player p, String label) {
	
	}
}
