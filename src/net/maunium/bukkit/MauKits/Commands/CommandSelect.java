package net.maunium.bukkit.MauKits.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.maunium.bukkit.MauKits.MauKits;
import net.maunium.bukkit.MauKits.Configuration.Kit;

public class CommandSelect implements CommandExecutor {
	private MauKits plugin;
	
	public CommandSelect(MauKits plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length > 0 && args[0].equalsIgnoreCase("gui")) plugin.getSelectGui().open(p);
			else if (args.length > 0 && label.equalsIgnoreCase("kit") || label.equalsIgnoreCase("maukit")) {
				plugin.assignKitWithOutput(p, args[0], MauKits.KitAssignMode.COMMAND);
			} else {
				StringBuffer sb = new StringBuffer();
				for (Kit k : plugin.getKits().values()) {
					if (k.hasPermission(p)) sb.append(ChatColor.GOLD);
					else sb.append(ChatColor.DARK_GRAY);
					sb.append(k.getName());
					sb.append(ChatColor.GRAY);
					sb.append(", ");
				}
				if (sb.length() > 0) {
					sb.delete(sb.length() - 4, sb.length());
					p.sendMessage(plugin.stag + plugin.translate("kits", sb.toString()));
				} else p.sendMessage(plugin.errtag + plugin.translate("kits.none"));
			}
		} else sender.sendMessage(plugin.errtag + plugin.translate("ingameonly", label));
		return true;
	}
}
