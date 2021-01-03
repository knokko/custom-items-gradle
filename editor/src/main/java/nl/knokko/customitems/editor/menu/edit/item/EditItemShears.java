package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomShears;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditItemShears extends EditItemTool {
	
	private static final AttributeModifier EXAMPLE_ATTRIBUTE_MODIFIER = new AttributeModifier(Attribute.MOVEMENT_SPEED, Slot.OFFHAND, Operation.ADD_FACTOR, 1.5);

	private final CustomShears toModify;
	
	private final IntEditField shearDurabilityLoss;

	public EditItemShears(EditMenu menu, CustomShears oldValues, CustomShears toModify) {
		super(menu, oldValues, toModify, Category.SHEAR);
		this.toModify = toModify;
		if (oldValues == null) {
			shearDurabilityLoss = new IntEditField(1, 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		} else {
			shearDurabilityLoss = new IntEditField(oldValues.getShearDurabilityLoss(), 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
	}
	
	@Override
	protected AttributeModifier getExampleAttributeModifier() {
		return EXAMPLE_ATTRIBUTE_MODIFIER;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextComponent("Durability loss on shearing:", EditProps.LABEL), 0.55f, 0.35f, 0.84f, 0.425f);
		addComponent(shearDurabilityLoss, 0.85f, 0.35f, 0.9f, 0.425f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/shear.html");
	}
	
	@Override
	protected String create(
			long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			float attackRange) {
		Option.Int durLoss = shearDurabilityLoss.getInt();
		if (!durLoss.hasValue())
			return "The shear durability loss must be a positive integer";
		return menu.getSet().addShears(new CustomShears(
				nameField.getText(), aliasField.getText(), getDisplayName(),
				lore, attributes, enchantments, maxUses, allowEnchanting.isChecked(),
				allowAnvil.isChecked(), repairItem.getIngredient(), 
				textureSelect.getSelected(), itemFlags,
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				durLoss.getValue(), customModel, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, attackRange), true
		);
	}
	
	@Override
	protected String apply(
			long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			float attackRange) {
		Option.Int durLoss = shearDurabilityLoss.getInt();
		if (!durLoss.hasValue())
			return "The shear durability loss must be a positive integer";
		return menu.getSet().changeShears(
				toModify, aliasField.getText(), getDisplayName(), lore, attributes, 
				enchantments, allowEnchanting.isChecked(), allowAnvil.isChecked(), 
				repairItem.getIngredient(), maxUses, textureSelect.getSelected(),
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				durLoss.getValue(), customModel, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, attackRange, true
		);
	}
}