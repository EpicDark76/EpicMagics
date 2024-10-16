package eu.epicdark.epicmagics.crafting.cauldron;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

public class ShapedCauldronRecipe extends CauldronRecipe{
	private static final Set<ShapedCauldronRecipe> SHAPED_RECIPES = new HashSet<>();
	
	private final RecipeChoice[] ingredients;
	
	public ShapedCauldronRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result, RecipeChoice...choices) {
		super(key, result);
		this.ingredients = choices;
		
		SHAPED_RECIPES.add(this);
	}
	
	public RecipeChoice[] getIngredients() {
		return ingredients;
	}
	
	
	
	
	
	
	public static Set<ShapedCauldronRecipe> getShapedRecipes() {
		return SHAPED_RECIPES;
	}
	
	public static boolean isInRecipe(ItemStack item) {
		for(ShapedCauldronRecipe recipe : SHAPED_RECIPES) {
			for(RecipeChoice ingredient : recipe.getIngredients()) {
				if(ingredient.test(item)) {
					return true;
				}
			}
		}
		return false;
	}

}
