package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.StringLength;
import nl.knokko.customitems.itemset.ItemSet;
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
	private final KciContainer container;
	private final int x, y;
	private final Supplier<ContainerSlot> getSlotToPaste;
	private final Consumer<ContainerSlot> copySlot;
	
	private GuiTexture topTextTexture;
	private GuiTexture bottomTextTexture;
	
	private GuiComponentState state;
	
	public SlotComponent(
			GuiComponent outerMenu, ItemSet set, KciContainer container, int x, int y,
			Supplier<ContainerSlot> getSlotToPaste, Consumer<ContainerSlot> copySlot) {
		this.outerMenu = outerMenu;
		this.set = set;
		this.container = container;
		this.x = x;
		this.y = y;
		this.getSlotToPaste = getSlotToPaste;
		this.copySlot = copySlot;
	}
	
	private void setSlot(ContainerSlot newSlot) {
		this.container.setSlot(this.x, this.y, newSlot);
		String topText;
		String bottomText;
		if (newSlot instanceof EnergyIndicatorSlot) {
			EnergyIndicatorSlot indicatorSlot = (EnergyIndicatorSlot) newSlot;
			topText = "energy ind. ";
			bottomText = indicatorSlot.getEnergyType().getName();
		} else if (newSlot instanceof DecorationSlot) {
			DecorationSlot decorationSlot = (DecorationSlot) newSlot;
			topText = "decoration";
			bottomText = decorationSlot.getDisplay().toString();
		} else if (newSlot instanceof LinkSlot) {
			LinkSlot linkSlot = (LinkSlot) newSlot;
			topText = "link";
			bottomText = linkSlot.getLinkedContainer().getName();
		} else if (newSlot instanceof ActionSlot) {
			ActionSlot actionSlot = (ActionSlot) newSlot;
			topText = "script action";
			bottomText = actionSlot.getActionID();
		} else if (newSlot instanceof EmptySlot) {
			topText = "empty";
			bottomText = "";
		} else if (newSlot instanceof FuelSlot) {
			FuelSlot fuelSlot = (FuelSlot) newSlot;
			topText = "fuel " + fuelSlot.getName();
			bottomText = fuelSlot.getFuelRegistry().getName();
		} else if (newSlot instanceof FuelIndicatorSlot) {
			FuelIndicatorSlot indicatorSlot = (FuelIndicatorSlot) newSlot;
			topText = "fuel ind.";
			bottomText = indicatorSlot.getFuelSlotName();
		} else if (newSlot instanceof InputSlot) {
			InputSlot inputSlot = (InputSlot) newSlot;
			topText = "input";
			bottomText = inputSlot.getName();
		} else if (newSlot instanceof OutputSlot) {
			OutputSlot outputSlot = (OutputSlot) newSlot;
			topText = "output";
			bottomText = outputSlot.getName();
		} else if (newSlot instanceof ProgressIndicatorSlot) {
			ProgressIndicatorSlot indicatorSlot = (ProgressIndicatorSlot) newSlot;
			topText = "progress ind. " + indicatorSlot.getIndicatorDomain().getBegin() + "% to "
					+ indicatorSlot.getIndicatorDomain().getEnd() + "%";
			bottomText = indicatorSlot.getDisplay().toString();
		} else if (newSlot instanceof StorageSlot) {
			StorageSlot storageSlot = (StorageSlot)	newSlot;
			topText = "storage";
			SlotDisplay placeHolder = storageSlot.getPlaceholder();
			if (placeHolder != null) {
				bottomText = placeHolder.toString();
			} else {
				bottomText = "";
			}
		} else if (newSlot instanceof ManualOutputSlot) {
			ManualOutputSlot outputSlot = (ManualOutputSlot) newSlot;
			topText = "manual output";
			bottomText = outputSlot.getName();
		} else {
			throw new Error("Unknown custom slot class: " + newSlot.getClass());
		}
		
		int maxLength = 15;
		topText = StringLength.fixLength(topText, maxLength);
		bottomText = StringLength.fixLength(bottomText, maxLength);

		BufferedImage topTextImage = TextBuilder.createTexture(topText, EditProps.LABEL);
		BufferedImage bottomTextImage = TextBuilder.createTexture(bottomText, EditProps.LABEL);
		topTextTexture = state.getWindow().getTextureLoader().loadTexture(topTextImage);
		bottomTextTexture = state.getWindow().getTextureLoader().loadTexture(bottomTextImage);
		state.getWindow().markChange();
	}

	@Override
	public void init() {
		setSlot(this.container.getSlot(x, y));
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
	public void click(float clickX, float clickY, int button) {
		Collection<ContainerSlot> otherSlots = new ArrayList<>(this.container.getWidth() * this.container.getHeight() - 1);
		for (int slotX = 0; slotX < this.container.getWidth(); slotX++) {
			for (int slotY = 0; slotY < this.container.getHeight(); slotY++) {
				if (slotX != this.x || slotY != this.y) {
					otherSlots.add(this.container.getSlot(slotX, slotY));
				}
			}
		}
		state.getWindow().setMainComponent(new CreateSlot(
				outerMenu, set, otherSlots, this::setSlot
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
				copySlot.accept(this.container.getSlot(this.x, this.y));
			} else if (keyCode == KeyCode.KEY_P) {
				ContainerSlot maybeNewSlot = getSlotToPaste.get();
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
