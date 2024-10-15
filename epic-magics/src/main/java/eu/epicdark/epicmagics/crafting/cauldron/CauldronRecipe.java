package eu.epicdark.epicmagics.crafting.cauldron;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import eu.epicdark.epicmagics.EpicMagics;
import eu.epicdark.epicmagics.utils.CauldronData;

public class CauldronRecipe implements Recipe, Keyed{
	private static final Set<CauldronRecipe> RECIPES = new HashSet<>();
	private static BukkitTask TASK;
	
	private final NamespacedKey key;
    private final ItemStack output;
    private final boolean reqiresWater;
	
    protected CauldronRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result) {
		this(key, result, false);
	}
    
	protected CauldronRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result, @NotNull boolean requiresWater) {
		Preconditions.checkArgument(key != null, "key cannot be null");
		this.key = key;
		this.output = new ItemStack(result);
		this.reqiresWater = requiresWater;
		
		Block block = Bukkit.getWorlds().get(0).getBlockAt(252, 71, 633);
		CauldronData data = new CauldronData(block);
		EpicMagics.INSTANCE.getLogger().info("level: " + data.getLevel());
		
		RECIPES.add(this);
		
		if(TASK == null) {
			EpicMagics.INSTANCE.getLogger().info("Starting cauldron task...");
			TASK = new BukkitRunnable() {
				
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
						items.forEach(item -> {
							if(item == null) {
								EpicMagics.INSTANCE.getLogger().info("Removing item from list");
								items.remove(item);
							}
						});
						CauldronData.getItemMap().put(block, items);
						
						
						for(ShapelessCauldronRecipe recipe : ShapelessCauldronRecipe.getShapelessRecipes()) {
							
							List<Item> craftingItems = new ArrayList<>();
							boolean selected = true;
							for(RecipeChoice choice : recipe.getIngredients()) {
								boolean accept = true;
								for(Item item : items) {
									if(!choice.test(item.getItemStack())) {
										accept = false;
									}else {
										craftingItems.add(item);
										continue;
									}
								}
								
								if(!accept) {
									selected = false;
								}
							}
							
							if(selected) {
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
    	return reqiresWater;
    }
    
    public static Set<CauldronRecipe> getRecipes() {
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
    @ApiStatus.Internal
    @NotNull
    protected static ItemStack checkResult(@NotNull ItemStack result) {
        Preconditions.checkArgument(!result.isEmpty(), "Recipe cannot have an empty result."); // Paper
        return result;
    }

}
