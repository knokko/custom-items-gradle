package nl.knokko.customitems.editor.menu.edit.recipe;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.IngredientComponent;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ResultComponent;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.CraftingRecipeReference;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ShapedRecipeEdit extends GuiMenu {
	
	private final EditMenu menu;
	private final ShapedRecipeValues currentValues;
	private final CraftingRecipeReference toModify;

	private final Ingredients ingredientsComponent;
	private final ResultComponent resultComponent;
	private final DynamicTextComponent errorComponent;

	private EagerTextEditField permissionField;

	public ShapedRecipeEdit(EditMenu menu, ShapedRecipeValues oldValues, CraftingRecipeReference toModify) {
		this.menu = menu;
		this.currentValues = oldValues.copy(true);
		this.toModify = toModify;

		this.ingredientsComponent = new Ingredients();
		this.resultComponent = new ResultComponent(
				currentValues.getResult(), currentValues::setResult, this, menu.getSet()
		);
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
		permissionField.loseFocus();
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(menu.getRecipeOverview());
		}), 0.1f, 0.85f, 0.25f, 0.95f);
		addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error;
			if (toModify == null) error = Validation.toErrorString(() -> menu.getSet().addRecipe(currentValues));
			else error = Validation.toErrorString(() -> menu.getSet().changeRecipe(toModify, currentValues));

			if (error != null) errorComponent.setText(error);
			else state.getWindow().setMainComponent(menu.getRecipeOverview());
		}), 0.1f, 0.7f, 0.25f, 0.8f);
		addComponent(ingredientsComponent, 0.05f, 0.1f, 0.65f, 0.6f);
		addComponent(
				new DynamicTextComponent("Required permission:", LABEL),
				0.1f, 0f, 0.3f, 0.1f
		);
		String initialRequiredPermission = currentValues.getRequiredPermission() == null ? "" : currentValues.getRequiredPermission();
		this.permissionField = new EagerTextEditField(initialRequiredPermission, EDIT_BASE, EDIT_ACTIVE, currentValues::setRequiredPermission);
		addComponent(permissionField, 0.325f, 0.01f, 0.6f, 0.09f);
		addComponent(errorComponent, 0.35f, 0.85f, 0.95f, 0.95f);
		addComponent(resultComponent, 0.75f, 0.275f, 0.95f, 0.425f);
		
		HelpButtons.addHelpLink(this, "edit menu/recipes/shaped.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private class Ingredients extends GuiMenu {
		
		private final IngredientComponent[] ingredients;
		
		private Ingredients() {
			ingredients = new IngredientComponent[9];
			for (int index = 0; index < ingredients.length; index++) {
				ingredients[index] = new IngredientComponent(
						currentValues, index % 3, index / 3, "empty", ShapedRecipeEdit.this, menu.getSet()
				);
			}
		}

		@Override
		protected void addComponents() {
			addComponent(ingredients[0], 0.025f, 0.675f, 0.325f, 0.975f);
			addComponent(ingredients[1], 0.350f, 0.675f, 0.650f, 0.975f);
			addComponent(ingredients[2], 0.675f, 0.675f, 0.975f, 0.975f);
			addComponent(ingredients[3], 0.025f, 0.350f, 0.325f, 0.650f);
			addComponent(ingredients[4], 0.350f, 0.350f, 0.650f, 0.650f);
			addComponent(ingredients[5], 0.675f, 0.350f, 0.975f, 0.650f);
			addComponent(ingredients[6], 0.025f, 0.025f, 0.325f, 0.325f);
			addComponent(ingredients[7], 0.350f, 0.025f, 0.650f, 0.325f);
			addComponent(ingredients[8], 0.675f, 0.025f, 0.975f, 0.325f);
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND2;
		}
	}
}