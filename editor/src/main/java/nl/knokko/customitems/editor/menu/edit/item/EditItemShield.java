package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.attack.effect.AttackEffectGroupCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.item.model.EditItemModel;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.KciShield;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditItemShield extends EditItemTool<KciShield> {
	
	public EditItemShield(ItemSet itemSet, GuiComponent returnMenu, KciShield oldValues, ItemReference toModify) {
		super(itemSet, returnMenu, oldValues, toModify);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(
				new DynamicTextComponent("Required damage to lose durability:", LABEL),
				0.5f, 0.325f, 0.84f, 0.4f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getThresholdDamage(), 0.0, EDIT_BASE, EDIT_ACTIVE, currentValues::setThresholdDamage),
				0.85f, 0.325f, 0.95f, 0.4f
		);
		
		addComponent(
				new DynamicTextComponent("Blocking model: ", LABEL),
				0.65f, 0.25f, 0.84f, 0.325f
		);
		addComponent(new DynamicTextButton("Change...", 
				EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditItemModel(
					currentValues.getBlockingModel(), currentValues::setBlockingModel, currentValues.getName(),
					currentValues.getTextureReference() != null ? currentValues.getTexture().getName() : "TEXTURE_NAME",
					DefaultModelType.SHIELD_BLOCKING, false, this
			));
		}), 0.85f, 0.25f, 0.95f, 0.325f);

		addComponent(new DynamicTextComponent("Blocking effects:", LABEL), 0.65f, 0.15f, 0.84f, 0.25f);
		addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new AttackEffectGroupCollectionEdit(
					currentValues.getBlockingEffects(), currentValues::setBlockingEffects, true, this, itemSet
			));
		}), 0.85f, 0.15f, 0.95f, 0.225f);

		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/shield.html");
	}
}
