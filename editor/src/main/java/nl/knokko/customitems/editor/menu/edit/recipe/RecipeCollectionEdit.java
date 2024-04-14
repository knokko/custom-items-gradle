package nl.knokko.customitems.editor.menu.edit.recipe;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.recipe.template.ChooseTemplateRecipeType;
import nl.knokko.customitems.editor.menu.edit.upgrade.UpgradeCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.CraftingRecipeReference;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class RecipeCollectionEdit extends DedicatedCollectionEdit<CraftingRecipeValues, CraftingRecipeReference> {
	
	private final EditMenu menu;

	public RecipeCollectionEdit(EditMenu menu) {
		super(menu, menu.getSet().craftingRecipes.references(), null);
		this.menu = menu;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Create template recipe", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseTemplateRecipeType(this, menu.getSet()));
		}), 0.025f, 0.38f, 0.29f, 0.48f);
		addComponent(new DynamicTextButton("Create shaped recipe", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ShapedRecipeEdit(menu, new ShapedRecipeValues(true), null));
		}), 0.025f, 0.26f, 0.27f, 0.36f);
		addComponent(new DynamicTextButton("Create shapeless recipe", BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ShapelessRecipeEdit(new ShapelessRecipeValues(true), null, menu.getSet(), this));
		}), 0.025f, 0.14f, 0.29f, 0.24f);
		addComponent(new DynamicTextButton("Upgrades...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new UpgradeCollectionEdit(this, menu.getSet()));
		}), 0.025f, 0.02f, 0.15f, 0.12f);

		HelpButtons.addHelpLink(this, "edit menu/recipes/overview.html");
	}

	@Override
	protected String getModelLabel(CraftingRecipeValues model) {
		return model.getResult().toString();
	}

	@Override
	protected BufferedImage getModelIcon(CraftingRecipeValues model) {
		if (model.getResult() instanceof CustomItemResultValues) {
			return ((CustomItemResultValues) model.getResult()).getItem().getTexture().getImage();
		} else {
			return null;
		}
	}

	@Override
	protected boolean canEditModel(CraftingRecipeValues model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(CraftingRecipeReference modelReference) {
		return createEditMenu(modelReference, false);
	}

	private GuiComponent createEditMenu(CraftingRecipeReference modelReference, boolean copy) {
		CraftingRecipeValues oldRecipe = modelReference.get();
		CraftingRecipeReference toModify = copy ? null : modelReference;
		if (oldRecipe instanceof ShapedRecipeValues) {
			return new ShapedRecipeEdit(menu, (ShapedRecipeValues) oldRecipe, toModify);
		} else if (oldRecipe instanceof ShapelessRecipeValues) {
			return new ShapelessRecipeEdit((ShapelessRecipeValues) oldRecipe, toModify, menu.getSet(), this);
		} else {
			throw new Error("Unknown recipe class: " + oldRecipe.getClass());
		}
	}

	@Override
	protected String deleteModel(CraftingRecipeReference modelReference) {
		return Validation.toErrorString(() -> menu.getSet().craftingRecipes.remove(modelReference));
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
