package nl.knokko.customitems.editor.menu.edit.container.fuel;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.fuel.FuelEntry;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EditFuelRegistry extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final ItemSet set;
	
	private final CustomFuelRegistry toModify;
	
	private final TextEditField nameField;
	private final Collection<FuelEntry> entries;
	private final DynamicTextComponent errorComponent;
	
	public EditFuelRegistry(GuiComponent returnMenu, ItemSet set, 
			CustomFuelRegistry oldValues, CustomFuelRegistry toModify) {
		this.returnMenu = returnMenu;
		this.set = set;
		this.toModify = toModify;
		
		if (oldValues != null) {
			this.nameField = new TextEditField(oldValues.getName(), 
					EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE
			);
			this.entries = new ArrayList<>();
			for (FuelEntry oldEntry : oldValues.getEntries()) {
				this.entries.add(new FuelEntry(oldEntry.getFuel(), oldEntry.getBurnTime()));
			}
		} else {
			this.nameField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			this.entries = new ArrayList<>();
		}
		
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
		
		addComponent(new DynamicTextComponent("Name:", EditProps.LABEL), 0.25f, 0.7f, 0.375f, 0.75f);
		addComponent(nameField, 0.4f, 0.7f, 0.6f, 0.75f);
		
		addComponent(new DynamicTextButton("Entries...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new FuelEntryCollectionEdit(entries, this, set));
		}), 0.4f, 0.5f, 0.6f, 0.6f);
		
		if (toModify != null) {
			addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = set.changeFuelRegistry(toModify, nameField.getText(), entries);
				if (error != null) {
					errorComponent.setText(error);
					errorComponent.setProperties(EditProps.ERROR);
				} else {
					state.getWindow().setMainComponent(returnMenu);
				}
			}), 0.025f, 0.1f, 0.15f, 0.2f);
		} else {
			addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				String error = set.addFuelRegistry(new CustomFuelRegistry(nameField.getText(), entries));
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
