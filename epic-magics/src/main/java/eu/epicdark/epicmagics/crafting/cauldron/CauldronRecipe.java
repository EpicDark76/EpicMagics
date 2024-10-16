package eu.epicdark.epicmagics.crafting.cauldron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import eu.epicdark.epicmagics.EpicMagics;
import eu.epicdark.epicmagics.utils.CauldronData;

public class CauldronRecipe implements Recipe, Keyed{
	private static final HashMap<NamespacedKey, CauldronRecipe> RECIPES = new HashMap<>();
	private static BukkitTask TASK;
	
	private final NamespacedKey key;
    private final ItemStack output;
    private final boolean reqireWater;
    private final boolean requireLava;
    private final int minFluidLevel;
    private final boolean requireCampfire;
    private final boolean requireSoulCampfire;
    private final boolean requireLitCampfire;
	
    protected CauldronRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result) {
		this(key, result, false, false, 0, false, false, false);
	}
    
	protected CauldronRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result, @NotNull boolean requireWater, boolean requireLava, int fluidLevel, boolean requireCampfire, boolean requireSoulCampfire, boolean requireLitCampfire) {
		Preconditions.checkArgument(key != null, "key cannot be null");
		Preconditions.checkArgument(!RECIPES.containsKey(key), "key already in use");
		Preconditions.checkArgument(0 >= fluidLevel && fluidLevel >= 3, "fluidLevel must be defined in [0;3]");
		this.key = key;
		this.output = new ItemStack(result);
		this.reqireWater = requireWater;
		this.requireLava = requireLava;
		this.minFluidLevel = fluidLevel;
		this.requireCampfire = requireCampfire;
		this.requireSoulCampfire = requireSoulCampfire;
		this.requireLitCampfire = requireLitCampfire;
		
		RECIPES.put(key, this);
		
		if(TASK == null) {
			EpicMagics.INSTANCE.getLogger().info("Starting cauldron task...");
			TASK = new BukkitRunnable() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					
					if(CauldronData.getItemMap().isEmpty()) {
						return;
					}
					for(Block block : CauldronData.getItemMap().keySet()) {
						ArrayList<Item> items = CauldronData.getItemMap().get(block);
						if(items.isEmpty()) {
							continue;
						}
						//remove despawned or picked up items from list
						items.forEach(item -> {
							if(item == null || item.getItemStack() == null) {
								EpicMagics.INSTANCE.getLogger().info("Removing item from list");
								items.remove(item);
							}
						});
						CauldronData.getItemMap().put(block, items);
						
						
						for(ShapelessCauldronRecipe recipe : ShapelessCauldronRecipe.getShapelessRecipes()) {
							
							List<Item> craftingItems = new ArrayList<>();
							ArrayList<Item> cloned = (ArrayList<Item>) items.clone();
							int success = 0;
							for(RecipeChoice choice : recipe.getIngredients()) {
								for(int i = 0; i < cloned.size(); i++) {
									Item item = cloned.get(i);
									if(item == null || item.getItemStack().isEmpty()) {
										continue;
									}
									if(choice.test(item.getItemStack())) {
										success++;
										//remove item from list to prevent it being counted as another possible ingredient for recipe
										item.setItemStack(new ItemStack(Material.AIR));
										cloned.set(i, item);
										continue;
									}
								}
							}
							
							if(success >= recipe.getIngredients().size()) {
								block.getWorld().playSound(block.getLocation(), Sound.ENTITY_WITHER_DEATH, SoundCategory.BLOCKS, 1, 1);
								block.getWorld().dropItem(block.getLocation().toCenterLocation(), recipe.getResult());
								craftingItems.forEach(item -> item.remove());
								return;
							}
							
						}
						
					}
					
				}
			}.runTaskTimerAsynchronously(EpicMagics.INSTANCE, 0, 20);
		}
	}
	
	@NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    /**
     * Get the result of this recipe.
     *
     * @return The result stack.
     */
    @Override
    @NotNull
    public ItemStack getResult() {
        return output.clone();
    }
    
    /**
     * Get if the cauldron needs to be filled with water
     *
     * @return The result boolean.
     */
    public boolean requiresWater() {
    	return reqireWater;
    }
    
    public boolean requiresLava() {
    	return requireLava;
    }
    
    public int requiredFluidLevel() {
    	return minFluidLevel;
    }
    
    public boolean requiresCampfire() {
    	return requireCampfire;
    }
    
    public boolean requiresSoulCampfire() {
    	return requireSoulCampfire;
    }
    
    public boolean requiresLitCampfire() {
    	return requireLitCampfire;
    }
    
    public static HashMap<NamespacedKey, CauldronRecipe> getRecipes() {
		return RECIPES;
    }
	
	/**
     * Checks an ItemStack to be used in constructors related to CraftingRecipe
     * is not empty.
     *
     * @param result an ItemStack
     * @return the same result ItemStack
     * @throws IllegalArgumentException if the {@code result} is an empty item
     * (AIR)
     */
    @NotNull
    protected static ItemStack checkResult(@NotNull ItemStack result) {
        Preconditions.checkArgument(!result.isEmpty(), "Recipe cannot have an empty result."); // Paper
        return result;
    }

}
