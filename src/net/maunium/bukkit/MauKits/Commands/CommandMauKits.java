package net.maunium.bukkit.MauKits.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.maunium.bukkit.MauKits.MauKits;

public class CommandMauKits implements CommandExecutor {
	private MauKits plugin;
	
	public CommandMauKits(MauKits plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("save")) {
				plugin.saveConfig();
				sender.sendMessage(plugin.stag + plugin.translate("conf.save"));
			} else if (args[0].equalsIgnoreCase("load") || args[0].equalsIgnoreCase("reload")) {
				plugin.loadConfig();
				sender.sendMessage(plugin.stag + plugin.translate("conf.load"));
			} else if (args[0].equalsIgnoreCase("psr")) {
				if (plugin.safeReloadPrepared) {
					plugin.safeReloadPrepared = false;
					plugin.saveConfig();
					sender.sendMessage(plugin.stag + plugin.translate("conf.psr.off"));
				} else {
					plugin.safeReloadPrepared = true;
					sender.sendMessage(plugin.stag + plugin.translate("conf.psr.on"));
				}
			} else sender.sendMessage(plugin.stag + plugin.translate("info", plugin.version));
		} else sender.sendMessage(plugin.stag + plugin.translate("info", plugin.version));
		return true;
	}
}
