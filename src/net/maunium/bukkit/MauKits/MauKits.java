package net.maunium.bukkit.MauKits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Maps;

import net.milkbowl.vault.economy.Economy;

import net.maunium.bukkit.MauKits.Commands.CommandCreate;
import net.maunium.bukkit.MauKits.Commands.CommandDelete;
import net.maunium.bukkit.MauKits.Commands.CommandDeselect;
import net.maunium.bukkit.MauKits.Commands.CommandKitIcon;
import net.maunium.bukkit.MauKits.Commands.CommandMauKits;
import net.maunium.bukkit.MauKits.Commands.CommandSelect;
import net.maunium.bukkit.MauKits.Listeners.DamageListener;
import net.maunium.bukkit.MauKits.Listeners.FoodListener;
import net.maunium.bukkit.MauKits.Listeners.InteractListener;
import net.maunium.bukkit.MauKits.Listeners.JQListener;
import net.maunium.bukkit.MauKits.Listeners.PreCommandListener;
import net.maunium.bukkit.MauKits.Listeners.RespawnListener;
import net.maunium.bukkit.MauKits.Listeners.SignChangeListener;
import net.maunium.bukkit.Maussentials.Utils.I18n;
import net.maunium.bukkit.Maussentials.Utils.I18n.I15r;
import net.maunium.bukkit.Maussentials.Utils.MetadataUtils;

/**
 * Main class for MauKits.
 * 
 * @author Tulir293
 * @since Pre-1.3
 */
public class MauKits extends JavaPlugin implements I15r {
	/** The version of MauKits. */
	public String version;
	/** Metadata keys that MauKits uses. */
	public static final String META_SELECTED_KIT = "MauKits_SelectedKit", META_DESELECTED_TIMESTAMP = "MauKits_DeselectedRecently";
	/** The standard output and error output prefixes. */
	public String stag, errtag;
	/** A name-kit mapping. */
	private Map<String, Kit> kits = new HashMap<String, Kit>();
	/** The internationalization system. */
	private I18n i18n;
	/** The Vault economy instance. */
	private Economy econ;
	private int dsTimeout = 5000;
	private GuiHandler guiHandler;
	/**
	 * Set to true when the selected kit values have been saved to a temporary file and thus the
	 * kits should not be deselected on disable and the metadata will be re-set on enable.
	 */
	public boolean safeReloadPrepared;
	/** The folder containing the files containing kits. */
	public final File kitfolder = new File(getDataFolder(), "kits"), guiConf = new File(getDataFolder(), "gui.yml");
	
	@Override
	public void onEnable() {
		long st = System.currentTimeMillis();
		
		version = getDescription().getVersion();
		saveDefaultConfig();
		
		// Register configuration serializables.
		ConfigurationSerialization.registerClass(Kit.class);
		
		guiHandler = new GuiHandler(this);
		
		// Load configuration.
		reloadConfig();
		
		// Register listeners.
		getServer().getPluginManager().registerEvents(new PreCommandListener(this), this);
		getServer().getPluginManager().registerEvents(new RespawnListener(this), this);
		getServer().getPluginManager().registerEvents(new GuiHandler(this), this);
		getServer().getPluginManager().registerEvents(new InteractListener(this), this);
		getServer().getPluginManager().registerEvents(new SignChangeListener(this), this);
		getServer().getPluginManager().registerEvents(new JQListener(this), this);
		getServer().getPluginManager().registerEvents(new DamageListener(), this);
		getServer().getPluginManager().registerEvents(new FoodListener(), this);
		
		// Set executors for commands.
		getCommand("kit").setExecutor(new CommandSelect(this));
		getCommand("maukits").setExecutor(new CommandMauKits(this));
		getCommand("kiticon").setExecutor(new CommandKitIcon(this));
		getCommand("createkit").setExecutor(new CommandCreate(this));
		getCommand("deletekit").setExecutor(new CommandDelete(this));
		getCommand("deselectkit").setExecutor(new CommandDeselect(this));
		
		// Get Vault economy instance.
		RegisteredServiceProvider<Economy> econp = getServer().getServicesManager().getRegistration(Economy.class);
		econ = econp.getProvider();
		
		// Get the safereload temp file.
		File f = new File(getDataFolder(), "safereload.yml");
		// If it exists, load the data in it.
		if (f.exists()) {
			getLogger().info("Safe Reload data found. Loading...");
			YamlConfiguration conf = new YamlConfiguration();
			try {
				conf.load(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Loop through the online players and if the safe reload data contains an entry for
			// them, give them the meta of the kit defined in the safe reload data.
			for (Player p : getServer().getOnlinePlayers())
				if (conf.contains(p.getUniqueId().toString()))
					p.setMetadata(META_SELECTED_KIT, new FixedMetadataValue(this, conf.get(p.getUniqueId().toString())));
					
			// Delete the temporary safe reload data file.
			f.delete();
		}
		
		dsTimeout = getConfig().getInt("deselect-timeout");
		
		// Save the standard and error output prefixes.
		stag = translate("stag");
		errtag = translate("errtag");
		
		int et = (int) (System.currentTimeMillis() - st);
		getLogger().info("MauKits v" + version + " by Tulir293 enabled in " + et + "ms.");
	}
	
	@Override
	public void onDisable() {
		long st = System.currentTimeMillis();
		
		// If safe reload has been prepared, save the safe reload data to a temporary file.
		if (safeReloadPrepared) {
			getLogger().info("Safe reload was prepared. Activating Safe Reload.");
			// Get the safe reload data file.
			File f = new File(getDataFolder(), "safereload.yml");
			YamlConfiguration safereload = new YamlConfiguration();
			// Loop through online players and save their selected kit to the safe reload data file.
			for (Player p : getServer().getOnlinePlayers()) {
				if (p.hasMetadata(META_SELECTED_KIT)) {
					String kit = p.getMetadata(META_SELECTED_KIT).get(0).asString();
					safereload.set(p.getUniqueId().toString(), kit);
				}
			}
			// Save the safe reload data.
			try {
				safereload.save(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// Safe reload has not been prepared. Deselect everyones kits.
			for (Player p : getServer().getOnlinePlayers())
				unassignKit(p, true);
		}
		
		// Save configuration.
		saveConfig();
		
		int et = (int) (System.currentTimeMillis() - st);
		getLogger().info("MauKits v" + version + " by Tulir293 disabled in " + et + "ms.");
	}
	
	@Override
	public void reloadConfig() {
		saveResource("en_US.lang", true);
		saveResource("fi_FI.lang", true);
		super.reloadConfig();
		
		try {
			i18n = I18n.createInstance(getDataFolder(), getConfig().getString("language"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		YamlConfiguration gui = YamlConfiguration.loadConfiguration(guiConf);
		if (gui.contains("gui")) guiHandler.setContents(gui.getList("gui").toArray(new ItemStack[0]));
		else guiHandler.setContents(new ItemStack[guiHandler.length]);
		
		if (!kitfolder.exists()) kitfolder.mkdirs();
		Permission p = new Permission("maukits.kits.*", "Allows you to use all kits", PermissionDefault.OP);
		if (getServer().getPluginManager().getPermission(p.getName()) == null) getServer().getPluginManager().addPermission(p);
		for (File f : kitfolder.listFiles()) {
			if (f.isFile() && f.getName().endsWith(".yml")) {
				try {
					YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
					
					Object o = conf.get("value");
					if (o instanceof Kit) addKit((Kit) o);
				} catch (Throwable t) {
					getLogger().info("Failed to load kit from file " + f.getName());
					t.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void saveConfig() {
		YamlConfiguration gui = new YamlConfiguration();
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (ItemStack is : guiHandler.getContents()) {
			if (is != null) items.add(is);
			else items.add(new ItemStack(Material.AIR));
		}
		gui.set("gui", items);
		try {
			gui.save(guiConf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (!kitfolder.exists()) kitfolder.mkdirs();
		for (Kit k : kits.values()) {
			try {
				File f = new File(kitfolder, k.getName() + ".yml");
				YamlConfiguration conf = new YamlConfiguration();
				conf.set("value", k);
				conf.save(f);
			} catch (Throwable t) {
				getLogger().info("Failed to save kit " + k.getName());
				t.printStackTrace();
			}
		}
	}
	
	public boolean addKit(Kit k) {
		Permission p = new Permission("maukits.kits." + k.getName().toLowerCase(), "Allows you to use the kit " + k.getName(), PermissionDefault.OP);
		p.addParent("maukits.kits.*", true);
		if (getServer().getPluginManager().getPermission(p.getName()) == null) getServer().getPluginManager().addPermission(p);
		if (!getGuiHandler().containsItem(k.getIcon())) getGuiHandler().addGuiItem(k.getIcon());
		return kits.put(k.getName().toLowerCase(), k) != null;
	}
	
	public Map<String, Kit> getKits() {
		return Maps.newHashMap(kits);
	}
	
	public GuiHandler getGuiHandler() {
		return guiHandler;
	}
	
	public boolean removeKit(String name) {
		name = name.toLowerCase();
		if (containsKit(name)) {
			Kit k = kits.remove(name);
			File f = new File(kitfolder, k.getName() + ".yml");
			if (f.exists()) f.delete();
			getServer().getPluginManager().removePermission("maukits.kits." + k.getName().toLowerCase());
			return true;
		} else return false;
	}
	
	public boolean containsKit(String name) {
		name = name.toLowerCase();
		return kits.containsKey(name);
	}
	
	public Kit getKit(String kit) {
		kit = kit.toLowerCase();
		if (containsKit(kit)) return kits.get(kit);
		else return null;
	}
	
	public void assignKitWithOutput(Player p, String kit, KitAssignMode mode) {
		kit = kit.toLowerCase();
		if (containsKit(kit)) assignKitWithOutput(p, kits.get(kit), mode);
		else p.sendMessage(errtag + translate("kit.notfound", kit));
	}
	
	public void assignKitWithOutput(Player p, Kit kit, KitAssignMode mode) {
		AssignResult i = assignKit(p, kit, mode);
		switch (i) {
			case NOTFOUND:
				p.sendMessage(errtag + translate("kit.notfound", kit.getName()));
				break;
			case SUCCESS:
				p.sendMessage(stag + translate("assign.success", kit.getName()));
				break;
			case NOPERMS:
				p.sendMessage(errtag + translate("assign.noperms", kit.getName()));
				break;
			case ALREADYSELECTED:
				p.sendMessage(errtag + translate("assign.alreadyselected", kit.getName()));
				break;
			case COOLINGDOWN:
				p.sendMessage(errtag + translate("assign.cooldown"));
				break;
		}
	}
	
	public AssignResult assignKit(Player p, Kit k, KitAssignMode mode) {
		synchronized (p) {
			if (k.hasPermission(p)) {
				MetadataValue mv = MetadataUtils.getMetadata(p, META_DESELECTED_TIMESTAMP, this);
				if (mv != null && System.currentTimeMillis() - mv.asLong() < dsTimeout) return AssignResult.COOLINGDOWN;
				mv = MetadataUtils.getMetadata(p, META_DESELECTED_TIMESTAMP, this);
				if (mv != null && !p.hasPermission("maukits.reselect." + mode.toString())) return AssignResult.ALREADYSELECTED;
			} else return AssignResult.NOPERMS;
			
			p.setMetadata(META_SELECTED_KIT, new FixedMetadataValue(this, k));
			k.giveTo(p);
			return AssignResult.SUCCESS;
		}
	}
	
	public static enum AssignResult {
		NOTFOUND, SUCCESS, NOPERMS, ALREADYSELECTED, COOLINGDOWN;
	}
	
	public boolean unassignKit(Player p, boolean death) {
		clearInv(p);
		if (p.hasMetadata(META_SELECTED_KIT)) {
			p.removeMetadata(META_SELECTED_KIT, this);
			if (!death) p.setMetadata(META_DESELECTED_TIMESTAMP, new FixedMetadataValue(this, System.currentTimeMillis()));
			return true;
		} else return false;
	}
	
	public void clearInv(Player p) {
		for (PotionEffect pe : p.getActivePotionEffects())
			p.removePotionEffect(pe.getType());
		p.getInventory().setContents(new ItemStack[p.getInventory().getContents().length]);
		p.getInventory().setArmorContents(new ItemStack[p.getInventory().getArmorContents().length]);
		
		ItemStack is = new ItemStack(Material.FEATHER);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(translate("gui.enter.name"));
		im.setLore(Arrays.asList(translate("gui.enter.lore").split("\n")));
		is.setItemMeta(im);
		p.getInventory().addItem(is);
	}
	
	public Economy getEconomy() {
		return econ;
	}
	
	public static enum KitAssignMode {
		COMMAND, SIGN, GUI;
		@Override
		public String toString() {
			switch (this) {
				case COMMAND:
					return "command";
				case SIGN:
					return "sign";
				case GUI:
					return "gui";
				default:
					throw new RuntimeException("Impossible switch clause state");
			}
		}
	}
	
	@Override
	public String translate(String node, Object... replace) {
		return i18n.translate(node, replace);
	}
}