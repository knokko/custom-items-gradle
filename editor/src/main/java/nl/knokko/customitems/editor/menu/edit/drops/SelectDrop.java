package nl.knokko.customitems.editor.menu.edit.drops;

import java.util.function.Consumer;

import nl.knokko.customitems.drops.DropValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.container.recipe.EditOutputTable;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class SelectDrop extends GuiMenu {
	
	private final ItemSet set;
	private final GuiComponent returnMenu;
	private final Consumer<DropValues> receiver;
	private final DropValues currentValues;
	
	private final DynamicTextComponent errorComponent;
	
	public SelectDrop(ItemSet set, GuiComponent returnMenu, DropValues previous, Consumer<DropValues> receiver) {
		this.set = set;
		this.returnMenu = returnMenu;
		this.receiver = receiver;
		this.currentValues = previous.copy(true);
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}
	
	@Override
	public void init() {
		super.init();
		errorComponent.setText("");
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
		
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		addComponent(new DynamicTextComponent("Items to drop:", LABEL), 0.3f, 0.7f, 0.5f, 0.8f);
		addComponent(new DynamicTextButton("Choose...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditOutputTable(
					this, currentValues.getOutputTable(), currentValues::setOutputTable, set, null
			));
		}), 0.55f, 0.7f, 0.7f, 0.8f);
		
		addComponent(new DynamicTextComponent("Item that must be held/used:", 
						LABEL), 0.3f, 0.55f, 0.65f, 0.65f
		);
		addComponent(new DynamicTextButton("Choose...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(
					new ChooseRequiredHeldItems(set, currentValues.getRequiredHeldItems(), currentValues::setRequiredHeldItems,
							this, "Players can use any item")
			);
		}), 0.7f, 0.55f, 0.85f, 0.65f);

		addComponent(
				new DynamicTextComponent("Allowed biomes:", LABEL),
				0.3f, 0.4f, 0.5f, 0.5f
		);
		addComponent(new DynamicTextButton("Choose...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new EditAllowedBiomes(
					currentValues.getAllowedBiomes(), currentValues::setAllowedBiomes, this
			));
		}), 0.55f, 0.4f, 0.65f, 0.5f);

		addComponent(
				new DynamicTextComponent("Prevent normal drops", LABEL),
				0.3f, 0.225f, 0.55f, 0.325f
		);
		addComponent(
				new CheckboxComponent(currentValues.shouldCancelNormalDrops(), currentValues::setCancelNormalDrops),
				0.25f, 0.225f, 0.275f, 0.25f
		);
		
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			String error = Validation.toErrorString(() -> currentValues.validate(set));
			if (error == null) {
				receiver.accept(currentValues);
				state.getWindow().setMainComponent(returnMenu);
			} else {
				errorComponent.setText(error);
			}
		}), 0.025f, 0.1f, 0.2f, 0.2f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/drops/drop.html");
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
