package eu.epicdark.epicmagics.crafting.cauldron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import eu.epicdark.epicmagics.EpicMagics;
import eu.epicdark.epicmagics.utils.CauldronData;
import eu.epicdark.epicmagics.utils.EpicUtils;

public class CauldronRecipe implements Recipe, Keyed{
	
	private static final HashMap<NamespacedKey, CauldronRecipe> RECIPES = new HashMap<>();
	private static BukkitTask TASK;
	
	private final NamespacedKey key;
    private final ItemStack output;
    
    private final boolean requiresWater;
    private final boolean requiresLava;
    private final int minFluidLevel;
    
    private final boolean requiresCampfire;
    private final boolean requiresSoulCampfire;
    private final boolean requiresLitCampfire;
    
    private final Set<RecipeChoice> ingredients;
    
    public CauldronRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result) {
    	this(key, result, false, false, 0, false, false, false, new RecipeChoice[] {});
    }
    
    public CauldronRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result, RecipeChoice...choices) {
    	this(key, result, false, false, 0, false, false, false, choices);
    }
    
    public CauldronRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result, @NotNull boolean requiresWater, @NotNull boolean requiresLava, @NotNull int minFluidLevel, boolean requiresCampfire, boolean requiresSoulCampfire, boolean requiresLitCampfire, RecipeChoice...choices) {
    	Preconditions.checkArgument(key != null, "key cannot be null");
		Preconditions.checkArgument(!RECIPES.containsKey(key), "key already in use");
		Preconditions.checkArgument(!result.isEmpty(), "Recipe cannot have an empty result.");
		Preconditions.checkArgument(minFluidLevel >= 0 && minFluidLevel <= 3, "minFluidLevel must be defined in [0;3], but is " + minFluidLevel);
		this.key = key;
		this.output = new ItemStack(result);
		this.requiresWater = requiresWater;
		this.requiresLava = requiresLava;
		this.minFluidLevel = minFluidLevel;
		
		this.requiresCampfire = requiresCampfire;
		this.requiresSoulCampfire = requiresSoulCampfire;
		this.requiresLitCampfire = requiresLitCampfire;
		
		this.ingredients = Sets.newHashSet(choices);
		
		RECIPES.put(key, this);
		
		if(TASK == null) {
			startTask();
		}
    }
    
    public CauldronRecipe addIngredient(RecipeChoice choice) {
    	this.ingredients.add(choice);
    	return this;
    }

	@Override
	public @NotNull NamespacedKey getKey() {
		return key;
	}

	@Override
	public @NotNull ItemStack getResult() {
		return output;
	}
	
	public boolean requiresWater() {
		return requiresWater;
	}
	
	public boolean requiresLava() {
    	return requiresLava;
    }
    
    public int requiredFluidLevel() {
    	return minFluidLevel;
    }
    
    public boolean requiresCampfire() {
    	return requiresCampfire;
    }
    
    public boolean requiresSoulCampfire() {
    	return requiresSoulCampfire;
    }
    
    public boolean requiresLitCampfire() {
    	return requiresLitCampfire;
    }
	
    public Set<RecipeChoice> getIngredients() {
    	return ingredients;
    }
    
	public static void startTask() {
		TASK = new BukkitRunnable() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				
				if(CauldronData.getItemMap().isEmpty()) {
					return;
				}
				
				for(Block block : CauldronData.getItemMap().keySet()) {
					ArrayList<Item> items = (ArrayList<Item>) CauldronData.getItemMap().get(block).clone();
					
					//remove block from cache map if no items are there anymore
					if(items == null || items.isEmpty()) {
						CauldronData.getItemMap().remove(block);
						continue;
					}
					
					//remove despawned items
					for(Item item : CauldronData.getItemMap().get(block)) {
						if(item == null || Bukkit.getEntity(item.getUniqueId()) == null) {
							items.remove(item);
						}
					}
					
					
					//remove again after invalid items have been removed
					if(items.isEmpty()) {
						CauldronData.getItemMap().remove(block);
						continue;
					}else {
						//set new item list if not empty
						CauldronData.getItemMap().put(block, items);
					}
					
					final CauldronData data = new CauldronData(block);
					
					for(CauldronRecipe recipe : RECIPES.values()) {
						
						if(recipe.requiresWater && !data.hasWater()) {
							continue;
						}
						if(recipe.requiresLava && !data.hasLava()) {
							continue;
						}
						if(recipe.minFluidLevel > data.getLevel()) {
							continue;
						}
						if(recipe.requiresCampfire && !data.hasCampfire()) {
							continue;
						}
						if(recipe.requiresSoulCampfire && !data.hasSoulCampfire()) {
							continue;
						}
						if(recipe.requiresLitCampfire && (data.getBlockBelow().getBlockData() instanceof Campfire) && ((Campfire)data.getBlockBelow().getBlockData()).isLit()) {
							continue;
						}
						
						List<Item> craftingItems = (List<Item>) items.clone();
						Set<RecipeChoice> choices = EpicUtils.cloneSet(recipe.getIngredients());
						for(RecipeChoice choice : recipe.getIngredients()) {
							
							for(Item item : craftingItems) {
								if(choice.test(item.getItemStack())) {
									choices.remove(choice);
								}
							}
							
						}
						
						if(choices.isEmpty()) {
							block.getWorld().playSound(block.getLocation(), Sound.ENTITY_WITHER_DEATH, SoundCategory.AMBIENT, 1, 1);
							block.getWorld().dropItem(block.getLocation().toCenterLocation(), recipe.getResult());
							items.forEach(item -> item.remove());
							break;
						}
					}
				}
				
			}
		}.runTaskTimer(EpicMagics.INSTANCE, 0, 10);
	}
	
	public static HashMap<NamespacedKey, CauldronRecipe> getRecipes() {
		return RECIPES;
	}
	
	public static boolean isInRecipe(ItemStack item) {
		for(CauldronRecipe recipe : RECIPES.values()) {
			Set<RecipeChoice> ingredients = recipe.getIngredients();
			for(RecipeChoice ingredient : ingredients) {
				if(ingredient.test(item)) {
					return true;
				}
			}
		}
		return false;
	}

}
