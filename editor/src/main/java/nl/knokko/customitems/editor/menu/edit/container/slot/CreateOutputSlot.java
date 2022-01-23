package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.container.slot.OutputSlotValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateOutputSlot extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final SItemSet itemSet;
	private final Collection<ContainerSlotValues> existingSlots;
	private final OutputSlotValues currentValues;
	private final Consumer<ContainerSlotValues> submitSlot;
	private final DynamicTextComponent errorComponent;
	
	public CreateOutputSlot(
			GuiComponent returnMenu, SItemSet itemSet,
			Collection<ContainerSlotValues> existingSlots, Consumer<ContainerSlotValues> submitSlot
	) {
		this.returnMenu = returnMenu;
		this.itemSet = itemSet;
		this.existingSlots = existingSlots;
		this.currentValues = new OutputSlotValues(true);
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
				new DynamicTextComponent("Placeholder:", LABEL),
				0.25f, 0.6f, 0.4f, 0.65f
		);
		addComponent(new DynamicTextButton("Choose...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateDisplay(
					this, itemSet, currentValues::setPlaceholder, true
			));
		}), 0.425f, 0.6f, 0.55f, 0.65f);
		addComponent(new DynamicTextButton("Clear", BUTTON, HOVER, () -> {
			currentValues.setPlaceholder(null);
		}), 0.575f, 0.6f, 0.675f, 0.65f);
		
		addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
			String error = Validation.toErrorString(() -> currentValues.validate(itemSet, existingSlots));

			if (error == null) {
				submitSlot.accept(currentValues);
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.3f, 0.15f, 0.4f);
		HelpButtons.addHelpLink(this, "edit menu/containers/slots/output.html");
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
