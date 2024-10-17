package eu.epicdark.epicmagics;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import eu.epicdark.epicmagics.commands.MagicstickCommand;
import eu.epicdark.epicmagics.crafting.cauldron.CauldronRecipe;
import eu.epicdark.epicmagics.listener.InteractListener;
import eu.epicdark.epicmagics.listener.ItemDropListener;
import eu.epicdark.epicmagics.listener.ServerListener;
import eu.epicdark.epicmagics.utils.ItemBuilder;
import io.papermc.paper.datapack.DatapackManager;
import net.kyori.adventure.text.Component;

public class EpicMagics extends JavaPlugin{
	public static EpicMagics INSTANCE;
	
	public static ItemStack MAGICSTICK;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		MAGICSTICK = new ItemBuilder(Material.STICK).enchantmentGlint(true).itemName(Component.text("Zauberstab")).rarity(ItemRarity.EPIC).maxStackSize(1).build();
		
		new CauldronRecipe(new NamespacedKey(this, "magicstick"), MAGICSTICK, new RecipeChoice.MaterialChoice(Material.STICK), new RecipeChoice.MaterialChoice(Material.EMERALD));
		
		registerListeners();
		registerCommands();
		loadDatapacks();
		
		this.getLogger().info("Finished loading " + this.getName());
	}
	
	@Override
	public void onDisable() {
		
	}
	
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new InteractListener(), this);
		pm.registerEvents(new ItemDropListener(), this);
		pm.registerEvents(new ServerListener(), this);
	}
	
	private void registerCommands() {
		this.getCommand("magicstick").setExecutor(new MagicstickCommand());
	}
	
	public void loadDatapacks() {
		DatapackManager dm = Bukkit.getDatapackManager();
		Set<String> datapacks= dm.getEnabledPacks().stream().map(pack -> pack.getName()).collect(Collectors.toUnmodifiableSet());
		
		FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if(name.endsWith(".json")) {
					return true;
				}
				return false;
			}
		};
		
		JSONParser parser = new JSONParser();
		File datapackFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "datapacks");
		for(File datapack : datapackFolder.listFiles()) {
			if(datapack.isDirectory()) {
				
				//check if folder is valid datapack
				File mcmeta = new File(datapack, "pack.mcmeta");
				if(!mcmeta.exists()) {
					continue;
				}
				//check if folder contains recipe data folder
				File recipeFolder = new File(datapack, "data" + File.separator + this.getName().toLowerCase() + File.separator + "recipe");
				if(!recipeFolder.exists()) {
					continue;
				}
				
				try {
					for(File file : recipeFolder.listFiles(filter)) {
						JSONObject object = (JSONObject) parser.parse(new FileReader(file));
						String type = (String) object.get("type");
						JSONArray ingredients = (JSONArray) object.get("ingredients");
						JSONObject result = (JSONObject) object.get("result");
					}
				}catch(Exception ex) {
					continue;
				}
				
			}else if(datapack.toString().endsWith(".zip") || datapack.toString().endsWith(".jar")) {
				
				try(ZipFile file = new ZipFile(datapackFolder)) {
					for(ZipEntry entry : file.stream().toList()) {
						
					}
				}catch(Exception ex) {
					continue;
				}
				
			}else {
				continue;
			}
		}
	}
	
	public static void info(String string) {
		INSTANCE.getLogger().info(string);
	}

}
