package net.maunium.bukkit.MauKits;

import java.util.Arrays;

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
	private ItemStack[] content;
	private MauKits plugin;
	
	public GuiHandler(MauKits plugin) {
		this.plugin = plugin;
		content = new ItemStack[length];
		Arrays.fill(content, null);
	}
	
	public ItemStack[] getContents() {
		return content;
	}
	
	public void addGuiItem(ItemStack is) {
		if (is == null) return;
		System.out.println("Add " + is);
		for (int i = 0; i < content.length; i++) {
			if (content[i] == null || content[i].getType() == Material.AIR) {
				content[i] = is;
				return;
			}
		}
	}
	
	public void replaceGuiItem(ItemStack oldItem, ItemStack newItem) {
		if (oldItem == null) return;
		for (int i = 0; i < content.length; i++)
			if (content[i].equals(oldItem)) content[i] = newItem;
	}
	
	public boolean containsItem(ItemStack is) {
		if (is == null) return true;
		for (ItemStack item : content)
			if (is.equals(item)) return true;
		return false;
	}
	
	public void setContents(ItemStack[] contents) {
		Arrays.fill(content, null);
		System.arraycopy(contents, 0, content, 0, contents.length >= length ? length : contents.length);
	}
	
	public void openSelector(Player p) {
		Inventory gui = plugin.getServer().createInventory(p, length, plugin.translate("gui.title"));
		gui.setContents(content);
		p.openInventory(gui);
	}
	
	public void openEditor(Player p) {
		Inventory gui = plugin.getServer().createInventory(p, length, plugin.translate("gui.edit.title"));
		gui.setContents(content);
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
