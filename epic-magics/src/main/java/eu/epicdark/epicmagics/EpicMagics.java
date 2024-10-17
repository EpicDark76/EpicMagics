package eu.epicdark.epicmagics;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import eu.epicdark.epicmagics.commands.MagicstickCommand;
import eu.epicdark.epicmagics.crafting.cauldron.CauldronRecipeNew;
import eu.epicdark.epicmagics.listener.InteractListener;
import eu.epicdark.epicmagics.listener.ItemDropListener;
import eu.epicdark.epicmagics.utils.ItemBuilder;
import net.kyori.adventure.text.Component;

public class EpicMagics extends JavaPlugin{
	public static EpicMagics INSTANCE;
	
	public static ItemStack MAGICSTICK;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		MAGICSTICK = new ItemBuilder(Material.STICK).enchantmentGlint(true).itemName(Component.text("Zauberstab")).rarity(ItemRarity.EPIC).maxStackSize(1).build();
		
		new CauldronRecipeNew(new NamespacedKey(this, "magicstick"), MAGICSTICK).addIngredient(new RecipeChoice.MaterialChoice(Material.STICK)).addIngredient(new RecipeChoice.MaterialChoice(Material.EMERALD));
		
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
	
	public static void info(String string) {
		INSTANCE.getLogger().info(string);
	}

}
