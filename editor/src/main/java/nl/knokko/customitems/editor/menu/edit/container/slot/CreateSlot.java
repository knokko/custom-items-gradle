package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.function.Consumer;

import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class CreateSlot extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Consumer<ContainerSlotValues> changeSlot;
	private final SItemSet set;
	private final Iterable<ContainerSlotValues> existingSlots;
	private final ContainerSlotValues slotToReplace;
	
	public CreateSlot(GuiComponent returnMenu, Consumer<ContainerSlotValues> changeSlot,
			SItemSet set, Iterable<ContainerSlotValues> existingSlots, ContainerSlotValues slotToReplace) {
		this.returnMenu = returnMenu;
		this.changeSlot = changeSlot;
		this.set = set;
		this.existingSlots = existingSlots;
		this.slotToReplace = slotToReplace;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		addComponent(new DynamicTextComponent("Change to", EditProps.LABEL), 0.6f, 0.7f, 0.75f, 0.8f);
		addComponent(new DynamicTextButton("Decoration", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateDisplay(returnMenu, display -> {
				changeSlot.accept(DecorationSlotValues.createQuick(display));
			}, true, set.getItems().references()));
		}), 0.6f, 0.6f, 0.8f, 0.65f);
		addComponent(new DynamicTextButton("Empty", EditProps.BUTTON, EditProps.HOVER, () -> {
			changeSlot.accept(new EmptySlotValues());
			state.getWindow().setMainComponent(returnMenu);
		}), 0.6f, 0.525f, 0.7f, 0.575f);
		addComponent(new DynamicTextButton("Fuel", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateFuelSlot(returnMenu, 
					changeSlot, set, existingSlots, slotToReplace
			));
		}), 0.6f, 0.45f, 0.7f, 0.5f);
		addComponent(new DynamicTextButton("Fuel indicator", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateFuelIndicatorSlot(returnMenu,
					changeSlot, existingSlots, set.getItems().references()
			));
		}), 0.6f, 0.375f, 0.8f, 0.425f);
		addComponent(new DynamicTextButton("Input", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateInputSlot(returnMenu,
					changeSlot, existingSlots, set.getItems().references(), slotToReplace
			));
		}), 0.6f, 0.3f, 0.7f, 0.35f);
		addComponent(new DynamicTextButton("Output", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateOutputSlot(returnMenu,
					changeSlot, existingSlots, set.getItems().references(), slotToReplace
			));
		}), 0.6f, 0.225f, 0.7f, 0.275f);
		addComponent(new DynamicTextButton("Crafting progress indicator", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateCraftingIndicatorSlot(
					returnMenu, changeSlot, set.getItems().references()
			));
		}), 0.6f, 0.15f, 0.9f, 0.2f);
		addComponent(new DynamicTextButton("Storage", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateStorageSlot(
					returnMenu, changeSlot, set.getItems().references()
			));
		}), 0.6f, 0.075f, 0.75f, 0.125f);
		HelpButtons.addHelpLink(this, "edit menu/containers/slots/create.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
