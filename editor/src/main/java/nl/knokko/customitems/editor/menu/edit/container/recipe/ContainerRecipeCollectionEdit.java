package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.function.Consumer;

import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.StringLength;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.result.CustomItemResult;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class ContainerRecipeCollectionEdit extends SelfDedicatedCollectionEdit<ContainerRecipe> {
	
	private final ItemSet itemSet;
	private final KciContainer container;

	public ContainerRecipeCollectionEdit(
			ItemSet itemSet, KciContainer container, GuiComponent returnMenu
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
					new ContainerRecipe(true), this::addModel
			));
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/recipes/overview.html");
	}

    @Override
	protected String getModelLabel(ContainerRecipe model) {
		int modelIndex = -1;
		for (int index = 0; index < liveCollection.size(); index++) {
			if (model == liveCollection.get(index)) {
				modelIndex = index;
				break;
			}
		}

		StringBuilder result = new StringBuilder();
		result.append(modelIndex + 1);
		result.append(": ");

		int entryCounter = 0;
		for (Map.Entry<String, OutputTable> output : model.getOutputs().entrySet()) {
			result.append(output.getValue());
			if (entryCounter != model.getOutputs().size() - 1) result.append(',');
			entryCounter += 1;
		}
		if (model.getManualOutput() != null) {
			result.append(model.getManualOutput());
		}

		// Don't make it too long; that will get unreadable
		int maxLength = 40;
		return StringLength.fixLength(result.toString(), maxLength);
	}

	@Override
	protected BufferedImage getModelIcon(ContainerRecipe model) {

		// If we find an output with a custom item, take it!
		for (Map.Entry<String, OutputTable> output : model.getOutputs().entrySet()) {
			OutputTable currentTable = output.getValue();
			for (OutputTable.Entry entry : currentTable.getEntries()) {
				if (entry.getResult() instanceof CustomItemResult) {
					CustomItemResult customResult = (CustomItemResult) entry.getResult();
					return customResult.getItem().getTexture().getImage();
				}
			}
		}

		// Otherwise, we don't have an icon ;(
		return null;
	}

	@Override
	protected boolean canEditModel(ContainerRecipe model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(ContainerRecipe oldModelValues, Consumer<ContainerRecipe> changeModelValues) {
		return new EditContainerRecipe(
				itemSet, container, this, oldModelValues, changeModelValues
		);
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(ContainerRecipe model) {
		return CopyMode.SEPARATE_MENU;
	}
}
