package net.maunium.bukkit.MauKits.Configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.maunium.bukkit.Maussentials.Utils.Deserialization.DeserializationException;
import net.maunium.bukkit.Maussentials.Utils.Deserialization.DeserializationUtils;

/**
 * A kit gui item.
 * 
 * @author Tulir293
 * @since Pre-1.3
 */
@SerializableAs(value = "MauKitGuiItem")
public class KitGuiItem implements Serializable, ConfigurationSerializable {
	private static final long serialVersionUID = 293140001;
	
	private int x, y;
	private ItemStack is;
	
	public KitGuiItem(ItemStack is, int x, int y) {
		assert is != null;
		this.is = is;
		assert x > -1 && x < 9;
		this.x = x;
		assert y > -1;
		this.y = y;
	}
	
	public void addTo(Inventory i) {
		i.setItem(y * 9 + x, is);
	}
	
	public KitGuiItem(Map<String, Object> serialized) throws DeserializationException {
		x = DeserializationUtils.getValue("x", serialized, int.class);
		y = DeserializationUtils.getValue("y", serialized, int.class);
		is = DeserializationUtils.getValue("item", serialized, ItemStack.class);
	}
	
	public static KitGuiItem deserialize(Map<String, Object> serialized) throws DeserializationException {
		return new KitGuiItem(serialized);
	}
	
	public static KitGuiItem valueOf(Map<String, Object> serialized) throws DeserializationException {
		return new KitGuiItem(serialized);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<String, Object>();
		
		serialized.put("x", x);
		serialized.put("y", y);
		serialized.put("item", is);
		
		return serialized;
	}
}
