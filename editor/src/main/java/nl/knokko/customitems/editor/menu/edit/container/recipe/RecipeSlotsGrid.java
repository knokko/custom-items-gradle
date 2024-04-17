package nl.knokko.customitems.editor.menu.edit.container.recipe;

import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;

public class RecipeSlotsGrid extends GuiMenu {
	
	private final GuiComponent outerMenu;
	private final ItemSet itemSet;
	private final KciContainer container;
	private final ContainerRecipe recipe;

	private final KciIngredient[] pClipboardIngredient = { null };
	private final OutputTable[] pClipboardResult = { null };

	public RecipeSlotsGrid(
			GuiComponent outerMenu, ItemSet itemSet,
			KciContainer container, ContainerRecipe recipe
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
				ContainerSlot slot = container.getSlot(x, y);
				if (slot instanceof InputSlot) {
					InputSlot inputSlot = (InputSlot) slot;
					slotComponent = new InputSlotComponent(
							inputSlot.getName(), outerMenu, pClipboardIngredient, recipe, itemSet
					);
				} else if (slot instanceof OutputSlot) {
					OutputSlot outputSlot = (OutputSlot) slot;
					slotComponent = new OutputSlotComponent(
							outputSlot.getName(), outerMenu, pClipboardResult, container, recipe, itemSet
					);
				} else if (slot instanceof ManualOutputSlot) {
					ManualOutputSlot outputSlot = (ManualOutputSlot) slot;
					slotComponent = new ManualOutputSlotComponent(
							outputSlot.getName(), outerMenu, container, recipe, itemSet
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
