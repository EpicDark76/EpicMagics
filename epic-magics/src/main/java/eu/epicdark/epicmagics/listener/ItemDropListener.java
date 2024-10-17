package eu.epicdark.epicmagics.listener;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.epicdark.epicmagics.EpicMagics;
import eu.epicdark.epicmagics.crafting.cauldron.CauldronRecipe;
import eu.epicdark.epicmagics.utils.CauldronData;

public class ItemDropListener implements Listener{
	
	@EventHandler
	public void onCauldron(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		ItemStack stack = item.getItemStack();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if(stack == null) {
					cancel();
					return;
				}
				
				Location location = item.getLocation();
				Block block = location.getBlock();
				if(block.getType().name().contains("CAULDRON")) {
					ArrayList<Item> list = CauldronData.getContainedItems(block);
					list.add(item);
					CauldronData.getItemMap().put(block, list);
					cancel();
					return;
				}
			}
		}.runTaskTimer(EpicMagics.INSTANCE, 0, 5);
		
		if(CauldronRecipe.getRecipes().isEmpty()) {
			return;
		}
		if(!CauldronRecipe.isInRecipe(stack)) {
			return;
		}
		
	}
	
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event) {
		
	}

}
