package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.function.Consumer;

import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.container.slot.ProgressIndicatorSlotValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class CreateCraftingIndicatorSlot extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Consumer<ContainerSlotValues> submitSlot;
	private final Iterable<ItemReference> customItems;
	private final DynamicTextComponent errorComponent;
	
	public CreateCraftingIndicatorSlot(GuiComponent returnMenu, Consumer<ContainerSlotValues> submitSlot,
			Iterable<ItemReference> customItems) {
		this.returnMenu = returnMenu;
		this.submitSlot = submitSlot;
		this.customItems = customItems;
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);
		
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.175f, 0.8f);
		
		SlotDisplayValues[] pDisplays = { null, null };
		addComponent(new DynamicTextButton("Display...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateDisplay(this, 
					newDisplay -> pDisplays[0] = newDisplay, false, customItems)
			);
		}), 0.25f, 0.625f, 0.4f, 0.675f);
		addComponent(new DynamicTextButton("Placeholder...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CreateDisplay(this,
					newPlaceholder -> pDisplays[1] = newPlaceholder, true, customItems)
			);
		}), 0.25f, 0.55f, 0.4f, 0.6f);
		
		IntEditField beginField = new IntEditField(0, 0, 100, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		IntEditField endField = new IntEditField(100, 0, 100, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		addComponent(new DynamicTextComponent("Indication domain:", EditProps.LABEL), 0.25f, 0.475f, 0.4f, 0.525f);
		addComponent(beginField, 0.425f, 0.475f, 0.475f, 0.525f);
		addComponent(new DynamicTextComponent("% to ", EditProps.LABEL), 0.475f, 0.475f, 0.525f, 0.525f);
		addComponent(endField, 0.525f, 0.475f, 0.575f, 0.525f);
		addComponent(new DynamicTextComponent("%", EditProps.LABEL), 0.575f, 0.475f, 0.6f, 0.525f);
		
		addComponent(new DynamicTextButton("Done", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			
			if (pDisplays[0] == null) {
				errorComponent.setText("You need to choose a display");
				return;
			}
			if (pDisplays[1] == null) {
				errorComponent.setText("You need to choose a placeholder");
				return;
			}
			
			Option.Int begin = beginField.getInt();
			if (!begin.hasValue()) {
				errorComponent.setText("The domain start must be an integer between 0 and 100");
				return;
			}
			
			Option.Int end = endField.getInt();
			if (!end.hasValue()) {
				errorComponent.setText("The domain end must be an integer between 0 and 100");
				return;
			}
			
			if (begin.getValue() > end.getValue()) {
				errorComponent.setText("The start of the domain must be smaller than the end");
				return;
			}
			
			submitSlot.accept(ProgressIndicatorSlotValues.createQuick(
					pDisplays[0], pDisplays[1],
					new IndicatorDomain(begin.getValue(), end.getValue())
			));
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.2f, 0.15f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/slots/crafting progress indicator.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
