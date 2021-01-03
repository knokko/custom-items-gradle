package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.CustomTrident;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditItemTrident extends EditItemTool {
	
	private final CustomTrident toModify;
	
	private final IntEditField throwDurabilityLoss;
	
	private final FloatEditField throwDamageMultiplier;
	private final FloatEditField throwSpeedMultiplier;
	
	private byte[] customInHandModel;
	private byte[] customThrowingModel;

	public EditItemTrident(EditMenu menu, CustomTrident oldValues, CustomTrident toModify) {
		super(menu, oldValues, toModify, Category.TRIDENT);
		this.toModify = toModify;
		if (oldValues == null) {
			throwDurabilityLoss = new IntEditField(1, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			throwDamageMultiplier = new FloatEditField(1.0, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			throwSpeedMultiplier = new FloatEditField(1.0, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		} else {
			throwDurabilityLoss = new IntEditField(oldValues.throwDurabilityLoss, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			throwDamageMultiplier = new FloatEditField(oldValues.throwDamageMultiplier, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			throwSpeedMultiplier = new FloatEditField(oldValues.speedMultiplier, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			customInHandModel = oldValues.customInHandModel;
			customThrowingModel = oldValues.customThrowingModel;
		}
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextComponent("Durability loss on throwing:", EditProps.LABEL), 0.55f, 0.35f, 0.84f, 0.425f);
		addComponent(throwDurabilityLoss, 0.85f, 0.35f, 0.9f, 0.425f);
		addComponent(new DynamicTextComponent("Throw damage multiplier:", EditProps.LABEL), 0.6f, 0.275f, 0.84f, 0.35f);
		addComponent(throwDamageMultiplier, 0.85f, 0.275f, 0.9f, 0.35f);
		addComponent(new DynamicTextComponent("Throw speed multiplier:", EditProps.LABEL), 0.6f, 0.2f, 0.84f, 0.275f);
		addComponent(throwSpeedMultiplier, 0.85f, 0.2f, 0.9f, 0.275f);
		
		addComponent(new DynamicTextComponent("In-hand model: ", EditProps.LABEL), 0.68f, 0.125f, 0.84f, 0.2f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow()
					.setMainComponent(new EditCustomModel(ItemSet.getDefaultModelBlockingShield(textureSelect.getSelected() != null ? textureSelect.getSelected().getName() : "TEXTURE_NAME"), this, (byte[] array) -> {
								customInHandModel = array;
							}, customInHandModel));
		}), 0.85f, 0.125f, 0.95f, 0.2f);
		addComponent(new DynamicTextComponent("Throwing model: ", EditProps.LABEL), 0.65f, 0.05f, 0.84f, 0.125f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow()
					.setMainComponent(new EditCustomModel(ItemSet.getDefaultModelBlockingShield(textureSelect.getSelected() != null ? textureSelect.getSelected().getName() : "TEXTURE_NAME"), this, (byte[] array) -> {
								customThrowingModel = array;
							}, customThrowingModel));
		}), 0.85f, 0.05f, 0.95f, 0.125f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/trident.html");
	}
	
	@Override
	protected String create(
			long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			float attackRange) {
		Option.Int durLoss = throwDurabilityLoss.getInt();
		if (!durLoss.hasValue())
			return "The throw durability loss must be a positive integer";
		Option.Double damageMult = throwDamageMultiplier.getDouble();
		if (!damageMult.hasValue())
			return "The throw damage multiplier must be a positive number";
		Option.Double speedMult = throwSpeedMultiplier.getDouble();
		if (!speedMult.hasValue())
			return "The throw speed multiplier must be a positive number";
		return menu.getSet().addTrident(new CustomTrident(
				nameField.getText(), aliasField.getText(), getDisplayName(),
				lore, attributes, enchantments, maxUses, 
				allowEnchanting.isChecked(), allowAnvil.isChecked(), 
				damageMult.getValue(), speedMult.getValue(), 
				repairItem.getIngredient(), textureSelect.getSelected(), itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				durLoss.getValue(), customModel, customInHandModel, 
				customThrowingModel, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, 
				attackRange), true
		);
	}
	
	@Override
	protected String apply(
			long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			float attackRange) {
		Option.Int durLoss = throwDurabilityLoss.getInt();
		if (!durLoss.hasValue())
			return "The shear durability loss must be a positive integer";
		Option.Double damageMult = throwDamageMultiplier.getDouble();
		if (!damageMult.hasValue())
			return "The throw damage multiplier must be a positive number";
		Option.Double speedMult = throwSpeedMultiplier.getDouble();
		if (!speedMult.hasValue())
			return "The throw speed multiplier must be a positive number";
		return menu.getSet().changeTrident(
				toModify, internalType, aliasField.getText(), getDisplayName(), 
				lore, attributes, enchantments, allowEnchanting.isChecked(),
				allowAnvil.isChecked(), damageMult.getValue(), speedMult.getValue(), 
				repairItem.getIngredient(), maxUses, textureSelect.getSelected(), 
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				durLoss.getValue(), customModel, customInHandModel, 
				customThrowingModel, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, 
				attackRange, true
		);
	}
}