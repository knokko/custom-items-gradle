package nl.knokko.customitems.editor.menu.edit.container.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import nl.knokko.customitems.container.slot.display.*;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectDataVanillaItem;
import nl.knokko.customitems.editor.menu.edit.select.item.SelectSimpleVanillaItem;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.menu.TextListEditMenu;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class CreateDisplay extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Consumer<SlotDisplayValues> setDisplay;
	private final boolean selectAmount;
	private final Iterable<ItemReference> customItems;
	private final DynamicTextComponent errorComponent;
	
	public CreateDisplay(GuiComponent returnMenu, Consumer<SlotDisplayValues> setDisplay,
			boolean selectAmount, Iterable<ItemReference> customItems) {
		this.returnMenu = returnMenu;
		this.setDisplay = setDisplay;
		this.selectAmount = selectAmount;
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
		}), 0.025f, 0.7f, 0.2f, 0.8f);
		
		TextEditField displayNameField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		addComponent(new DynamicTextComponent("Display name:", EditProps.LABEL), 0.25f, 0.7f, 0.4f, 0.75f);
		addComponent(displayNameField, 0.425f, 0.7f, 0.575f, 0.75f);
		
		List<List<String>> pLore = new ArrayList<>(1);
		pLore.add(new ArrayList<>());
		addComponent(new DynamicTextButton("Lore...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new TextListEditMenu(this, newLore -> pLore.set(0, newLore),
					EditProps.BACKGROUND, EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, 
					EditProps.SAVE_BASE, EditProps.SAVE_HOVER, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, pLore.get(0)
			));
		}), 0.25f, 0.625f, 0.35f, 0.675f);
		
		IntEditField amountField = new IntEditField(1, 1, 64, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		if (selectAmount) {
			addComponent(new DynamicTextComponent("Amount:", EditProps.LABEL), 0.25f, 0.55f, 0.35f, 0.6f);
			addComponent(amountField, 0.375f, 0.55f, 0.425f, 0.6f);
		}
		
		SlotDisplayItemValues[] pDisplayItem = { null };
		DynamicTextComponent selectedItemDisplay = new DynamicTextComponent("", EditProps.LABEL);
		addComponent(new DynamicTextComponent("Choose item:", EditProps.LABEL), 0.6f, 0.7f, 0.75f, 0.8f);
		addComponent(selectedItemDisplay, 0.775f, 0.7f, 0.975f, 0.75f);
		addComponent(new DynamicTextButton("Custom item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new CollectionSelect<>(
					customItems, selectedItem -> {
						selectedItemDisplay.setText(selectedItem.get().getName());
						pDisplayItem[0] = CustomDisplayItemValues.createQuick(selectedItem);
					}, candidate -> true, selectedItem -> selectedItem.get().getName(), this));
		}), 0.6f, 0.6f, 0.75f, 0.65f);
		addComponent(new DynamicTextButton("Simple vanilla item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectSimpleVanillaItem(this, chosenMaterial -> {
				selectedItemDisplay.setText(chosenMaterial.toString());
				pDisplayItem[0] = SimpleVanillaDisplayItemValues.createQuick(chosenMaterial);
			}, false));
		}), 0.6f, 0.525f, 0.85f, 0.575f);
		addComponent(new DynamicTextButton("Data vanilla item", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new SelectDataVanillaItem(this, (chosenMaterial, chosenData) -> {
				selectedItemDisplay.setText(chosenMaterial + " [" + chosenData + "]");
				pDisplayItem[0] = DataVanillaDisplayItemValues.createQuick(chosenMaterial, chosenData);
			}));
		}), 0.6f, 0.45f, 0.8f, 0.5f);
		
		addComponent(new DynamicTextButton("Done", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			
			Option.Int amount = amountField.getInt();
			if (!amount.hasValue()) {
				errorComponent.setText("The amount must be an integer between 1 and 64");
				return;
			}
			
			if (pDisplayItem[0] == null) {
				errorComponent.setText("You must choose an item");
				return;
			}
			
			setDisplay.accept(SlotDisplayValues.createQuick(pDisplayItem[0], displayNameField.getText(), pLore.get(0), amount.getValue()));
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.2f, 0.15f, 0.3f);
		HelpButtons.addHelpLink(this, "edit menu/containers/slots/display.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
