package eu.epicdark.epicmagics.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.ChiseledBookshelf;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import eu.epicdark.epicmagics.EpicMagics;
import eu.epicdark.epicmagics.crafting.cauldron.CauldronRecipe;
import eu.epicdark.epicmagics.utils.BlockUtils;
import eu.epicdark.epicmagics.utils.CauldronData;
import net.kyori.adventure.text.Component;

public class ItemDropListener implements Listener{
	
	@EventHandler
	public void onCauldron(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		ItemStack stack = item.getItemStack();
		World world = item.getWorld();
		
		if(stack.getType() == Material.STICK && stack.getItemMeta().displayName().contains(Component.text("Zauberstab")) && stack.getEnchantments().isEmpty()) {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					
					if(item == null || Bukkit.getEntity(item.getUniqueId()) == null) {
						cancel();
						return;
					}
					
					Set<Block> bookshelves = BlockUtils.surroundingBlocks(item.getLocation(), 4, false, block -> {
						if(block.getType() == Material.BOOKSHELF) {
							return true;
						}
						//if chiseled bookshelf, at least one book required
						if(block.getType() == Material.CHISELED_BOOKSHELF) {
							ChiseledBookshelf data = (ChiseledBookshelf) block.getBlockData();
							if(data.getOccupiedSlots().size()>0) {
								return true;
							}
						}
						return false;
					});
					
					for(int i = 0; i < bookshelves.size(); i++) {
						world.spawnParticle(Particle.ENCHANT, item.getLocation(), 1, 2, 2, 2, 0.1);
					}
					
					List<Entity> surroundingItems = item.getNearbyEntities(1, 1, 1).stream().filter(entity -> (entity instanceof Item)).collect(Collectors.toList());
					for(Entity n : surroundingItems) {
						Item i = (Item)n;
						if(i.getItemStack().getType() != Material.CHORUS_FRUIT) {
							continue;
						}
						
						i.remove();
						item.setItemStack(EpicMagics.MAGICSTICK);
						item.setPickupDelay(20);
						item.setGlowing(true);
						item.setGravity(false);
						item.setVelocity(new Vector(0, 1, 0));
						
						world.strikeLightningEffect(item.getLocation());
						world.playSound(item.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, SoundCategory.AMBIENT, 1, 0.5f);
						world.playSound(item.getLocation(), Sound.ENTITY_WITHER_DEATH, SoundCategory.AMBIENT, 1, 1);
						
						cancel();
						return;
					}
					
				}
			};
		}
		
		if(CauldronRecipe.isInRecipe(stack)) {
			new BukkitRunnable() {
				
				@Override
				public void run() {
					
					if(item == null || Bukkit.getEntity(item.getUniqueId()) == null) {
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
			return;
		}
		
		
		
	}
	
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event) {
		
	}

}
