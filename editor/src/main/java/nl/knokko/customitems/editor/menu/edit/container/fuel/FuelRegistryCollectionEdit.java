package nl.knokko.customitems.editor.menu.edit.container.fuel;

import java.awt.image.BufferedImage;

import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.DedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.FuelRegistryReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class FuelRegistryCollectionEdit extends DedicatedCollectionEdit<FuelRegistryValues, FuelRegistryReference> {
	
	private final ItemSet set;

	public FuelRegistryCollectionEdit(GuiComponent returnMenu, ItemSet set) {
		super(returnMenu, set.fuelRegistries.references(), null);
		this.set = set;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextButton("Add new", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditFuelRegistry(
					this, set, new FuelRegistryValues(true), null
			));
		}), 0.05f, 0.2f, 0.2f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/fuel registries/overview.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	@Override
	protected String getModelLabel(FuelRegistryValues model) {
		return model.getName();
	}

	@Override
	protected BufferedImage getModelIcon(FuelRegistryValues model) {
		return null;
	}

	@Override
	protected boolean canEditModel(FuelRegistryValues model) {
		return true;
	}

	@Override
	protected GuiComponent createEditMenu(FuelRegistryReference modelReference) {
		return new EditFuelRegistry(this, set, modelReference.get(), modelReference);
	}

	@Override
	protected String deleteModel(FuelRegistryReference modelReference) {
		return Validation.toErrorString(() -> set.fuelRegistries.remove(modelReference));
	}

	@Override
	protected boolean canDeleteModels() {
		return true;
	}

	@Override
	protected CopyMode getCopyMode(FuelRegistryReference modelReference) {
		return CopyMode.SEPARATE_MENU;
	}

	@Override
	protected GuiComponent createCopyMenu(FuelRegistryReference modelReference) {
		return new EditFuelRegistry(this, set, modelReference.get(), null);
	}
}
