package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import java.util.function.Supplier;

import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;

public class SlotComponent implements GuiComponent {
	
	private final GuiComponent outerMenu;
	private final SItemSet set;
	private final Iterable<ContainerSlotValues> allSlots;
	private ContainerSlotValues currentSlot;
	private final Consumer<ContainerSlotValues> changeSlot;
	private final Supplier<ContainerSlotValues> getSlotToPaste;
	private final Consumer<ContainerSlotValues> copySlot;
	
	private GuiTexture topTextTexture;
	private GuiTexture bottomTextTexture;
	
	private GuiComponentState state;
	
	public SlotComponent(GuiComponent outerMenu, SItemSet set, Iterable<ContainerSlotValues> allSlots,
			ContainerSlotValues slot, Consumer<ContainerSlotValues> changeSlot,
			Supplier<ContainerSlotValues> getSlotToPaste, Consumer<ContainerSlotValues> copySlot) {
		this.outerMenu = outerMenu;
		this.set = set;
		this.allSlots = allSlots;
		this.currentSlot = slot;
		this.changeSlot = changeSlot;
		this.getSlotToPaste = getSlotToPaste;
		this.copySlot = copySlot;
	}
	
	private void setSlot(ContainerSlotValues newSlot) {
		this.currentSlot = newSlot;
		this.changeSlot.accept(newSlot);
		String topText;
		String bottomText;
		if (newSlot instanceof DecorationSlotValues) {
			DecorationSlotValues decorationSlot = (DecorationSlotValues) newSlot;
			topText = "decoration";
			bottomText = decorationSlot.getDisplay().toString();
		} else if (newSlot instanceof EmptySlotValues) {
			topText = "empty";
			bottomText = "";
		} else if (newSlot instanceof FuelSlotValues) {
			FuelSlotValues fuelSlot = (FuelSlotValues) newSlot;
			topText = "fuel " + fuelSlot.getName();
			bottomText = fuelSlot.getFuelRegistry().getName();
		} else if (newSlot instanceof FuelIndicatorSlotValues) {
			FuelIndicatorSlotValues indicatorSlot = (FuelIndicatorSlotValues) newSlot;
			topText = "fuel ind. " + indicatorSlot.getFuelSlotName() + " "
					+ indicatorSlot.getIndicatorDomain().getBegin() + "% to "
					+ indicatorSlot.getIndicatorDomain().getEnd() + "%";
			bottomText = indicatorSlot.getDisplay().toString();
		} else if (newSlot instanceof InputSlotValues) {
			InputSlotValues inputSlot = (InputSlotValues) newSlot;
			topText = "input";
			bottomText = inputSlot.getName();
		} else if (newSlot instanceof OutputSlotValues) {
			OutputSlotValues outputSlot = (OutputSlotValues) newSlot;
			topText = "output";
			bottomText = outputSlot.getName();
		} else if (newSlot instanceof ProgressIndicatorSlotValues) {
			ProgressIndicatorSlotValues indicatorSlot = (ProgressIndicatorSlotValues) newSlot;
			topText = "progress ind. " + indicatorSlot.getIndicatorDomain().getBegin() + "% to "
					+ indicatorSlot.getIndicatorDomain().getEnd() + "%";
			bottomText = indicatorSlot.getDisplay().toString();
		} else if (newSlot instanceof StorageSlotValues) {
			StorageSlotValues storageSlot = (StorageSlotValues)	newSlot;
			topText = "storage";
			SlotDisplayValues placeHolder = storageSlot.getPlaceholder();
			if (placeHolder != null) {
				bottomText = placeHolder.toString();
			} else {
				bottomText = "";
			}
		} else {
			throw new Error("Unknown custom slot class: " + newSlot.getClass());
		}
		
		int maxLength = 15;
		if (topText.length() > maxLength) {
			topText = topText.substring(0, maxLength);
		}
		if (bottomText.length() > maxLength) {
			bottomText = bottomText.substring(0, maxLength);
		}
		
		BufferedImage topTextImage = TextBuilder.createTexture(topText, EditProps.LABEL);
		BufferedImage bottomTextImage = TextBuilder.createTexture(bottomText, EditProps.LABEL);
		topTextTexture = state.getWindow().getTextureLoader().loadTexture(topTextImage);
		bottomTextTexture = state.getWindow().getTextureLoader().loadTexture(bottomTextImage);
		state.getWindow().markChange();
	}

	@Override
	public void init() {
		setSlot(currentSlot);
	}

	@Override
	public void setState(GuiComponentState state) {
		this.state = state;
	}

	@Override
	public GuiComponentState getState() {
		return state;
	}

	@Override
	public void update() {}

	@Override
	public void render(GuiRenderer renderer) {
		renderer.fill(state.isMouseOver() ? SimpleGuiColor.WHITE : EditProps.BACKGROUND2, 0.05f, 0.05f, 0.95f, 0.95f);
		renderer.renderTexture(topTextTexture, 0.1f, 0.6f, 0.9f, 0.9f);
		renderer.renderTexture(bottomTextTexture, 0.1f, 0.2f, 0.9f, 0.5f);
	}

	@Override
	public void click(float x, float y, int button) {
		state.getWindow().setMainComponent(new CreateSlot(outerMenu, 
				this::setSlot, set, allSlots, currentSlot
		));
	}

	@Override
	public void clickOut(int button) {}

	@Override
	public boolean scroll(float amount) {
		return false;
	}

	@Override
	public void keyPressed(int keyCode) {
		if (state.isMouseOver()) {
			if (keyCode == KeyCode.KEY_C) {
				copySlot.accept(currentSlot);
			} else if (keyCode == KeyCode.KEY_P) {
				ContainerSlotValues maybeNewSlot = getSlotToPaste.get();
				if (maybeNewSlot != null) {
					setSlot(maybeNewSlot);
				}
			}
		}
	}

	@Override
	public void keyPressed(char character) {}

	@Override
	public void keyReleased(int keyCode) {}
}
