package eu.epicdark.epicmagics.crafting.cauldron;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import eu.epicdark.epicmagics.EpicMagics;
import eu.epicdark.epicmagics.utils.CauldronData;
import eu.epicdark.epicmagics.utils.EpicUtils;

public class CauldronRecipe implements Recipe, Keyed{
	private static final Set<CauldronRecipe> RECIPES = new HashSet<>();
	private static BukkitTask TASK;
	
	private final NamespacedKey key;
    protected final ItemStack output;
    public final boolean requiresWater;
    public final boolean requiresLava;
    public final int minFluidLevel;
    
    public final boolean requiresCampfire;
    public final boolean requiresSoulCampfire;
    public final boolean requiresLitCampfire;
	
    protected CauldronRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result) {
		this(key, result, false, false, 0, false, false, false);
	}
    
	protected CauldronRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result, @NotNull boolean requiresWater, @NotNull boolean requiresLava, @NotNull int minFluidLevel, boolean requiresCampfire, boolean requiresSoulCampfire, boolean requiresLitCampfire) {
		Preconditions.checkArgument(key != null, "key cannot be null");
		this.key = key;
		this.output = new ItemStack(result);
		this.requiresWater = requiresWater;
		this.requiresLava = requiresLava;
		this.minFluidLevel = minFluidLevel;
		
		this.requiresCampfire = requiresCampfire;
		this.requiresSoulCampfire = requiresSoulCampfire;
		this.requiresLitCampfire = requiresLitCampfire;
		
		Block block = Bukkit.getWorlds().get(0).getBlockAt(252, 71, 633);
		CauldronData data = new CauldronData(block);
		EpicMagics.INSTANCE.getLogger().info("level: " + data.getLevel());
		
		RECIPES.add(this);
		
		if(TASK == null) {
			EpicMagics.info("Starting cauldron task...");
			TASK = new BukkitRunnable() {
				
				@SuppressWarnings({ "unchecked", "deprecation" })
				@Override
				public void run() {
					
					if(CauldronData.getItemMap().isEmpty()) {
						return;
					}
					for(Block block : CauldronData.getItemMap().keySet()) {
						ArrayList<Item> items = (ArrayList<Item>) CauldronData.getItemMap().get(block).clone();
						
						//remove despawned/picked up items
						for(Item item : CauldronData.getItemMap().get(block)) {
							if(item == null || Bukkit.getEntity(item.getUniqueId()) == null) {
								items.remove(item);
							}
						}
						CauldronData.getItemMap().put(block, items);
						
						//remove block from cache map if no items are there anymore
						if(items.isEmpty()) {
							CauldronData.getItemMap().remove(block);
							continue;
						}
						
						CauldronData data = new CauldronData(block);
						
						EpicMagics.info(" ");
						EpicMagics.info("Looping through block " + block.getX() + " " + block.getY() + " " + block.getZ());
						for(ShapelessCauldronRecipe recipe : ShapelessCauldronRecipe.getShapelessRecipes()) {
							EpicMagics.info("Checking for recipe " + recipe.getKey().asString());
							EpicMagics.info("Found ingredients " + recipe.getIngredients().toString());
							
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
								EpicMagics.info("Checking for recipe ingredient " + choice.toString());
								
								for(Item item : craftingItems) {
									ItemStack stack = item.getItemStack();
									
									EpicMagics.info("Expected " + choice.getItemStack().getType());
									EpicMagics.info("Got " + stack.getType());
									if(choice.test(stack)) {
										EpicMagics.info("Cauldron has item " + stack.getType());
										choices.remove(choice);
									}
								}
								
								EpicMagics.info("Remaining items: " + craftingItems.stream().map(s -> s.getItemStack().getType().name()).collect(Collectors.toList()).toString());

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
			}.runTaskTimer(EpicMagics.INSTANCE, 0, 20);
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
    	return requiresWater;
    }
    
    
    @Override
    public String toString() {
    	return "CauldronRecipe[key=" + this.getKey().asString() + ", result=" + this.output.toString() + ", water=" + requiresWater + ", lava=" + requiresLava + "]";
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
