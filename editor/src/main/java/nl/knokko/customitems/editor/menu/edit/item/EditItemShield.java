package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomShield;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditItemShield extends EditItemTool {
	
	private final CustomShield toModify;
	
	private final FloatEditField thresholdField;
	
	private byte[] customBlockingModel;

	public EditItemShield(EditMenu menu, CustomShield oldValues, CustomShield toModify) {
		super(menu, oldValues, toModify, Category.SHIELD);
		this.toModify = toModify;
		if (oldValues == null) {
			thresholdField = new FloatEditField(4.0, 0.0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		} else {
			thresholdField = new FloatEditField(oldValues.getThresholdDamage(), 0.0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextComponent("Required damage to lose durability:", 
				EditProps.LABEL), 0.5f, 0.325f, 0.84f, 0.4f);
		addComponent(thresholdField, 0.85f, 0.325f, 0.95f, 0.425f);
		
		addComponent(new DynamicTextComponent("Blocking model: ", EditProps.LABEL), 
				0.65f, 0.25f, 0.84f, 0.325f);
		addComponent(new DynamicTextButton("Change...", 
				EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow()
					.setMainComponent(new EditCustomModel(ItemSet.getDefaultModelBlockingShield(textureSelect.getSelected() != null ? textureSelect.getSelected().getName() : "TEXTURE_NAME"), this, (byte[] array) -> {
								customBlockingModel = array;
							}, customBlockingModel));
		}), 0.85f, 0.25f, 0.95f, 0.325f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/shield.html");
	}
	
	@Override
	protected String create(
			long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			float attackRange) {
		Option.Double thresholdDamage = thresholdField.getDouble();
		if (!thresholdDamage.hasValue())
			return "The required damage must be a positive number";
		return menu.getSet().addShield(new CustomShield(
				nameField.getText(), aliasField.getText(), getDisplayName(), lore, 
				attributes, enchantments, maxUses, allowEnchanting.isChecked(),
				allowAnvil.isChecked(), repairItem.getIngredient(), 
				textureSelect.getSelected(), itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, thresholdDamage.getValue(), customModel, 
				customBlockingModel, playerEffects, targetEffects, equippedEffects,
				commands, conditions, op, extraNbt, attackRange), true
		);
	}
	
	@Override
	protected String apply(
			long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			float attackRange) {
		Option.Double thresholdDamage = thresholdField.getDouble();
		if (!thresholdDamage.hasValue())
			return "The required damage must be a positive number";
		return menu.getSet().changeShield(
				toModify, aliasField.getText(), getDisplayName(), lore, attributes, 
				enchantments, allowEnchanting.isChecked(), allowAnvil.isChecked(), 
				repairItem.getIngredient(), maxUses, textureSelect.getSelected(),
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				thresholdDamage.getValue(), customModel, customBlockingModel, 
				playerEffects, targetEffects, equippedEffects, commands, 
				conditions, op, extraNbt, attackRange, true
		);
	}
}
