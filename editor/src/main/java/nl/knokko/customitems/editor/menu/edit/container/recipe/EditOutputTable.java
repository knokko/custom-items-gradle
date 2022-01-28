package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.SafeCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditOutputTable extends SafeCollectionEdit<OutputTableValues.Entry> {
	
	private final Consumer<OutputTableValues> onApply;
	private final boolean isCreatingNew;
	private final ItemSet set;
	
	// 101 is an impossible value, so this will trigger a recompute right away
	private int previousNothingChance = 101;
	private final DynamicTextComponent nothingChanceComponent;

	public EditOutputTable(GuiComponent returnMenu, OutputTableValues original,
			Consumer<OutputTableValues> onApply, ItemSet set) {
		super(returnMenu, original == null ? new ArrayList<>() : Mutability.createDeepCopy(original.getEntries(), true));
		this.onApply = onApply;
		this.isCreatingNew = original == null;
		this.set = set;
		
		this.nothingChanceComponent = new DynamicTextComponent("", EditProps.LABEL);
	}
	
	@Override
	public void update() {
		super.update();
		
		int currentNothingChance = OutputTableValues.createQuick(currentCollection).getNothingChance();
		if (currentNothingChance != previousNothingChance) {
			nothingChanceComponent.setText("Chance to get nothing: " + currentNothingChance + "%");
			previousNothingChance = currentNothingChance;
		}
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		
		addComponent(new DynamicTextButton("Add entry", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateOutputTableEntry(
					this, currentCollection::add, set
			));
		}), 0.025f, 0.55f, 0.2f, 0.65f);
		
		addComponent(nothingChanceComponent, 0f, 0.4f, 0.25f, 0.5f);
		
		HelpButtons.addHelpLink(this, "edit menu/recipes/output table.html");
	}

	@Override
	protected String getItemLabel(OutputTableValues.Entry item) {
		return item.getChance() + "% " + item.getResult();
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
