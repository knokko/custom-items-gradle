package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.resourcepack.DefaultItemModels;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CustomShieldValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EditItemShield extends EditItemTool<CustomShieldValues> {
	
	public EditItemShield(EditMenu menu, CustomShieldValues oldValues, ItemReference toModify) {
		super(menu, oldValues, toModify);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(
				new DynamicTextComponent("Required damage to lose durability:", EditProps.LABEL),
				0.5f, 0.325f, 0.84f, 0.4f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getThresholdDamage(), 0.0, EDIT_BASE, EDIT_ACTIVE, currentValues::setThresholdDamage),
				0.85f, 0.325f, 0.95f, 0.425f
		);
		
		addComponent(
				new DynamicTextComponent("Blocking model: ", EditProps.LABEL),
				0.65f, 0.25f, 0.84f, 0.325f
		);
		addComponent(new DynamicTextButton("Change...", 
				EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditCustomModel(
					DefaultItemModels.getDefaultModelBlockingShield(
							currentValues.getTextureReference() != null ? currentValues.getTexture().getName() : "TEXTURE_NAME"),
					this, currentValues::setCustomBlockingModel, currentValues.getCustomBlockingModel()
			));
		}), 0.85f, 0.25f, 0.95f, 0.325f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/shield.html");
	}
}
