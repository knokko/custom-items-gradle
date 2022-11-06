package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import org.bukkit.inventory.ItemStack;

import nl.knokko.customitems.item.CIMaterial;

import java.util.ArrayList;
import java.util.List;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.convertResultToItemStack;
import static nl.knokko.customitems.plugin.recipe.RecipeHelper.shouldIngredientAcceptItemStack;

public class ShapelessCraftingRecipeWrapper implements CraftingRecipeWrapper {

	private final ShapelessRecipeValues recipe;

	public ShapelessCraftingRecipeWrapper(ShapelessRecipeValues recipe) {
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
	public ItemStack getResult() {
		return convertResultToItemStack(this.recipe.getResult());
	}

	@Override
	public List<IngredientEntry> shouldAccept(ItemStack[] ingredients) {

		List<IngredientValues> recipeIngredients = new ArrayList<>(this.recipe.getIngredients());
		boolean[] has = new boolean[recipeIngredients.size()];
		List<IngredientEntry> result = new ArrayList<>(recipeIngredients.size());

		outerLoop:
		for (int ingredientIndex = 0; ingredientIndex < ingredients.length; ingredientIndex++) {
		    ItemStack ingredient = ingredients[ingredientIndex];
			if (!KciNms.instance.items.getMaterialName(ingredient).equals(CIMaterial.AIR.name())) {
				for (int index = 0; index < has.length; index++) {
					if (!has[index] && shouldIngredientAcceptItemStack(recipeIngredients.get(index), ingredient)) {
						has[index] = true;
						result.add(new IngredientEntry(recipeIngredients.get(index), ingredientIndex));
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
