package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.function.Consumer;

import nl.knokko.customitems.container.slot.display.*;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ChooseDataVanillaResult;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.menu.TextListEditMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateDisplay extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final ItemSet itemSet;
	private final SlotDisplay currentValues;
	private final Consumer<SlotDisplay> setDisplay;
	private final boolean selectAmount;
	private final DynamicTextComponent errorComponent;
	
	public CreateDisplay(
			GuiComponent returnMenu, ItemSet itemSet,
			Consumer<SlotDisplay> setDisplay, boolean selectAmount
	) {
		this.returnMenu = returnMenu;
		this.itemSet = itemSet;
		this.currentValues = new SlotDisplay(true);
		this.setDisplay = setDisplay;
		this.selectAmount = selectAmount;
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
		
		addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		addComponent(
				new DynamicTextComponent("Display name:", LABEL),
				0.25f, 0.7f, 0.4f, 0.75f
		);
		addComponent(
				new EagerTextEditField(currentValues.getDisplayName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setDisplayName),
				0.425f, 0.7f, 0.575f, 0.75f
		);
		
		addComponent(new DynamicTextButton("Lore...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new TextListEditMenu(
					this, currentValues::setLore,
					BACKGROUND, CANCEL_BASE, CANCEL_HOVER, SAVE_BASE, SAVE_HOVER,
					EDIT_BASE, EDIT_ACTIVE, currentValues.getLore()
			));
		}), 0.25f, 0.625f, 0.35f, 0.675f);
		
		if (selectAmount) {
			addComponent(
					new DynamicTextComponent("Amount:", LABEL),
					0.25f, 0.55f, 0.35f, 0.6f
			);
			addComponent(
					new EagerIntEditField(currentValues.getAmount(), 0, 64, EDIT_BASE, EDIT_ACTIVE, currentValues::setAmount),
					0.375f, 0.55f, 0.425f, 0.6f
			);
		}

		DynamicTextComponent selectedItemDisplay = new DynamicTextComponent("", EditProps.LABEL);
		addComponent(
				new DynamicTextComponent("Choose item:", LABEL),
				0.6f, 0.7f, 0.75f, 0.8f
		);
		addComponent(
				selectedItemDisplay,
				0.775f, 0.7f, 0.975f, 0.75f
		);
		addComponent(new DynamicTextButton("Custom item", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new CollectionSelect<>(
					itemSet.items.references(), selectedItem -> {
						selectedItemDisplay.setText(selectedItem.get().getName());
						currentValues.setDisplayItem(CustomDisplayItem.createQuick(selectedItem));
					}, candidate -> true, selectedItem -> selectedItem.get().getName(), this, false
			));
		}), 0.6f, 0.6f, 0.75f, 0.65f);
		addComponent(new DynamicTextButton("Simple vanilla item", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EnumSelect<>(
					VMaterial.class, newMaterial -> {
						selectedItemDisplay.setText(newMaterial.toString());
						currentValues.setDisplayItem(SimpleVanillaDisplayItem.createQuick(newMaterial));
					}, candidate -> true, this
			));
		}), 0.6f, 0.525f, 0.85f, 0.575f);
		addComponent(new DynamicTextButton("Data vanilla item", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseDataVanillaResult(this, false, dataResult -> {
				selectedItemDisplay.setText(dataResult.getMaterial() + " [" + dataResult.getDataValue() + "]");
				currentValues.setDisplayItem(DataVanillaDisplayItem.createQuick(
						dataResult.getMaterial(), dataResult.getDataValue()
				));
				currentValues.setAmount(dataResult.getAmount());
			}));
		}), 0.6f, 0.45f, 0.8f, 0.5f);
		
		addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
			String error = Validation.toErrorString(() -> currentValues.validate(itemSet));
			if (error == null) {
				setDisplay.accept(currentValues);
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.2f, 0.15f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/slots/display.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
