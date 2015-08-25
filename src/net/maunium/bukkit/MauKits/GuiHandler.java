package net.maunium.bukkit.MauKits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.maunium.bukkit.MauKits.MauKits.AssignResult;
import net.maunium.bukkit.MauKits.MauKits.KitAssignMode;

public class GuiHandler implements Listener {
	public int length = 6 * 9;
	private ItemStack[] gui = new ItemStack[length];
	private MauKits plugin;
	
	public GuiHandler(MauKits plugin) {
		this.plugin = plugin;
	}
	
	public ItemStack[] getContents() {
		return gui;
	}
	
	public void addGuiItem(ItemStack is) {
		if (is == null) return;
		for (int i = 0; i < gui.length; i++)
			if (gui[i] == null || gui[i].getType() == Material.AIR) gui[i] = is;
	}
	
	public void replaceGuiItem(ItemStack oldItem, ItemStack newItem) {
		if (oldItem == null) return;
		for (int i = 0; i < gui.length; i++)
			if (gui[i].equals(oldItem)) gui[i] = newItem;
	}
	
	public boolean containsItem(ItemStack is) {
		if (is == null) return true;
		for (ItemStack item : getContents())
			if (is.equals(item)) return true;
		return false;
	}
	
	public void setContents(ItemStack[] contents) {
		ItemStack air = new ItemStack(Material.AIR);
		if (contents.length != length) {
			gui = new ItemStack[length];
			for (int i = 0; i < contents.length; i++) {
				if (contents[i] != null) gui[i] = contents[i];
				else gui[i] = air;
			}
		} else gui = contents;
		System.out.println(contents.length + ", " + length + ", " + gui.length + ", " + equals(contents, gui));
	}
	
	public boolean equals(ItemStack[] i1, ItemStack[] i2) {
		if (i1.length != i2.length) return false;
		for (int i = 0; i < i1.length; i++) {
			if (i1[i] == null) {
				if (i2[i] == null) continue;
			}
			if (!i1[i].equals(i2[i])) return false;
		}
		return true;
	}
	
	public void openSelector(Player p) {
		Inventory gui = plugin.getServer().createInventory(null, length, plugin.translate("gui.title"));
		gui.setContents(this.gui);
		p.openInventory(gui);
	}
	
	public void openEditor(Player p) {
		Inventory gui = plugin.getServer().createInventory(null, length, plugin.translate("gui.edit.title"));
		gui.setContents(this.gui);
		p.openInventory(gui);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent evt) {
		if (!(evt.getPlayer() instanceof Player)) return;
		Player p = (Player) evt.getPlayer();
		Inventory i = evt.getInventory();
		if (!i.getTitle().equals(plugin.translate("gui.edit.title"))) return;
		setContents(i.getContents());
		p.sendMessage(plugin.stag + "Updated GUI");
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent evt) {
		if (!(evt.getWhoClicked() instanceof Player)) return;
		Player p = (Player) evt.getWhoClicked();
		Inventory i = evt.getInventory();
		if (!i.getTitle().equals(plugin.translate("gui.title"))) return;
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
