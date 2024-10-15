package eu.epicdark.epicmagics.crafting.cauldron;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

public class ShapelessCauldronRecipe extends CauldronRecipe{
	private static final Set<ShapelessCauldronRecipe> RECIPES = new HashSet<>();
	
	private final Set<RecipeChoice> ingredients = new HashSet<>();
	
	public ShapelessCauldronRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result) {
		super(key, checkResult(result));
		
		RECIPES.add(this);
	}
	
	public ShapelessCauldronRecipe addIngredient(RecipeChoice choice) {
		ingredients.add(choice);
		return this;
	}
	
	public Set<RecipeChoice> getIngredients() {
		return ingredients;
	}
	
	
	
	
	public static Set<ShapelessCauldronRecipe> getShapelessRecipes() {
		return RECIPES;
	}
	
	public static boolean isInRecipe(ItemStack item) {
		for(ShapelessCauldronRecipe recipe : ShapelessCauldronRecipe.getShapelessRecipes()) {
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
