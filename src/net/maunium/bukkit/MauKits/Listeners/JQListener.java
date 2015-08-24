package net.maunium.bukkit.MauKits.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.maunium.bukkit.MauKits.MauKits;

/**
 * Class to deselect kit on player quit/join.
 * 
 * @author Tulir293
 * @since Pre-1.3
 */
public class JQListener implements Listener {
	private MauKits plugin;
	
	public JQListener(MauKits plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt) {
		evt.getPlayer().removeMetadata(MauKits.META_SELECTED_KIT, plugin);
		plugin.clearInv(evt.getPlayer());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		evt.getPlayer().removeMetadata(MauKits.META_SELECTED_KIT, plugin);
		plugin.clearInv(evt.getPlayer());
	}
}
