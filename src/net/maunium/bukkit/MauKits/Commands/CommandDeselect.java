package net.maunium.bukkit.MauKits.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.maunium.bukkit.MauKits.MauKits;

public class CommandDeselect implements CommandExecutor {
	private MauKits plugin;
	
	public CommandDeselect(MauKits plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (plugin.unassignKit(p, false)) p.sendMessage(plugin.stag + plugin.translate("unassign"));
			else p.sendMessage(plugin.errtag + plugin.translate("unassign.fail"));
		} else sender.sendMessage(plugin.errtag + plugin.translate("ingameonly", label));
		return true;
	}
}
