package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class MissingSlotsComponent extends GuiMenu {
	
	private final ContainerSlotValues[][] slots;
	private final Map<String, IngredientValues> inputs;
	private final Map<String, OutputTableValues> outputs;
	
	public MissingSlotsComponent(ContainerSlotValues[][] slots, Map<String, IngredientValues> inputs,
			Map<String, OutputTableValues> outputs) {
		this.slots = slots;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	@Override
	protected void addComponents() {
		Iterable<ContainerSlotValues> slots = CustomContainerValues.createSlotList(this.slots);
		
		Collection<String> missingInputSlots = new ArrayList<>();
		inputLoop:
		for (String inputSlotName : inputs.keySet()) {
			for (ContainerSlotValues slot : slots) {
				if (slot instanceof InputSlotValues) {
					InputSlotValues inputSlot = (InputSlotValues) slot;
					if (inputSlot.getName().equals(inputSlotName)) {
						continue inputLoop;
					}
				}
			}
			missingInputSlots.add(inputSlotName);
		}
		
		Collection<String> missingOutputSlots = new ArrayList<>();
		outputLoop:
		for (String outputSlotName : outputs.keySet()) {
			for (ContainerSlotValues slot : slots) {
				if (slot instanceof OutputSlotValues) {
					OutputSlotValues outputSlot = (OutputSlotValues) slot;
					if (outputSlot.getName().equals(outputSlotName)) {
						continue outputLoop;
					}
				}
			}
			missingOutputSlots.add(outputSlotName);
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
					inputs.remove(rememberMissingInput);
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
					outputs.remove(rememberMissingOutput);
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
