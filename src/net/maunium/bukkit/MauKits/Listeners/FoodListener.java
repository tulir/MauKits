package net.maunium.bukkit.MauKits.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodListener implements Listener {
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent evt) {
		evt.setFoodLevel(20);
	}
}
