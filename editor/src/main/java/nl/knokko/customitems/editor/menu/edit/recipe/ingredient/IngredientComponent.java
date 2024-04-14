package nl.knokko.customitems.editor.menu.edit.recipe.ingredient;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ChooseDataVanillaResult;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ingredient.*;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class IngredientComponent extends DynamicTextButton {

	private final ShapedRecipeValues recipe;
	private final int x, y;
	private final GuiComponent menu;
	private final String emptyText;
	private final ItemSet set;

	public IngredientComponent(
			ShapedRecipeValues recipe, int x, int y,
			String emptyText, GuiComponent menu, ItemSet set) {
		super(recipe.getIngredientAt(x, y).toString(emptyText), EditProps.BUTTON, EditProps.HOVER, null);
		this.clickAction = () -> {
			state.getWindow().setMainComponent(new EditIngredient(
					menu, this::setIngredient, recipe.getIngredientAt(x, y), true, set
			));
		};
		this.recipe = recipe;
		this.x = x;
		this.y = y;
		this.emptyText = emptyText;
		this.menu = menu;
		this.set = set;
	}

	public void setIngredient(IngredientValues newIngredient) {
		recipe.setIngredientAt(x, y, newIngredient);
		setText(newIngredient.toString(emptyText));
	}

	@Override
	public void keyPressed(char character) {
		if (state.isMouseOver()) {
			if (character == 'v') {
				state.getWindow().setMainComponent(new EnumSelect<>(CIMaterial.class, vanillaMaterial -> {
					IngredientComponent.this.setIngredient(SimpleVanillaIngredientValues.createQuick(vanillaMaterial, 1));
				}, candidateMaterial -> true, menu));
			} else if (character == 'c') {
				state.getWindow().setMainComponent(new CollectionSelect<>(set.items.references(), customItem -> {
					IngredientComponent.this.setIngredient(CustomItemIngredientValues.createQuick(customItem, 1));
				}, candidateItem -> true, itemRef -> itemRef.get().getName(), menu, false));
			} else if (character == 'd') {
				state.getWindow().setMainComponent(new ChooseDataVanillaResult(menu, true, dataResult -> {
					IngredientComponent.this.setIngredient(DataVanillaIngredientValues.createQuick(
							dataResult.getMaterial(), dataResult.getDataValue(), dataResult.getAmount()
					));
				}));
			} else if (character == 'e') {
				IngredientComponent.this.setIngredient(new NoIngredientValues());
			}
		}
	}
}