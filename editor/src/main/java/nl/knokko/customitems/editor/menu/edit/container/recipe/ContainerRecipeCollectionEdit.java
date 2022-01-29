package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.function.Consumer;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.StringLength;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ContainerRecipeCollectionEdit extends SelfDedicatedCollectionEdit<ContainerRecipeValues> {
	
	private final ItemSet itemSet;
	private final CustomContainerValues container;

	public ContainerRecipeCollectionEdit(
            ItemSet itemSet, CustomContainerValues container, GuiComponent returnMenu
	) {
		super(container.getRecipes(), container::setRecipes, returnMenu);
		this.itemSet = itemSet;
		this.container = container;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add recipe", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditContainerRecipe(
					itemSet, container, this,
					new ContainerRecipeValues(true), this::addModel
			));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/recipes/overview.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected String getModelLabel(ContainerRecipeValues model) {
		StringBuilder result = new StringBuilder();
		result.append('(');
		for (Map.Entry<String, OutputTableValues> output : model.getOutputs().entrySet()) {
			result.append(output.getValue());
			result.append(',');
		}
		result.append(')');

		// Don't make it too long; that will get unreadable
		int maxLength = 30;
		return StringLength.fixLength(result.toString(), 30);
	}

	@Override
	protected BufferedImage getModelIcon(ContainerRecipeValues model) {

		// If we find an output with a custom item, take it!
		for (Map.Entry<String, OutputTableValues> output : model.getOutputs().entrySet()) {
			OutputTableValues currentTable = output.getValue();
			for (OutputTableValues.Entry entry : currentTable.getEntries()) {
				if (entry.getResult() instanceof CustomItemResultValues) {
					CustomItemResultValues customResult = (CustomItemResultValues) entry.getResult();
					return customResult.getItem().getTexture().getImage();
				}
			}
		}

		// Otherwise, we don't have an icon ;(
		return null;
	}

	@Override
	protected boolean canEditModel(ContainerRecipeValues model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(ContainerRecipeValues oldModelValues, Consumer<ContainerRecipeValues> changeModelValues) {
		return new EditContainerRecipe(
				itemSet, container, this, oldModelValues, changeModelValues
		);
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(ContainerRecipeValues model) {
		return CopyMode.SEPARATE_MENU;
	}
}
