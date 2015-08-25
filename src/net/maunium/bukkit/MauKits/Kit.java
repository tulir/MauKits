package net.maunium.bukkit.MauKits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import net.maunium.bukkit.Maussentials.Utils.Deserialization.DeserializationException;
import net.maunium.bukkit.Maussentials.Utils.Deserialization.DeserializationUtils;

/**
 * The container for all kits.
 * 
 * @author Tulir293
 * @since Pre-1.3
 */
@SerializableAs(value = "MauKit")
public class Kit implements Serializable, ConfigurationSerializable {
	private static final long serialVersionUID = 293130001;
	
	private String kitName;
	private ItemStack[] mainInventory;
	private ItemStack[] armor;
	private ItemStack icon, refill;
	private int guiX = -1, guiY = -1;
	private List<PotionEffect> potions;
	
	/**
	 * Create a kit with the given name and contents.
	 * 
	 * @param name The name of the kit.
	 * @param inv The main inventory contents of the kit.
	 * @param armor The armor of the kit.
	 * @param potions The potion effects of the kit.
	 */
	public Kit(String name, ItemStack[] inv, ItemStack[] armor, List<PotionEffect> potions) {
		kitName = name;
		mainInventory = inv;
		this.armor = armor;
		this.potions = potions;
		icon = new ItemStack(Material.DIAMOND_SWORD);
		refill = new ItemStack(Material.MUSHROOM_SOUP);
	}
	
	/**
	 * Create a kit with the given name and the items that the given player has equipped.
	 * 
	 * @param name The name of the kit.
	 * @param p The player to get the contents from.
	 */
	public Kit(String name, Player p) {
		kitName = name;
		mainInventory = p.getInventory().getContents();
		armor = p.getInventory().getArmorContents();
		
		List<PotionEffect> potions = new ArrayList<PotionEffect>();
		potions.addAll(p.getActivePotionEffects());
		this.potions = potions;
		
		icon = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta im = icon.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + getName());
		icon.setItemMeta(im);
		
		Map<ItemStack, Integer> amount = new HashMap<ItemStack, Integer>();
		for (ItemStack is : p.getInventory().getContents()) {
			if (amount.containsKey(is)) amount.put(is, amount.get(is) + 1);
			else amount.put(is, 1);
		}
		Entry<ItemStack, Integer> highest = null;
		for (Entry<ItemStack, Integer> e : amount.entrySet()) {
			if (highest == null) highest = e;
			else if (highest.getValue() < e.getValue()) highest = e;
		}
		refill = highest.getKey();
	}
	
	/**
	 * Get the name of this kit.
	 */
	public String getName() {
		return kitName;
	}
	
	public ItemStack getRefillItem() {
		return refill;
	}
	
	public void setRefillItem(ItemStack refill) {
		this.refill = refill;
	}
	
	/**
	 * Set the icon of this kit.
	 */
	public void setIcon(ItemStack is) {
		icon = is;
	}
	
	/**
	 * Get a copy of the icon of this kit. Use {@link #setIcon(ItemStack)} when modifying the icon.
	 */
	public ItemStack getIcon() {
		return icon.clone();
	}
	
	/**
	 * Check whether or not this kit item has been placed in a admin-defined slot.<br>
	 * TODO: All kits should have a specific slot in the GUI.
	 */
	public boolean hasCustomPosition() {
		return guiX > -1 && guiY > -1;
	}
	
	/**
	 * Set the position of this kit in the kit gui.
	 */
	public void setCustomPosition(int guiX, int guiY) {
		this.guiX = guiX;
		this.guiY = guiY;
	}
	
	/**
	 * Get the gui X position.
	 */
	public int getCustomGuiX() {
		return guiX;
	}
	
	/**
	 * Get the gui Y position.
	 */
	public int getCustomGuiY() {
		return guiY;
	}
	
	/**
	 * Remove the custom gui position of this kit.<br>
	 * TODO: All kits should have a specific slot in the GUI, thus removing the position should not be possible.
	 */
	public void removeCustomPosition() {
		guiX = -1;
		guiY = -1;
	}
	
	/**
	 * Check if the given player has permissions for this kit.
	 */
	public boolean hasPermission(Player p) {
		return p.hasPermission("maukits.kits." + kitName);
	}
	
	/**
	 * Give this kit to the given player.
	 */
	public void giveTo(Player p) {
		p.getInventory().setArmorContents(armor);
		p.getInventory().setContents(mainInventory);
		
		for (PotionEffect pe : p.getActivePotionEffects())
			p.removePotionEffect(pe.getType());
		p.addPotionEffects(potions);
	}
	
	public Kit(Map<String, Object> serialized) throws DeserializationException {
		kitName = DeserializationUtils.getValue("name", serialized, String.class);
		guiX = DeserializationUtils.getValue("guix", serialized, int.class, -1);
		guiY = DeserializationUtils.getValue("guiy", serialized, int.class, -1);
		icon = DeserializationUtils.getValue("icon", serialized, ItemStack.class);
		
		List<?> list = DeserializationUtils.getValue("inventory", serialized, List.class);
		mainInventory = new ItemStack[36];
		for (int i = 0; i < mainInventory.length; i++) {
			Object o = list.get(i);
			if (o != null && o instanceof ItemStack && ((ItemStack) o).getType() != Material.AIR) mainInventory[i] = (ItemStack) o;
			else mainInventory[i] = null;
		}
		
		list = DeserializationUtils.getValue("armor", serialized, List.class);
		armor = new ItemStack[4];
		for (int i = 0; i < armor.length; i++) {
			Object o = list.get(i);
			if (o != null && o instanceof ItemStack && ((ItemStack) o).getType() != Material.AIR) armor[i] = (ItemStack) o;
			else armor[i] = null;
		}
		
		list = DeserializationUtils.getValue("potions", serialized, List.class);
		potions = new ArrayList<PotionEffect>();
		for (Object oo : list) {
			if (oo != null && oo instanceof PotionEffect) potions.add((PotionEffect) oo);
			else potions.add(null);
		}
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<String, Object>();
		serialized.put("name", getName());
		serialized.put("icon", getIcon());
		
		if (guiX > -1) serialized.put("guix", guiX);
		if (guiY > -1) serialized.put("guiy", guiY);
		
		List<ItemStack> mainInv = new ArrayList<ItemStack>(mainInventory.length);
		for (ItemStack is : mainInventory) {
			if (is != null) mainInv.add(is);
			else mainInv.add(new ItemStack(Material.AIR));
		}
		serialized.put("inventory", mainInv);
		
		List<ItemStack> arm = new ArrayList<ItemStack>(armor.length);
		for (ItemStack is : armor) {
			if (is != null) arm.add(is);
			else arm.add(new ItemStack(Material.AIR));
		}
		serialized.put("armor", arm);
		
		serialized.put("potions", potions);
		
		return serialized;
	}
	
	public static Kit valueOf(Map<String, Object> serialized) throws DeserializationException {
		return new Kit(serialized);
	}
	
	public static Kit deserialize(Map<String, Object> serialized) throws DeserializationException {
		return new Kit(serialized);
	}
}
