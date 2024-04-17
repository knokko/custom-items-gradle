package nl.knokko.customitems.editor.menu.edit.container.slot;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.ContainerSlot;
import nl.knokko.customitems.container.slot.EmptySlot;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class SlotsComponent extends GuiMenu {
	
	private final GuiComponent outerMenu;
	private final ItemSet set;
	private final KciContainer container;
	private ContainerSlot clipboardSlot;
	
	public SlotsComponent(GuiComponent outerMenu, ItemSet set, KciContainer container) {
		this.outerMenu = outerMenu;
		this.set = set;
		this.container = container;
		this.clipboardSlot = new EmptySlot();
	}
	
	@Override
	protected void addComponents() {
		for (int x = 0; x < container.getWidth(); x++) {
			for (int y = 0; y < container.getHeight(); y++) {
				addComponent(new SlotComponent(
						outerMenu, set, container, x, y,
						() -> this.clipboardSlot.nonConflictingCopy(container),
						slotToCopy -> this.clipboardSlot = slotToCopy
				), x * 0.1f, 0.85f - y * 0.15f, x * 0.1f + 0.1f, 1f - y * 0.15f);
			}
		}
		
		for (int y = 0; y < container.getHeight(); y++) {
			final int fixedY = y;
			if (y != 0) {
				addComponent(new DynamicTextButton("/\\", BUTTON, HOVER, () -> {
					for (int x = 0; x < 9; x++) {
						ContainerSlot oldUpper = container.getSlot(x, fixedY - 1);
						container.setSlot(x, fixedY - 1, container.getSlot(x, fixedY));
						container.setSlot(x, fixedY, oldUpper);
					}
					clearComponents();
					addComponents();
				}), 0.9f, 0.95f - 0.15f * y, 0.95f, 1f - 0.15f * y);
			}
			if (y != container.getHeight() - 1) {
				addComponent(new DynamicTextButton("\\/", BUTTON, HOVER, () -> {
					for (int x = 0; x < 9; x++) {
						ContainerSlot oldUpper = container.getSlot(x, fixedY);
						container.setSlot(x, fixedY, container.getSlot(x, fixedY + 1));
						container.setSlot(x, fixedY + 1, oldUpper);
					}
					clearComponents();
					addComponents();
				}), 0.9f, 0.85f - 0.15f * y, 0.95f, 0.9f - 0.15f * y);
			}
			addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
				container.removeSlotRow(fixedY);
				clearComponents();
				addComponents();
			}), 0.9f, 0.9f - 0.15f * y, 0.95f, 0.95f - 0.15f * y);

			// Add a + button for each row, to insert a new empty row below it
			// But at most 6 rows are allowed
			if (container.getHeight() < 6) {
				addComponent(new DynamicTextButton("+", SAVE_BASE, SAVE_HOVER, () -> {
					container.insertSlotRow(fixedY);
					clearComponents();
					addComponents();
				}), 0.95f, 0.825f - 0.15f * y, 1f, 0.875f - 0.15f * y);
			}
		}
		
		// The upper + button, to insert a new empty first row
		if (container.getHeight() < 6) {
			addComponent(new DynamicTextButton("+", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				container.insertSlotRow(container.getHeight());
				clearComponents();
				addComponents();
			}), 0.95f, 0.95f, 1f, 1f);
		}
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
