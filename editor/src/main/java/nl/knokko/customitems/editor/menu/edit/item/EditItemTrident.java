package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.item.model.EditItemModel;
import nl.knokko.customitems.editor.resourcepack.DefaultItemModels;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CustomTridentValues;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EditItemTrident extends EditItemTool<CustomTridentValues> {
	
	public EditItemTrident(EditMenu menu, CustomTridentValues oldValues, ItemReference toModify) {
		super(menu, oldValues, toModify);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(
				new DynamicTextComponent("Durability loss on throwing:", EditProps.LABEL),
				0.55f, 0.35f, 0.84f, 0.425f
		);
		addComponent(
				new EagerIntEditField(currentValues.getThrowDurabilityLoss(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setThrowDurabilityLoss),
				0.85f, 0.35f, 0.9f, 0.425f
		);
		addComponent(
				new DynamicTextComponent("Throw damage multiplier:", EditProps.LABEL),
				0.6f, 0.275f, 0.84f, 0.35f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getThrowDamageMultiplier(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setThrowDamageMultiplier),
				0.85f, 0.275f, 0.9f, 0.35f
		);
		addComponent(
				new DynamicTextComponent("Throw speed multiplier:", EditProps.LABEL),
				0.6f, 0.2f, 0.84f, 0.275f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getThrowSpeedMultiplier(), -1000f, EDIT_BASE, EDIT_ACTIVE, currentValues::setThrowSpeedMultiplier),
				0.85f, 0.2f, 0.9f, 0.275f
		);
		
		addComponent(
				new DynamicTextComponent("In-hand model: ", EditProps.LABEL),
				0.68f, 0.125f, 0.84f, 0.2f
		);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemModel(
					currentValues.getInHandModel(), currentValues::setInHandModel, currentValues.getName(),
					currentValues.getTextureReference() != null ? currentValues.getTexture().getName() : "TEXTURE_NAME",
					DefaultModelType.TRIDENT_IN_HAND, false, this
			));
		}), 0.85f, 0.125f, 0.95f, 0.2f);
		addComponent(
				new DynamicTextComponent("Throwing model: ", EditProps.LABEL),
				0.65f, 0.05f, 0.84f, 0.125f
		);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemModel(
					currentValues.getThrowingModel(), currentValues::setThrowingModel, currentValues.getName(),
					currentValues.getTextureReference() != null ? currentValues.getTexture().getName() : "TEXTURE_NAME",
					DefaultModelType.TRIDENT_THROWING, false, this
			));
		}), 0.85f, 0.05f, 0.95f, 0.125f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/trident.html");
	}
}
