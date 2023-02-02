package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateSlot extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final ItemSet itemSet;
	private final Collection<ContainerSlotValues> existingSlots;
	private final Consumer<ContainerSlotValues> changeSlot;

	public CreateSlot(
			GuiComponent returnMenu, ItemSet itemSet,
			Collection<ContainerSlotValues> existingSlots, Consumer<ContainerSlotValues> changeSlot
	) {
		this.returnMenu = returnMenu;
		this.itemSet = itemSet;
		this.existingSlots = existingSlots;
		this.changeSlot = changeSlot;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		addComponent(
				new DynamicTextComponent("Change to", LABEL),
				0.6f, 0.885f, 0.75f, 0.985f
		);

		addComponent(new DynamicTextButton("Execute script", BUTTON, HOVER, () -> {
			//state.getWindow().setMainComponent(new CreateScriptSlot());
		}), 0.6f, 0.825f, 0.75f, 0.875f);
		addComponent(new DynamicTextButton("Link to other container", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateLinkSlot(
					returnMenu, itemSet, changeSlot
			));
		}), 0.6f, 0.75f, 0.85f, 0.8f);
		addComponent(new DynamicTextButton("Energy indicator", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateEnergyIndicatorSlot(
					returnMenu, itemSet, changeSlot
			));
		}), 0.6f, 0.675f, 0.8f, 0.725f);
		addComponent(new DynamicTextButton("Decoration", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateDisplay(returnMenu, itemSet, display -> {
				changeSlot.accept(DecorationSlotValues.createQuick(display));
			}, true));
		}), 0.6f, 0.6f, 0.8f, 0.65f);
		addComponent(new DynamicTextButton("Empty", BUTTON, HOVER, () -> {
			changeSlot.accept(new EmptySlotValues());
			state.getWindow().setMainComponent(returnMenu);
		}), 0.6f, 0.525f, 0.7f, 0.575f);
		addComponent(new DynamicTextButton("Fuel", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateFuelSlot(
					returnMenu, itemSet, existingSlots, changeSlot
			));
		}), 0.6f, 0.45f, 0.7f, 0.5f);
		addComponent(new DynamicTextButton("Fuel indicator", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateFuelIndicatorSlot(
					returnMenu, itemSet, existingSlots, changeSlot
			));
		}), 0.6f, 0.375f, 0.8f, 0.425f);
		addComponent(new DynamicTextButton("Input", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateInputSlot(
					returnMenu, itemSet, existingSlots, changeSlot
			));
		}), 0.6f, 0.3f, 0.7f, 0.35f);
		addComponent(new DynamicTextButton("Output", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateOutputSlot(
					returnMenu, itemSet, existingSlots, changeSlot
			));
		}), 0.6f, 0.225f, 0.7f, 0.275f);
		addComponent(new DynamicTextButton("Crafting progress indicator", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateCraftingIndicatorSlot(
					returnMenu, itemSet, changeSlot
			));
		}), 0.6f, 0.15f, 0.9f, 0.2f);
		addComponent(new DynamicTextButton("Storage", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateStorageSlot(
					returnMenu, itemSet, changeSlot
			));
		}), 0.6f, 0.075f, 0.75f, 0.125f);
		addComponent(new DynamicTextButton("Manual output", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CreateManualOutputSlot(
					returnMenu, itemSet, existingSlots, changeSlot
			));
		}), 0.6f, 0f, 0.8f, 0.05f);

		// TODO Update help button
		HelpButtons.addHelpLink(this, "edit menu/containers/slots/create.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
