package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.Arrays;

import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.EmptyCustomSlot;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

public class SlotsComponent extends GuiMenu {
	
	private final GuiComponent outerMenu;
	private final ItemSet set;
	
	private CustomSlot[][] slots;
	private CustomSlot clipboardSlot;
	
	public SlotsComponent(GuiComponent outerMenu, ItemSet set, CustomContainer oldValues) {
		this.outerMenu = outerMenu;
		this.set = set;
		if (oldValues != null) {
			this.slots = new CustomSlot[9][oldValues.getHeight()];
			for (int x = 0; x < 9; x++) {
				for (int y = 0; y < oldValues.getHeight(); y++) {
					this.slots[x][y] = oldValues.getSlot(x, y);
				}
			}
		} else {
			this.slots = new CustomSlot[9][5];
			for (int x = 0; x < 9; x++) {
				Arrays.fill(this.slots[x], new EmptyCustomSlot());
			}
		}
	}
	
	public CustomSlot[][] getSlots() {
		return slots;
	}

	@Override
	protected void addComponents() {
		Iterable<CustomSlot> allSlots = CustomContainer.slotIterable(slots);
		int numRows = slots[0].length;
		for (int x = 0; x < 9; x++) {
			final int fixedX = x;
			for (int y = 0; y < numRows; y++) {
				final int fixedY = y;
				addComponent(new SlotComponent(outerMenu, set, allSlots, slots[x][y],
						newSlot -> slots[fixedX][fixedY] = newSlot, 
						() -> this.clipboardSlot.safeClone(slots), 
						slotToCopy -> this.clipboardSlot = slotToCopy
				), x * 0.1f, 0.85f - y * 0.15f, x * 0.1f + 0.1f, 1f - y * 0.15f);
			}
		}
		
		for (int y = 0; y < numRows; y++) {
			final int fixedY = y;
			if (y != 0) {
				addComponent(new DynamicTextButton("/\\", EditProps.BUTTON, EditProps.HOVER, () -> {
					for (int x = 0; x < 9; x++) {
						CustomSlot oldUpper = slots[x][fixedY - 1];
						slots[x][fixedY - 1] = slots[x][fixedY];
						slots[x][fixedY] = oldUpper;
					}
					clearComponents();
					addComponents();
				}), 0.9f, 0.95f - 0.15f * y, 0.95f, 1f - 0.15f * y);
			}
			if (y != numRows - 1) {
				addComponent(new DynamicTextButton("\\/", EditProps.BUTTON, EditProps.HOVER, () -> {
					for (int x = 0; x < 9; x++) {
						CustomSlot oldUpper = slots[x][fixedY];
						slots[x][fixedY] = slots[x][fixedY + 1];
						slots[x][fixedY + 1] = oldUpper;
					}
					clearComponents();
					addComponents();
				}), 0.9f, 0.85f - 0.15f * y, 0.95f, 0.9f - 0.15f * y);
			}
			if (slots[0].length > 0) {
				addComponent(new DynamicTextButton("X", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					CustomSlot[][] newSlots = new CustomSlot[9][numRows - 1];
					for (int x = 0; x < 9; x++) {
						for (int newY = 0; newY < fixedY; newY++) {
							newSlots[x][newY] = slots[x][newY];
						}
						for (int newY = fixedY; newY < numRows - 1; newY++) {
							newSlots[x][newY] = slots[x][newY + 1];
						}
					}
					setSlots(newSlots);
				}), 0.9f, 0.9f - 0.15f * y, 0.95f, 0.95f - 0.15f * y);
			}
			
			// Add a + button for each row, to insert a new empty row below it
			// But at most 6 rows are allowed
			if (numRows < 6) {
				addComponent(new DynamicTextButton("+", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
					CustomSlot[][] newSlots = new CustomSlot[9][numRows + 1];
					for (int x = 0; x < 9; x++) {
						for (int newY = 0; newY <= fixedY; newY++) {
							newSlots[x][newY] = slots[x][newY];
						}
						newSlots[x][fixedY + 1] = new EmptyCustomSlot();
						for (int oldY = fixedY + 1; oldY < numRows; oldY++) {
							newSlots[x][oldY + 1] = slots[x][oldY];
						}
					}
					setSlots(newSlots);
				}), 0.95f, 0.825f - 0.15f * y, 1f, 0.875f - 0.15f * y);
			}
		}
		
		// The upper + button, to insert a new empty first row
		if (numRows < 6) {
			addComponent(new DynamicTextButton("+", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
				CustomSlot[][] newSlots = new CustomSlot[9][numRows + 1];
				for (int x = 0; x < 9; x++) {
					for (int oldY = 0; oldY < numRows; oldY++) {
						newSlots[x][oldY + 1] = slots[x][oldY];
					}
					newSlots[x][0] = new EmptyCustomSlot();
				}
				setSlots(newSlots);
			}), 0.95f, 0.95f, 1f, 1f);
		}
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	private void setSlots(CustomSlot[][] newSlots) {
		slots = newSlots;
		clearComponents();
		addComponents();
	}
}
