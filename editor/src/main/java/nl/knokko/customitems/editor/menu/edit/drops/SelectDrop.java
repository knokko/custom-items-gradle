package nl.knokko.customitems.editor.menu.edit.drops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.drops.DropValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.container.recipe.EditOutputTable;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class SelectDrop extends GuiMenu {
	
	private final SItemSet set;
	private final GuiComponent returnMenu;
	private final Consumer<DropValues> receiver;
	
	private OutputTableValues dropTable;
	private boolean cancelNormalDrop;
	private Collection<ItemReference> requiredHeldItems;
	
	private final DynamicTextComponent errorComponent;
	
	public SelectDrop(SItemSet set, GuiComponent returnMenu, DropValues previous, Consumer<DropValues> receiver) {
		this.set = set;
		this.returnMenu = returnMenu;
		this.receiver = receiver;
		
		if (previous != null) {
			dropTable = previous.getOutputTable();
			cancelNormalDrop = previous.shouldCancelNormalDrops();
			requiredHeldItems = previous.getRequiredHeldItems();
		} else {
			dropTable = new OutputTableValues(true);
			cancelNormalDrop = false;
			requiredHeldItems = new ArrayList<>();
		}
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		addComponent(new DynamicTextComponent("Items to drop:", EditProps.LABEL), 0.3f, 0.7f, 0.5f, 0.8f);
		addComponent(new DynamicTextButton("Choose...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditOutputTable(
					this, dropTable, newDropTable -> dropTable = newDropTable, set
			));
		}), 0.55f, 0.7f, 0.7f, 0.8f);
		
		addComponent(new DynamicTextComponent("Item that must be held/used:", 
						EditProps.LABEL), 0.3f, 0.55f, 0.65f, 0.65f
		);
		addComponent(new DynamicTextButton("Choose...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseRequiredHeldItems(set, requiredHeldItems, newRequiredHeldItems -> {
				requiredHeldItems = newRequiredHeldItems;
			}, this, "Players can use any item"));
		}), 0.7f, 0.55f, 0.85f, 0.65f);
		
		addComponent(new DynamicTextComponent("Prevent normal drops", EditProps.LABEL), 0.3f, 0.225f, 0.55f, 0.325f);
		CheckboxComponent preventOtherDrops = new CheckboxComponent(cancelNormalDrop);
		addComponent(preventOtherDrops, 0.25f, 0.225f, 0.275f, 0.25f);
		
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = Validation.toErrorString(() -> dropTable.validate(set));
			if (error == null) {
				receiver.accept(DropValues.createQuick(dropTable, preventOtherDrops.isChecked(), requiredHeldItems));
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.1f, 0.2f, 0.2f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/drops/drop.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
