package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.SafeCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.UpgradeResultValues;
import nl.knokko.customitems.util.Chance;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditOutputTable extends SafeCollectionEdit<OutputTableValues.Entry> {
	
	private final Consumer<OutputTableValues> onApply;
	private final boolean isCreatingNew;
	private final ItemSet set;
	private final BiFunction<GuiComponent, UpgradeResultValues, GuiComponent> createUpgradeIngredientMenu;

	private Chance previousNothingChance = null;
	private final DynamicTextComponent nothingChanceComponent;

	public EditOutputTable(
			GuiComponent returnMenu, OutputTableValues original, Consumer<OutputTableValues> onApply, ItemSet set,
			BiFunction<GuiComponent, UpgradeResultValues, GuiComponent> createUpgradeIngredientMenu
	) {
		super(returnMenu, original == null ? new ArrayList<>() : Mutability.createDeepCopy(original.getEntries(), true));
		this.onApply = onApply;
		this.isCreatingNew = original == null;
		this.set = set;
		this.createUpgradeIngredientMenu = createUpgradeIngredientMenu;
		
		this.nothingChanceComponent = new DynamicTextComponent("", EditProps.LABEL);
	}
	
	@Override
	public void update() {
		super.update();
		
		Chance currentNothingChance = OutputTableValues.createQuick(currentCollection).getNothingChance();
		if (!Objects.equals(currentNothingChance, previousNothingChance)) {
			if (currentNothingChance != null) {
				nothingChanceComponent.setText("Chance to get nothing: " + currentNothingChance);
			} else {
				nothingChanceComponent.setText("Error: total chance > 100%");
			}
			previousNothingChance = currentNothingChance;
		}
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(new DynamicTextButton("Add entry", EditProps.BUTTON, EditProps.HOVER, () -> {
			ResultValues oldResult = null;
			for (OutputTableValues.Entry entry : currentCollection) {
				oldResult = entry.getResult();
				break;
			}
			state.getWindow().setMainComponent(new CreateOutputTableEntry(
					this, currentCollection::add, set, oldResult, createUpgradeIngredientMenu
			));
		}), 0.025f, 0.55f, 0.2f, 0.65f);
		
		addComponent(nothingChanceComponent, 0f, 0.4f, 0.25f, 0.5f);
		
		HelpButtons.addHelpLink(this, "edit menu/recipes/output table.html");
	}

	@Override
	protected String getItemLabel(OutputTableValues.Entry item) {
		return item.getChance() + " " + item.getResult();
	}

	@Override
	protected BufferedImage getItemIcon(OutputTableValues.Entry item) {
		if (item.getResult() instanceof CustomItemResultValues) {
			return ((CustomItemResultValues) item.getResult()).getItem().getTexture().getImage();
		} else {
			return null;
		}
	}

	@Override
	protected EditMode getEditMode(OutputTableValues.Entry item) {
		// Entries are so simple that I don't even bother editing
		return EditMode.DISABLED;
	}

	@Override
	protected GuiComponent createEditMenu(OutputTableValues.Entry itemToEdit) {
		// Entries are so simple that I don't even bother editing
		return null;
	}

	@Override
	protected String deleteItem(OutputTableValues.Entry itemToDelete) {
		// Deleting entries is always allowed
		return null;
	}

	@Override
	protected CopyMode getCopyMode(OutputTableValues.Entry item) {
		// Entries are so simple that I don't even bother copying
		return CopyMode.DISABLED;
	}

	@Override
	protected OutputTableValues.Entry copy(OutputTableValues.Entry item) {
		// Entries are immutable, so no need for a real copy
		return item;
	}

	@Override
	protected GuiComponent createCopyMenu(OutputTableValues.Entry itemToCopy) {
		// Entries are so simple that I don't even bother copying
		return null;
	}

	@Override
	protected boolean isCreatingNew() {
		return isCreatingNew;
	}

	@Override
	protected void onApply() {
		OutputTableValues outputTable = OutputTableValues.createQuick(currentCollection);
		String error = Validation.toErrorString(() -> outputTable.validate(set));
		
		if (error == null) {
			onApply.accept(outputTable);
			state.getWindow().setMainComponent(returnMenu);
		} else {
			errorComponent.setText(error);
		}
	}
}
