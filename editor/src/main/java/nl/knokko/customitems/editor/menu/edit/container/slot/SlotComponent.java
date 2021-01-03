package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import java.util.function.Supplier;

import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.DecorationCustomSlot;
import nl.knokko.customitems.container.slot.EmptyCustomSlot;
import nl.knokko.customitems.container.slot.FuelCustomSlot;
import nl.knokko.customitems.container.slot.FuelIndicatorCustomSlot;
import nl.knokko.customitems.container.slot.InputCustomSlot;
import nl.knokko.customitems.container.slot.OutputCustomSlot;
import nl.knokko.customitems.container.slot.ProgressIndicatorCustomSlot;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.keycode.KeyCode;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.TextBuilder;

public class SlotComponent implements GuiComponent {
	
	private final GuiComponent outerMenu;
	private final ItemSet set;
	private final Iterable<CustomSlot> allSlots;
	private CustomSlot currentSlot;
	private final Consumer<CustomSlot> changeSlot;
	private final Supplier<CustomSlot> getSlotToPaste;
	private final Consumer<CustomSlot> copySlot;
	
	private GuiTexture topTextTexture;
	private GuiTexture bottomTextTexture;
	
	private GuiComponentState state;
	
	public SlotComponent(GuiComponent outerMenu, ItemSet set, Iterable<CustomSlot> allSlots,
			CustomSlot slot, Consumer<CustomSlot> changeSlot,
			Supplier<CustomSlot> getSlotToPaste, Consumer<CustomSlot> copySlot) {
		this.outerMenu = outerMenu;
		this.set = set;
		this.allSlots = allSlots;
		this.currentSlot = slot;
		this.changeSlot = changeSlot;
		this.getSlotToPaste = getSlotToPaste;
		this.copySlot = copySlot;
	}
	
	private void setSlot(CustomSlot newSlot) {
		this.currentSlot = newSlot;
		this.changeSlot.accept(newSlot);
		String topText;
		String bottomText;
		if (newSlot instanceof DecorationCustomSlot) {
			DecorationCustomSlot decorationSlot = (DecorationCustomSlot) newSlot;
			topText = "decoration";
			bottomText = decorationSlot.getDisplay().toString();
		} else if (newSlot instanceof EmptyCustomSlot) {
			topText = "empty";
			bottomText = "";
		} else if (newSlot instanceof FuelCustomSlot) {
			FuelCustomSlot fuelSlot = (FuelCustomSlot) newSlot;
			topText = "fuel " + fuelSlot.getName();
			bottomText = fuelSlot.getRegistry().getName();
		} else if (newSlot instanceof FuelIndicatorCustomSlot) {
			FuelIndicatorCustomSlot indicatorSlot = (FuelIndicatorCustomSlot) newSlot;
			topText = "fuel ind. " + indicatorSlot.getFuelSlotName() + " " 
					+ indicatorSlot.getDomain().getBegin() + "% to "
					+ indicatorSlot.getDomain().getEnd() + "%";
			bottomText = indicatorSlot.getDisplay().toString();
		} else if (newSlot instanceof InputCustomSlot) {
			InputCustomSlot inputSlot = (InputCustomSlot) newSlot;
			topText = "input";
			bottomText = inputSlot.getName();
		} else if (newSlot instanceof OutputCustomSlot) {
			OutputCustomSlot outputSlot = (OutputCustomSlot) newSlot;
			topText = "output";
			bottomText = outputSlot.getName();
		} else if (newSlot instanceof ProgressIndicatorCustomSlot) {
			ProgressIndicatorCustomSlot indicatorSlot = (ProgressIndicatorCustomSlot) newSlot;
			topText = "progress ind. " + indicatorSlot.getDomain().getBegin() + "% to "
					+ indicatorSlot.getDomain().getEnd() + "%";
			bottomText = indicatorSlot.getDisplay().toString();
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
				CustomSlot maybeNewSlot = getSlotToPaste.get();
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
