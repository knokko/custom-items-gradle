package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.util.Collection;

import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.InputCustomSlot;
import nl.knokko.customitems.container.slot.OutputCustomSlot;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.recipe.ContainerRecipe.InputEntry;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;

public class RecipeSlotsGrid extends GuiMenu {
	
	private final GuiComponent outerMenu;
	private final CustomSlot[][] slots;
	private final Collection<InputEntry> inputs;
	private final Collection<OutputEntry> outputs;
	private final ItemSet set;
	
	public RecipeSlotsGrid(CustomSlot[][] slots, GuiComponent outerMenu,
			Collection<InputEntry> inputs, Collection<OutputEntry> outputs, ItemSet set) {
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
				CustomSlot slot = slots[x][y];
				if (slot instanceof InputCustomSlot) {
					InputCustomSlot inputSlot = (InputCustomSlot) slot;
					slotComponent = new InputSlotComponent(
							inputSlot.getName(), outerMenu, inputs, set
					);
				} else if (slot instanceof OutputCustomSlot) {
					OutputCustomSlot outputSlot = (OutputCustomSlot) slot;
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
