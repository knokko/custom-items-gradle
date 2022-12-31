package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.item.CIMaterial;

import java.util.ArrayList;
import java.util.List;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.shouldIngredientAcceptItemStack;

public class ShapelessCraftingRecipeWrapper extends CraftingRecipeWrapper {

	private final ShapelessRecipeValues recipe;

	public ShapelessCraftingRecipeWrapper(ShapelessRecipeValues recipe) {
		super(recipe);
		this.recipe = recipe;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ShapelessCraftingRecipeWrapper) {
			return this.recipe == ((ShapelessCraftingRecipeWrapper) other).recipe;
		} else {
			return false;
		}
	}

	@Override
	public List<IngredientEntry> shouldAccept(ItemStack[] ingredients) {

		List<IngredientValues> recipeIngredients = new ArrayList<>(this.recipe.getIngredients());
		boolean[] has = new boolean[recipeIngredients.size()];
		List<IngredientEntry> result = new ArrayList<>(recipeIngredients.size());

		outerLoop:
		for (int itemIndex = 0; itemIndex < ingredients.length; itemIndex++) {
		    ItemStack ingredient = ingredients[itemIndex];
			if (!KciNms.instance.items.getMaterialName(ingredient).equals(CIMaterial.AIR.name())) {
				for (int ingredientIndex = 0; ingredientIndex < has.length; ingredientIndex++) {
					if (!has[ingredientIndex]
							&& shouldIngredientAcceptItemStack(recipeIngredients.get(ingredientIndex), ingredient)
					) {
						has[ingredientIndex] = true;
						result.add(new IngredientEntry(
								recipeIngredients.get(ingredientIndex),
								ingredientIndex,
								itemIndex
						));
						continue outerLoop;
					}
				}
				// When we reach this code, we don't need that ingredient
				return null;
			}
		}
		
		// Now see if we have all necessary ingredients
		for (boolean b : has)
			if (!b)
				return null;
		
		// We have exactly what we need
		return result;
	}
}
