package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateFuelIndicatorSlot extends GuiMenu {

	private final GuiComponent returnMenu;
	private final ItemSet itemSet;
	private final Collection<ContainerSlot> existingSlots;
	private final FuelIndicatorSlot currentValues;
	private final Consumer<ContainerSlot> submitSlot;
	private final DynamicTextComponent errorComponent;
	
	public CreateFuelIndicatorSlot(
            GuiComponent returnMenu, ItemSet itemSet, Collection<ContainerSlot> existingSlots,
            Consumer<ContainerSlot> submitSlot
	) {
		this.returnMenu = returnMenu;
		this.itemSet = itemSet;
		this.existingSlots = existingSlots;
		this.currentValues = new FuelIndicatorSlot(true);
		this.submitSlot = submitSlot;
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);
		
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		addComponent(
				new DynamicTextComponent("Fuel slot:", LABEL),
				0.25f, 0.7f, 0.4f, 0.75f
		);
		addComponent(
				CollectionSelect.createButton(
						existingSlots.stream().filter(
								slot -> slot instanceof FuelSlot
						).map(
								slot -> (FuelSlot) slot
						).collect(Collectors.toList()),
						newSlot -> currentValues.setFuelSlotName(newSlot.getName()),
						FuelSlot::getName, null, false
				), 0.425f, 0.7f, 0.6f, 0.75f
		);
		
		addComponent(new DynamicTextButton("Display...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateDisplay(
					this, itemSet, currentValues::setDisplay, false
			));
		}), 0.25f, 0.625f, 0.4f, 0.675f);
		addComponent(new DynamicTextButton("Placeholder...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateDisplay(
					this, itemSet, currentValues::setPlaceholder, true
			));
		}), 0.25f, 0.55f, 0.4f, 0.6f);
		
		addComponent(
				new DynamicTextComponent("Indication domain:", LABEL),
				0.25f, 0.475f, 0.4f, 0.525f
		);
		addComponent(
				new EagerIntEditField(
						currentValues.getIndicatorDomain().getBegin(), 0, 100, EDIT_BASE, EDIT_ACTIVE,
						newBegin -> currentValues.setIndicatorDomain(new IndicatorDomain(newBegin, currentValues.getIndicatorDomain().getEnd()))
				), 0.425f, 0.475f, 0.475f, 0.525f
		);
		addComponent(
				new DynamicTextComponent("% to ", LABEL),
				0.475f, 0.475f, 0.525f, 0.525f
		);
		addComponent(
				new EagerIntEditField(
						currentValues.getIndicatorDomain().getEnd(), 0, 100, EDIT_BASE, EDIT_ACTIVE,
						newEnd -> currentValues.setIndicatorDomain(new IndicatorDomain(currentValues.getIndicatorDomain().getBegin(), newEnd))
				), 0.525f, 0.475f, 0.575f, 0.525f
		);
		addComponent(
				new DynamicTextComponent("%", LABEL),
				0.575f, 0.475f, 0.6f, 0.525f
		);
		
		addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
			String error = Validation.toErrorString(() -> currentValues.validate(itemSet, existingSlots));
			if (error == null) {
				submitSlot.accept(currentValues);
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.2f, 0.15f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/slots/fuel indicator.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
