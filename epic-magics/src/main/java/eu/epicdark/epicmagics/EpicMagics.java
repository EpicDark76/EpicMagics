package eu.epicdark.epicmagics;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import eu.epicdark.epicmagics.commands.MagicstickCommand;
import eu.epicdark.epicmagics.crafting.cauldron.ShapelessCauldronRecipe;
import eu.epicdark.epicmagics.listener.InteractListener;
import eu.epicdark.epicmagics.listener.ItemDropListener;
import eu.epicdark.epicmagics.utils.CauldronData;
import eu.epicdark.epicmagics.utils.ItemBuilder;
import net.kyori.adventure.text.Component;

public class EpicMagics extends JavaPlugin{
	public static EpicMagics INSTANCE;
	
	public static ItemStack MAGICSTICK;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		MAGICSTICK = new ItemBuilder(Material.STICK).enchantmentGlint(true).itemName(Component.text("Zauberstab")).rarity(ItemRarity.EPIC).maxStackSize(1).build();
		
		new ShapelessCauldronRecipe(new NamespacedKey(this, "epicmagics"), MAGICSTICK).addIngredient(new RecipeChoice.MaterialChoice(Material.STICK)).addIngredient(new RecipeChoice.MaterialChoice(Material.EMERALD));
		
		Block block = Bukkit.getWorlds().get(0).getBlockAt(252, 71, 633);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				List<Item> list = CauldronData.getContainedItems(block);
				getLogger().info(list.toString());
				
			}
		}.runTaskTimer(this, 0, 20);
		
		registerListeners();
		registerCommands();
		
		this.getLogger().info("Finished loading " + this.getName());
	}
	
	@Override
	public void onDisable() {
		
	}
	
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new InteractListener(), this);
		pm.registerEvents(new ItemDropListener(), this);
	}
	
	private void registerCommands() {
		this.getCommand("magicstick").setExecutor(new MagicstickCommand());
	}

}
