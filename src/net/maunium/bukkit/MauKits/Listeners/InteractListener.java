package net.maunium.bukkit.MauKits.Listeners;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import net.maunium.bukkit.MauKits.Kit;
import net.maunium.bukkit.MauKits.MauKits;
import net.maunium.bukkit.Maussentials.Utils.MetadataUtils;

public class InteractListener implements Listener {
	private MauKits plugin;
	private boolean chests;
	
	public InteractListener(MauKits plugin) {
		this.plugin = plugin;
		chests = plugin.getConfig().getBoolean("refill-chests.enabled");
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent evt) {
		if (evt.getItem() != null && evt.getItem().getType().equals(Material.FEATHER)
				&& (evt.getAction().equals(Action.RIGHT_CLICK_AIR) || evt.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			if (evt.getItem().hasItemMeta() && evt.getItem().getItemMeta().hasDisplayName()
					&& evt.getItem().getItemMeta().getDisplayName().equals(plugin.translate("gui.enter.name")))
				plugin.getSelectGui().openSelector(evt.getPlayer());
		} else if (evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (chests && evt.getClickedBlock().getType().equals(Material.TRAPPED_CHEST)) {
				evt.setCancelled(true);
				ItemStack refill = new ItemStack(Material.MUSHROOM_SOUP, 1);
				MetadataValue mv = MetadataUtils.getMetadata(evt.getPlayer(), MauKits.META_SELECTED_KIT, plugin);
				if (mv != null && mv.value() != null && mv.value() instanceof Kit) refill = ((Kit) mv.value()).getRefillItem();
				
				evt.getPlayer().playSound(evt.getClickedBlock().getLocation(), Sound.CHEST_OPEN, 1.0F, 0.7F);
				Inventory i = plugin.getServer().createInventory(null, 4 * 9, plugin.translate("refillchest"));
				ItemStack[] isa = i.getContents();
				Arrays.fill(isa, refill);
				i.setContents(isa);
				evt.getPlayer().openInventory(i);
			} else if (evt.getClickedBlock().getType().equals(Material.SIGN_POST) || evt.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
				Sign s = (Sign) evt.getClickedBlock().getState();
				if (s.getLine(0).equals(ChatColor.AQUA + "[" + ChatColor.GREEN + "Ⓜ-Kit" + ChatColor.AQUA + "]")) {
					plugin.assignKitWithOutput(evt.getPlayer(), s.getLine(1), MauKits.KitAssignMode.SIGN);
				} else if (contains(ChatColor.DARK_AQUA + "[" + ChatColor.DARK_GREEN + "Deselect" + ChatColor.DARK_AQUA + "]", s.getLines())) {
					if (plugin.unassignKit(evt.getPlayer(), false)) evt.getPlayer().sendMessage(plugin.stag + plugin.translate("unassign"));
					else evt.getPlayer().sendMessage(plugin.errtag + plugin.translate("unassign.fail"));
				}
			}
		}
	}
	
	private boolean contains(String c, String... l) {
		for (String s : l)
			if (s.equals(c)) return true;
		return false;
	}
}
