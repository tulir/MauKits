package net.maunium.bukkit.MauKits.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.maunium.bukkit.MauKits.Kit;
import net.maunium.bukkit.MauKits.MauKits;

public class CommandCreate implements CommandExecutor {
	private MauKits plugin;
	
	public CommandCreate(MauKits plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length > 0) {
				Kit k = new Kit(args[0], p);
				if (plugin.addKit(k)) p.sendMessage(plugin.stag + plugin.translate("add.replaced", k.getName()));
				else p.sendMessage(plugin.stag + plugin.translate("add.created", k.getName()));
				plugin.clearInv(p);
			} else p.sendMessage(plugin.errtag + plugin.translate("usage", label));
		} else sender.sendMessage(plugin.errtag + plugin.translate("ingameonly", label));
		return true;
	}
}
