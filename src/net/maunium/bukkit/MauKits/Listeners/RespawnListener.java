package net.maunium.bukkit.MauKits.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.maunium.bukkit.MauKits.MauKits;

public class RespawnListener implements Listener {
	private MauKits plugin;
	
	public RespawnListener(MauKits plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent evt) {
		plugin.unassignKit(evt.getPlayer(), true);
	}
}
