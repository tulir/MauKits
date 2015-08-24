package net.maunium.bukkit.MauKits.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.maunium.bukkit.MauKits.MauKits;

public class SignChangeListener implements Listener {
	private MauKits plugin;
	
	public SignChangeListener(MauKits plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent evt) {
		if (evt.getLine(0).equalsIgnoreCase("[MauKit]")) {
			if (plugin.containsKit(evt.getLine(1))) {
				evt.setLine(0, ChatColor.AQUA + "[" + ChatColor.GREEN + "Ⓜ-Kit" + ChatColor.AQUA + "]");
				evt.setLine(1, plugin.getKit(evt.getLine(1)).getName());
				evt.getPlayer().sendMessage(plugin.stag + plugin.translate("sign.success", evt.getLine(1)));
			} else {
				evt.setLine(0, ChatColor.DARK_RED + "[" + ChatColor.RED + "Ⓜ-Kit" + ChatColor.DARK_RED + "]");
				evt.getPlayer().sendMessage(plugin.errtag + plugin.translate("sign.failed", evt.getLine(1)));
			}
		} else if (evt.getLine(1).equalsIgnoreCase("[Deselect]")) evt.setLine(1, ChatColor.DARK_AQUA + "[" + ChatColor.DARK_GREEN + "Deselect" + ChatColor.DARK_AQUA + "]");
		else if (evt.getLine(0).equalsIgnoreCase("[Deselect]")) evt.setLine(0, ChatColor.DARK_AQUA + "[" + ChatColor.DARK_GREEN + "Deselect" + ChatColor.DARK_AQUA + "]");
	}
}
