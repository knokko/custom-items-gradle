package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.InputCustomSlot;
import nl.knokko.customitems.container.slot.OutputCustomSlot;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.recipe.ContainerRecipe.InputEntry;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class MissingSlotsComponent extends GuiMenu {
	
	private final CustomSlot[][] slots;
	private final Collection<InputEntry> inputs;
	private final Collection<OutputEntry> outputs;
	
	public MissingSlotsComponent(CustomSlot[][] slots, Collection<InputEntry> inputs,
			Collection<OutputEntry> outputs) {
		this.slots = slots;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	@Override
	protected void addComponents() {
		Iterable<CustomSlot> slots = CustomContainer.slotIterable(this.slots);
		
		Collection<String> missingInputSlots = new ArrayList<>();
		inputLoop:
		for (InputEntry input : inputs) {
			for (CustomSlot slot : slots) {
				if (slot instanceof InputCustomSlot) {
					InputCustomSlot inputSlot = (InputCustomSlot) slot;
					if (inputSlot.getName().equals(input.getInputSlotName())) {
						continue inputLoop;
					}
				}
			}
			missingInputSlots.add(input.getInputSlotName());
		}
		
		Collection<String> missingOutputSlots = new ArrayList<>();
		outputLoop:
		for (OutputEntry output : outputs) {
			for (CustomSlot slot : slots) {
				if (slot instanceof OutputCustomSlot) {
					OutputCustomSlot outputSlot = (OutputCustomSlot) slot;
					if (outputSlot.getName().equals(output.getOutputSlotName())) {
						continue outputLoop;
					}
				}
			}
			missingOutputSlots.add(output.getOutputSlotName());
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
					InputEntry toRemove = null;
					for (InputEntry inputEntry : inputs) {
						if (inputEntry.getInputSlotName().equals(rememberMissingInput)) {
							toRemove = inputEntry;
							break;
						}
					}
					
					inputs.remove(toRemove);
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
					OutputEntry toRemove = null;
					for (OutputEntry outputEntry : outputs) {
						if (outputEntry.getOutputSlotName().equals(rememberMissingOutput)) {
							toRemove = outputEntry;
							break;
						}
					}
					
					outputs.remove(toRemove);
					clearComponents();
					addComponents();
				}), 0.81f, 0.5f - 0.25f * index, 0.84f, 0.6f - 0.25f * index);
				index++;
			}
		}
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
