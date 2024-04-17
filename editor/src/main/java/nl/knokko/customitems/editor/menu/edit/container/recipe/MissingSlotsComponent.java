package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class MissingSlotsComponent extends GuiMenu {

	private final KciContainer container;
	private final ContainerRecipe recipe;

	public MissingSlotsComponent(KciContainer container, ContainerRecipe recipe) {
		this.container = container;
		this.recipe = recipe;
	}

	@Override
	protected void addComponents() {
		Iterable<ContainerSlot> slots = KciContainer.createSlotList(container.getSlots());
		
		Collection<String> missingInputSlots = new ArrayList<>();
		inputLoop:
		for (String inputSlotName : recipe.getInputs().keySet()) {
			for (ContainerSlot slot : slots) {
				if (slot instanceof InputSlot) {
					InputSlot inputSlot = (InputSlot) slot;
					if (inputSlot.getName().equals(inputSlotName)) {
						continue inputLoop;
					}
				}
			}
			missingInputSlots.add(inputSlotName);
		}
		
		Collection<String> missingOutputSlots = new ArrayList<>();
		outputLoop:
		for (String outputSlotName : recipe.getOutputs().keySet()) {
			for (ContainerSlot slot : slots) {
				if (slot instanceof OutputSlot) {
					OutputSlot outputSlot = (OutputSlot) slot;
					if (outputSlot.getName().equals(outputSlotName)) {
						continue outputLoop;
					}
				}
			}
			missingOutputSlots.add(outputSlotName);
		}

		boolean missingManualOutputSlot = false;
		if (recipe.getManualOutputSlotName() != null) {
			missingManualOutputSlot = true;
			for (ContainerSlot slot : slots) {
				if (slot instanceof ManualOutputSlot) {
					ManualOutputSlot outputSlot = (ManualOutputSlot) slot;
					if (outputSlot.getName().equals(recipe.getManualOutputSlotName())) {
						missingManualOutputSlot = false;
						break;
					}
				}
			}
		}
		
		if (!missingInputSlots.isEmpty()) {
			addComponent(new DynamicTextComponent("Missing input slots:", EditProps.ERROR), 0f, 0.7f, 0.45f, 1f);
			int index = 0;
			for (String missingInput : missingInputSlots) {
				final String rememberMissingInput = missingInput;
				addComponent(
						new DynamicTextComponent(missingInput, EditProps.LABEL), 
						0.05f, 0.45f - 0.25f * index, 0.25f, 0.65f - 0.25f * index
				);
				addComponent(new DynamicTextButton("X", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					recipe.clearInput(rememberMissingInput);
					clearComponents();
					addComponents();
				}), 0.26f, 0.5f - 0.25f * index, 0.29f, 0.6f - 0.25f * index);
				index++;
			}
		}
		
		if (!missingOutputSlots.isEmpty()) {
			addComponent(new DynamicTextComponent("Missing output slots:", EditProps.ERROR), 0.55f, 0.7f, 1f, 1f);
			int index = 0;
			for (String missingOutput : missingOutputSlots) {
				final String rememberMissingOutput = missingOutput;
				addComponent(
						new DynamicTextComponent(missingOutput, EditProps.LABEL), 
						0.6f, 0.45f - 0.25f * index, 0.8f, 0.65f - 0.25f * index
				);
				addComponent(new DynamicTextButton("X", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					recipe.clearOutput(rememberMissingOutput);
					clearComponents();
					addComponents();
				}), 0.81f, 0.5f - 0.25f * index, 0.84f, 0.6f - 0.25f * index);
				index++;
			}
		}

		if (missingManualOutputSlot) {
			addComponent(new DynamicTextComponent("Missing manual output slot", EditProps.ERROR), 0.55f, 0.7f, 1f, 1f);
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
