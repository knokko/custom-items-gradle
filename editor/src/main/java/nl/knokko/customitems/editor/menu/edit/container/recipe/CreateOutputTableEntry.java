package nl.knokko.customitems.editor.menu.edit.container.recipe;

import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.result.ChooseResult;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class CreateOutputTableEntry extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Consumer<OutputTableValues.Entry> onCreate;
	private final ItemSet set;
	
	public CreateOutputTableEntry(
			GuiComponent returnMenu, Consumer<OutputTableValues.Entry> onCreate, ItemSet set) {
		this.returnMenu = returnMenu;
		this.onCreate = onCreate;
		this.set = set;
	}

	@Override
	protected void addComponents() {
		
		DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		addComponent(errorComponent, 0.1f, 0.9f, 0.9f, 1f);
		
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.7f, 0.15f, 0.8f);
		
		addComponent(new DynamicTextComponent("Chance: ", EditProps.LABEL), 0.2f, 0.6f, 0.3f, 0.7f);
		IntEditField chanceField = new IntEditField(50, 1, 100, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		addComponent(chanceField, 0.3f, 0.6f, 0.4f, 0.7f);
		addComponent(new DynamicTextComponent("%", EditProps.LABEL), 0.4f, 0.6f, 0.42f, 0.7f);
		
		addComponent(new DynamicTextComponent("Item: ", EditProps.LABEL), 0.2f, 0.4f, 0.3f, 0.5f);
		ResultValues[] pResult = {null};
		addComponent(new DynamicTextButton("Choose...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new ChooseResult(
					this, newResult -> pResult[0] = newResult, set
			));
		}), 0.3f, 0.4f, 0.45f, 0.5f);
		
		addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			if (pResult[0] == null) {
				errorComponent.setText("You need to choose an item");
				return;
			}
			
			Option.Int chance = chanceField.getInt();
			if (!chance.hasValue()) {
				errorComponent.setText("The chance must be an integer between 1 and 100");
				return;
			}

			OutputTableValues.Entry entry = OutputTableValues.Entry.createQuick(pResult[0], chance.getValue());
			String error = Validation.toErrorString(() -> entry.validate(set));
			if (error == null) {
				onCreate.accept(entry);
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.1f, 0.15f, 0.2f);
		
		HelpButtons.addHelpLink(this, "edit menu/recipes/output table entry.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
