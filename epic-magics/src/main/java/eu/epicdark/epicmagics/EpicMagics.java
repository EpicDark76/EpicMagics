package eu.epicdark.epicmagics;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import eu.epicdark.epicmagics.commands.MagicstickCommand;
import eu.epicdark.epicmagics.listener.InteractListener;
import eu.epicdark.epicmagics.utils.ItemBuilder;
import net.kyori.adventure.text.Component;

public class EpicMagics extends JavaPlugin{
	public static EpicMagics INSTANCE;
	
	public static ItemStack MAGICSTICK;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		MAGICSTICK = new ItemBuilder(Material.STICK).enchantmentGlint(true).itemName(Component.text("Zauberstab")).rarity(ItemRarity.EPIC).maxStackSize(1).build();
		
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
	}
	
	private void registerCommands() {
		this.getCommand("magicstick").setExecutor(new MagicstickCommand());
	}

}
