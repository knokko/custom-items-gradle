package nl.knokko.customitems.plugin.recipe;

import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static nl.knokko.customitems.plugin.recipe.RecipeHelper.shouldIngredientAcceptItemStack;

public class ShapedCraftingRecipeWrapper extends CraftingRecipeWrapper {

	private final ShapedRecipeValues recipe;
	private final int recipeWidth, recipeHeight;
	private final int recipeMinX, recipeMinY;

    public ShapedCraftingRecipeWrapper(ShapedRecipeValues recipe){
    	super(recipe);
    	this.recipe = recipe;
		this.recipeMinX = recipe.getEffectiveMinX();
		this.recipeMinY = recipe.getEffectiveMinY();
		this.recipeWidth = recipe.getEffectiveWidth();
		this.recipeHeight = recipe.getEffectiveHeight();
    }

    @Override
	public boolean equals(Object other) {
    	if (other instanceof ShapedCraftingRecipeWrapper) {
    		return this.recipe == ((ShapedCraftingRecipeWrapper) other).recipe;
		} else {
    		return false;
		}
	}

	private IngredientValues getIngredientPossiblyOutOfBounds(int x, int y) {
		if (x < 0 || x >= 3 || y < 0 || y >= 3) return new NoIngredientValues();
		return this.recipe.getIngredientAt(x, y);
	}

	private List<IngredientEntry> shouldAccept(
			ItemStack[] ingredients, int offsetX, int offsetY,
			int inventoryGridWidth, int inventoryGridHeight
	) {
		List<IngredientEntry> result = new ArrayList<>(9);
		for (int itemX = 0; itemX < 3; itemX++) {
			for (int itemY = 0; itemY < 3; itemY++) {

				int itemIndex = itemX + inventoryGridWidth * itemY;
				ItemStack actualIngredient = itemX < inventoryGridWidth && itemY < inventoryGridHeight ? ingredients[itemIndex] : null;

				int ingredientX = itemX + (recipe.shouldIgnoreDisplacement() ? recipeMinX : 0) - offsetX;
				int ingredientY = itemY + (recipe.shouldIgnoreDisplacement() ? recipeMinY : 0) - offsetY;
				IngredientValues expectedIngredient = getIngredientPossiblyOutOfBounds(ingredientX, ingredientY);

				if (shouldIngredientAcceptItemStack(expectedIngredient, actualIngredient)) {
					if (!(expectedIngredient instanceof NoIngredientValues)) {
						result.add(new IngredientEntry(
								expectedIngredient, ingredientX + 3 * ingredientY, itemIndex
						));
					}
				} else {
					return null;
				}
			}
		}

		return result;
	}

	private List<IngredientEntry> shouldAccept(ItemStack[] ingredients, int inventoryGridWidth, int inventoryGridHeight) {
		if (ingredients.length == inventoryGridWidth * inventoryGridHeight) {
			int maxOffsetX = recipe.shouldIgnoreDisplacement() ? inventoryGridWidth - recipeWidth : 0;
			int maxOffsetY = recipe.shouldIgnoreDisplacement() ? inventoryGridHeight - recipeHeight : 0;
			for (int offsetX = 0; offsetX <= maxOffsetX; offsetX++) {
				for (int offsetY = 0; offsetY <= maxOffsetY; offsetY++) {
					List<IngredientEntry> possibleMatch = this.shouldAccept(
							ingredients, offsetX, offsetY, inventoryGridWidth, inventoryGridHeight
					);
					if (possibleMatch != null) return possibleMatch;
				}
			}
		}
		return null;
	}

	@Override
	public List<IngredientEntry> shouldAccept(ItemStack[] ingredients) {

		List<IngredientEntry> possibleMatch = this.shouldAccept(ingredients, 3, 3);
		if (possibleMatch != null) return possibleMatch;

		return this.shouldAccept(ingredients, 2, 2);
	}
}
