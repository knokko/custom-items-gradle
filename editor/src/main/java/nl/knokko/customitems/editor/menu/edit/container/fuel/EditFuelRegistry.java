package nl.knokko.customitems.editor.menu.edit.container.fuel;

import nl.knokko.customitems.container.fuel.ContainerFuelRegistry;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.FuelRegistryReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditFuelRegistry extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final ItemSet set;
	
	private final FuelRegistryReference toModify;
	private final ContainerFuelRegistry currentValues;
	
	private final DynamicTextComponent errorComponent;
	
	public EditFuelRegistry(GuiComponent returnMenu, ItemSet set,
                            ContainerFuelRegistry oldValues, FuelRegistryReference toModify) {
		this.returnMenu = returnMenu;
		this.set = set;
		this.toModify = toModify;
		this.currentValues = oldValues.copy(true);
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		
		addComponent(errorComponent, 0.025f, 0.9f, 0.97f, 1f);
		
		addComponent(
				new DynamicTextComponent("Name:", EditProps.LABEL),
				0.25f, 0.7f, 0.375f, 0.75f
		);
		addComponent(
				new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName),
				0.4f, 0.7f, 0.6f, 0.75f
		);
		
		addComponent(new DynamicTextButton("Entries...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new FuelEntryCollectionEdit(
					currentValues.getEntries(), currentValues::setEntries, this, set
			));
		}), 0.4f, 0.5f, 0.6f, 0.6f);
		
		if (toModify != null) {
			addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = Validation.toErrorString(() -> set.fuelRegistries.change(toModify, currentValues));
				if (error != null) {
					errorComponent.setText(error);
					errorComponent.setProperties(EditProps.ERROR);
				} else {
					state.getWindow().setMainComponent(returnMenu);
				}
			}), 0.025f, 0.1f, 0.15f, 0.2f);
		} else {
			addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = Validation.toErrorString(() -> set.fuelRegistries.add(currentValues));
				if (error != null) {
					errorComponent.setProperties(EditProps.ERROR);
					errorComponent.setText(error);
				} else
					state.getWindow().setMainComponent(returnMenu);
			}), 0.025f, 0.1f, 0.15f, 0.2f);
		}
		HelpButtons.addHelpLink(this, "edit menu/containers/fuel registries/edit.html");
	}

}
