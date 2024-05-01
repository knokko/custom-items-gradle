package nl.knokko.customitems.editor.menu.edit.recipe;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.recipe.template.ChooseTemplateRecipeType;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.CraftingRecipeReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.KciCraftingRecipe;
import nl.knokko.customitems.recipe.KciShapedRecipe;
import nl.knokko.customitems.recipe.KciShapelessRecipe;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;

public class RecipeCollectionEdit extends DedicatedCollectionEdit<KciCraftingRecipe, CraftingRecipeReference> {

	private final ItemSet itemSet;

	public RecipeCollectionEdit(ItemSet itemSet, GuiComponent returnMenu) {
		super(returnMenu, itemSet.craftingRecipes.references(), null);
		this.itemSet = itemSet;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create template recipe", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseTemplateRecipeType(this, itemSet));
		}), 0.025f, 0.38f, 0.29f, 0.48f);
		addComponent(new DynamicTextButton("Create shaped recipe", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ShapedRecipeEdit(itemSet, this, new KciShapedRecipe(true), null));
		}), 0.025f, 0.26f, 0.27f, 0.36f);
		addComponent(new DynamicTextButton("Create shapeless recipe", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ShapelessRecipeEdit(itemSet, this, new KciShapelessRecipe(true), null));
		}), 0.025f, 0.14f, 0.29f, 0.24f);

		HelpButtons.addHelpLink(this, "edit menu/recipes/overview.html");
	}

	@Override
	protected String getModelLabel(KciCraftingRecipe model) {
		return model.getResult().toString();
	}

	@Override
	protected BufferedImage getModelIcon(KciCraftingRecipe model) {
		if (model.getResult() instanceof CustomItemResult) {
			return ((CustomItemResult) model.getResult()).getItem().getTexture().getImage();
		} else {
			return null;
		}
	}

	@Override
	protected boolean canEditModel(KciCraftingRecipe model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(CraftingRecipeReference modelReference) {
		return createEditMenu(modelReference, false);
	}

	private GuiComponent createEditMenu(CraftingRecipeReference modelReference, boolean copy) {
		KciCraftingRecipe oldRecipe = modelReference.get();
		CraftingRecipeReference toModify = copy ? null : modelReference;
		if (oldRecipe instanceof KciShapedRecipe) {
			return new ShapedRecipeEdit(itemSet, this, (KciShapedRecipe) oldRecipe, toModify);
		} else if (oldRecipe instanceof KciShapelessRecipe) {
			return new ShapelessRecipeEdit(itemSet, this, (KciShapelessRecipe) oldRecipe, toModify);
		} else {
			throw new Error("Unknown recipe class: " + oldRecipe.getClass());
		}
	}

	@Override
	protected String deleteModel(CraftingRecipeReference modelReference) {
		return Validation.toErrorString(() -> itemSet.craftingRecipes.remove(modelReference));
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(CraftingRecipeReference modelReference) {
		return CopyMode.SEPARATE_MENU;
	}

	@Override
	protected GuiComponent createCopyMenu(CraftingRecipeReference modelReference) {
		return createEditMenu(modelReference, true);
	}
}
