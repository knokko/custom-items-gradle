package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.container.slot.FuelSlotValues;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.container.fuel.EditFuelRegistry;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateFuelSlot extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final ItemSet itemSet;
	private final Collection<ContainerSlotValues> otherSlots;
	private final FuelSlotValues currentValues;
	private final Consumer<ContainerSlotValues> submitSlot;
	private final DynamicTextComponent errorComponent;
	
	public CreateFuelSlot(
			GuiComponent returnMenu, ItemSet itemSet,
			Collection<ContainerSlotValues> otherSlots, Consumer<ContainerSlotValues> submitSlot
	) {
		this.returnMenu = returnMenu;
		this.itemSet = itemSet;
		this.otherSlots = otherSlots;
		this.currentValues = new FuelSlotValues(true);
		this.submitSlot = submitSlot;
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);
		
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		
		addComponent(
				new DynamicTextComponent("Name:", LABEL),
				0.25f, 0.7f, 0.35f, 0.75f
		);
		addComponent(
				new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName),
				0.375f, 0.7f, 0.5f, 0.75f
		);
		
		addComponent(
				new DynamicTextComponent("Registry:", LABEL),
				0.25f, 0.6f, 0.4f, 0.65f
		);
		addComponent(CollectionSelect.createButton(
				itemSet.fuelRegistries.references(),
				currentValues::setFuelRegistry,
				registry -> registry == null ? "Select..." : registry.get().getName(),
				null, false
			), 0.425f, 0.6f, 0.55f, 0.65f
		);
		addComponent(new DynamicTextButton("Create new", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EditFuelRegistry(
					this, itemSet, new FuelRegistryValues(true), null
			));
		}), 0.6f, 0.6f, 0.75f, 0.65f);
		
		addComponent(
				new DynamicTextComponent("Placeholder:", LABEL),
				0.25f, 0.5f, 0.45f, 0.55f
		);
		addComponent(new DynamicTextButton("Choose...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateDisplay(
					this, itemSet, currentValues::setPlaceholder, true
			));
		}), 0.475f, 0.5f, 0.625f, 0.55f);
		addComponent(new DynamicTextButton("Clear", CANCEL_BASE, CANCEL_HOVER, () -> {
			currentValues.setPlaceholder(null);
		}), 0.65f, 0.5f, 0.75f, 0.55f);
		
		addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
			String error = Validation.toErrorString(() -> currentValues.validate(itemSet, otherSlots));
			if (error == null) {
				submitSlot.accept(currentValues);
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.2f, 0.2f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/slots/fuel.html");
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
