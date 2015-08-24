package net.maunium.bukkit.MauKits.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.maunium.bukkit.MauKits.MauKits;
import net.maunium.bukkit.MauKits.Configuration.Kit;

public class PreCommandListener implements Listener {
	private MauKits plugin;
	
	public PreCommandListener(MauKits plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPreCommand(PlayerCommandPreprocessEvent evt) {
		String kit = evt.getMessage().substring(1);
		if (plugin.containsKit(kit)) {
			Kit k = plugin.getKit(kit);
			plugin.assignKitWithOutput(evt.getPlayer(), k, MauKits.KitAssignMode.COMMAND);
			evt.setCancelled(true);
		}
	}
}
