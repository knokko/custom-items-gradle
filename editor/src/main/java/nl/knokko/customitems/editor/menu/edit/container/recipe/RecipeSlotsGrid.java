package nl.knokko.customitems.editor.menu.edit.container.recipe;

import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;

public class RecipeSlotsGrid extends GuiMenu {
	
	private final GuiComponent outerMenu;
	private final ItemSet itemSet;
	private final CustomContainerValues container;
	private final ContainerRecipeValues recipe;

	public RecipeSlotsGrid(
			GuiComponent outerMenu, ItemSet itemSet,
			CustomContainerValues container, ContainerRecipeValues recipe
	) {
		this.outerMenu = outerMenu;
		this.itemSet = itemSet;
		this.container = container;
		this.recipe = recipe;
	}

	@Override
	protected void addComponents() {
		for (int x = 0; x < container.getWidth(); x++) {
			for (int y = 0; y < container.getHeight(); y++) {
				GuiComponent slotComponent;
				ContainerSlotValues slot = container.getSlot(x, y);
				if (slot instanceof InputSlotValues) {
					InputSlotValues inputSlot = (InputSlotValues) slot;
					slotComponent = new InputSlotComponent(
							inputSlot.getName(), outerMenu, recipe, itemSet
					);
				} else if (slot instanceof OutputSlotValues) {
					OutputSlotValues outputSlot = (OutputSlotValues) slot;
					slotComponent = new OutputSlotComponent(
							outputSlot.getName(), outerMenu, recipe, itemSet
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
