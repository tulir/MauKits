package net.maunium.bukkit.MauKits.Gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.maunium.bukkit.MauKits.MauKits;
import net.maunium.bukkit.MauKits.MauKits.AssignResult;
import net.maunium.bukkit.MauKits.MauKits.KitAssignMode;
import net.maunium.bukkit.MauKits.Configuration.Kit;
import net.maunium.bukkit.MauKits.Configuration.KitGuiItem;

public class GuiSelect implements Listener {
	private MauKits plugin;
	
	public GuiSelect(MauKits plugin) {
		this.plugin = plugin;
	}
	
	public void open(Player p) {
		Inventory gui = plugin.getServer().createInventory(null, 36, ChatColor.GREEN + "" + ChatColor.BOLD + plugin.translate("gui.title"));
		for (Kit k : plugin.getKits().values())
			if (k.hasCustomPosition()) gui.setItem(k.getCustomGuiY() * 9 + k.getCustomGuiX(), k.getIcon());
			else gui.addItem(k.getIcon());
		for (KitGuiItem kgi : plugin.extra)
			kgi.addTo(gui);
		p.openInventory(gui);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent evt) {
		if (!(evt.getWhoClicked() instanceof Player)) return;
		Player p = (Player) evt.getWhoClicked();
		Inventory i = evt.getInventory();
		if (!i.getTitle().equals(plugin.translate("gui.title")) || i.getHolder() == null) return;
		evt.setCancelled(true);
		ItemStack is = evt.getCurrentItem();
		if (is == null || is.getType() == Material.AIR) return;
		for (Kit k : plugin.getKits().values()) {
			if (is.equals(k.getIcon()) && k.hasPermission(p)) {
				AssignResult s = plugin.assignKit(p, k, KitAssignMode.GUI);
				switch (s) {
					case NOTFOUND:
						p.sendMessage(plugin.errtag + plugin.translate("kit.notfound", k.getName()));
						i.clear();
						p.closeInventory();
						break;
					case SUCCESS:
						p.sendMessage(plugin.stag + plugin.translate("assign.success", k.getName()));
						i.clear();
						p.closeInventory();
						break;
					case NOPERMS:
						p.sendMessage(plugin.errtag + plugin.translate("assign.noperms", k.getName()));
						break;
					case ALREADYSELECTED:
						p.sendMessage(plugin.errtag + plugin.translate("assign.alreadyselected", k.getName()));
						break;
					case COOLINGDOWN:
						p.sendMessage(plugin.errtag + plugin.translate("assign.cooldown"));
						break;
				}
			}
		}
	}
}
