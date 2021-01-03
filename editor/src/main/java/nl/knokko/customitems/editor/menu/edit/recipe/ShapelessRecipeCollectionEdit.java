package nl.knokko.customitems.editor.menu.edit.recipe;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.SafeCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.ChooseIngredient;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ResultComponent;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectCustomItem;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectDataVanillaItem;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectSimpleVanillaItem;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.editor.set.recipe.ShapelessRecipe;
import nl.knokko.customitems.editor.set.recipe.ingredient.CustomItemIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.DataVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.Ingredient;
import nl.knokko.customitems.editor.set.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.editor.set.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class ShapelessRecipeCollectionEdit extends SafeCollectionEdit<Ingredient> {
	
	private static Collection<Ingredient> toCollection(Ingredient[] array) {
		Collection<Ingredient> collection = new ArrayList<>(array.length);
		for (Ingredient ingredient : array)
			collection.add(ingredient);
		return collection;
	}
	
	private final EditMenu menu;
	
	private final ShapelessRecipe toEdit;
	private final ResultComponent result;

	public ShapelessRecipeCollectionEdit(EditMenu menu, ShapelessRecipe oldValues, ShapelessRecipe toEdit) {
		super(menu.getRecipeOverview(), oldValues == null ? new LinkedList<>() : toCollection(oldValues.getIngredients()));
		this.menu = menu;
		this.toEdit = toEdit;
		if (oldValues == null)
			result = new ResultComponent(new SimpleVanillaResult(CIMaterial.DIAMOND, (byte) 1), this, menu.getSet());
		else
			result = new ResultComponent(oldValues.getResult(), this, menu.getSet());
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new ConditionalTextButton("Add ingredient", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseIngredient(this, (Ingredient ingredient) -> {
				currentCollection.add(ingredient);
				itemList.refresh();
			}, false, menu.getSet()));
		}, () -> {
			return currentCollection.size() < 9;
		}), 0.025f, 0.55f, 0.175f, 0.65f);
		addComponent(new DynamicTextComponent("Result", EditProps.LABEL), 0.025f, 0.4f, 0.175f, 0.5f);
		addComponent(result, 0.025f, 0.3f, 0.175f, 0.4f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/recipes/shapeless.html");
	}

	@Override
	protected String getItemLabel(Ingredient item) {
		return item.toString(null);
	}

	@Override
	protected BufferedImage getItemIcon(Ingredient item) {
		if (item instanceof CustomItemIngredient)
			return ((CustomItemIngredient) item).getItem().getTexture().getImage();
		else
			return null;
	}

	@Override
	protected EditMode getEditMode(Ingredient item) {
		return EditMode.DISABLED;
	}

	@Override
	protected GuiComponent createEditMenu(Ingredient itemToEdit) {
		return null;
	}

	@Override
	protected String deleteItem(Ingredient itemToDelete) {
		// Ingredients can always be deleted
		return null;
	}

	@Override
	protected CopyMode getCopyMode(Ingredient item) {
		return currentCollection.size() < 9 ? CopyMode.INSTANT : CopyMode.DISABLED;
	}

	@Override
	protected Ingredient copy(Ingredient item) {
		// Ingredients are immutable, so no need for an explicit copy
		return item;
	}

	@Override
	protected GuiComponent createCopyMenu(Ingredient itemToCopy) {
		return null;
	}

	@Override
	protected void onApply() {
		Ingredient[] ingredients = new Ingredient[currentCollection.size()];
		int index = 0;
		for (Ingredient ingredient : currentCollection)
			ingredients[index++] = ingredient;
		
		String error;
		if (toEdit == null)
			error = menu.getSet().addShapelessRecipe(ingredients, result.getResult());
		else
			error = menu.getSet().changeShapelessRecipe(toEdit, ingredients, result.getResult());
		
		if (error != null)
			errorComponent.setText(error);
		else
			state.getWindow().setMainComponent(menu.getRecipeOverview());
	}

	@Override
	protected boolean isCreatingNew() {
		return toEdit == null;
	}
	
	@Override
	public void keyPressed(char character) {
		if (character == 'v') {
			state.getWindow().setMainComponent(new SelectSimpleVanillaItem(this, (CIMaterial material) -> {
				currentCollection.add(new SimpleVanillaIngredient(material));
			},false));
		} else if (character == 'c') {
			state.getWindow().setMainComponent(new SelectCustomItem(this, (CustomItem item) -> {
				currentCollection.add(new CustomItemIngredient(item));
			}, menu.getSet()));
		} else if (character == 'd') {
			state.getWindow().setMainComponent(new SelectDataVanillaItem(this, (CIMaterial material, byte data) -> {
				currentCollection.add(new DataVanillaIngredient(material, data));
			}));
		}
	}
}
