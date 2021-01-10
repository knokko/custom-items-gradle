package nl.knokko.customitems.editor.menu.edit.recipe;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.CollectionEdit;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.template.ChooseTemplateRecipeType;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.recipe.Recipe;
import nl.knokko.customitems.editor.set.recipe.ShapedRecipe;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.editor.set.recipe.result.CustomItemResult;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class RecipeCollectionEdit extends CollectionEdit<Recipe> {
	
	private final EditMenu menu;

	public RecipeCollectionEdit(EditMenu menu) {
		super(new RecipeActionHandler(menu), menu.getSet().getBackingRecipes());
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create template recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseTemplateRecipeType(this, menu.getSet()));
		}), 0.025f, 0.35f, 0.29f, 0.45f);
		addComponent(new DynamicTextButton("Create shaped recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ShapedRecipeEdit(menu, null, null));
		}), 0.025f, 0.2f, 0.27f, 0.3f);
		addComponent(new DynamicTextButton("Create shapeless recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ShapelessRecipeCollectionEdit(menu, null, null));
		}), 0.025f, 0.05f, 0.29f, 0.15f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/recipes/overview.html");
	}
	
	private static class RecipeActionHandler implements ActionHandler<Recipe> {
		
		private final EditMenu menu;
		
		private RecipeActionHandler(EditMenu menu) {
			this.menu = menu;
		}

		@Override
		public void goBack() {
			menu.getState().getWindow().setMainComponent(menu);
		}

		@Override
		public BufferedImage getImage(Recipe recipe) {
			if (recipe.getResult() instanceof CustomItemResult) {
				CustomItem item = ((CustomItemResult)recipe.getResult()).getItem();
				return item.getTexture().getImage();
			} else {
				
				// It is allowed to return null
				return null;
			}
		}

		@Override
		public String getLabel(Recipe item) {
			return item.getResult().getString();
		}
		
		private GuiComponent createEditMenu(Recipe recipe, boolean copy) {
			Recipe second = copy ? null : recipe;
			if (recipe instanceof ShapedRecipe)
				return new ShapedRecipeEdit(menu, (ShapedRecipe) recipe, (ShapedRecipe) second);
			else if (recipe instanceof ShapelessRecipe)
				return new ShapelessRecipeCollectionEdit(menu, (ShapelessRecipe) recipe, (ShapelessRecipe) second);
			else
				throw new IllegalStateException("Unknown recipe class: " + recipe.getClass());
		}

		@Override
		public GuiComponent createEditMenu(Recipe recipe, GuiComponent returnMenu) {
			return createEditMenu(recipe, false);
		}
		
		@Override
		public GuiComponent createCopyMenu(Recipe recipe, GuiComponent returnMenu) {
			return createEditMenu(recipe, true);
		}

		@Override
		public String deleteItem(Recipe itemToDelete) {
			menu.getSet().removeRecipe(itemToDelete);
			
			// Well... there isn't really something that can go wrong when deleting recipes
			return null;
		}
	}
}
