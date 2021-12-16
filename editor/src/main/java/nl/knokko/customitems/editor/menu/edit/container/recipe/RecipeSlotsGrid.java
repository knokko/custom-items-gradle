package nl.knokko.customitems.editor.menu.edit.container.recipe;

import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;

import java.util.Map;

public class RecipeSlotsGrid extends GuiMenu {
	
	private final GuiComponent outerMenu;
	private final ContainerSlotValues[][] slots;
	private final Map<String, IngredientValues> inputs;
	private final Map<String, OutputTableValues> outputs;
	private final SItemSet set;
	
	public RecipeSlotsGrid(ContainerSlotValues[][] slots, GuiComponent outerMenu,
			Map<String, IngredientValues> inputs, Map<String, OutputTableValues> outputs, SItemSet set) {
		this.slots = slots;
		this.outerMenu = outerMenu;
		this.inputs = inputs;
		this.outputs = outputs;
		this.set = set;
	}

	@Override
	protected void addComponents() {
		int numRows = slots[0].length;
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < numRows; y++) {
				GuiComponent slotComponent;
				ContainerSlotValues slot = slots[x][y];
				if (slot instanceof InputSlotValues) {
					InputSlotValues inputSlot = (InputSlotValues) slot;
					slotComponent = new InputSlotComponent(
							inputSlot.getName(), outerMenu, inputs, set
					);
				} else if (slot instanceof OutputSlotValues) {
					OutputSlotValues outputSlot = (OutputSlotValues) slot;
					slotComponent = new OutputSlotComponent(
							outputSlot.getName(), outerMenu, outputs, set
					);
				} else {
					slotComponent = new OtherSlotComponent();
				}
				
				addComponent(
						slotComponent, 
						x * 0.11f, 0.85f - y * 0.15f, 
						(x + 1) * 0.11f, 1f - y * 0.15f
				);
			}
		}
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
