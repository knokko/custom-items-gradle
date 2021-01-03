package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.set.item.CustomArmor;
import nl.knokko.customitems.editor.set.item.CustomHelmet3D;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CustomItemType.Category;

public class EditItemHelmet3D extends EditItemArmor {

	public EditItemHelmet3D(EditMenu menu, CustomArmor oldValues, CustomArmor toModify) {
		/*
		 * Minecrafts (interesting) resourcepack design allows any item to have
		 * a custom model when in the head/helmet slot, except helmets. Thus, 3d
		 * helmet custom items can't use helmets as internal item type, so it should
		 * instead be something like a HOE.
		 * 
		 * Because hoes normally can't be put in the helmet slot, the plug-in will
		 * use some dirty event handling to bypass this restriction.
		 */
		super(menu, oldValues, toModify, Category.HOE);
	}

	@Override
	protected AttributeModifier getExampleAttributeModifier() {
		return new AttributeModifier(Attribute.ARMOR, Slot.HEAD, Operation.ADD, 6);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/helmet3d.html");
	}
	
	@Override
	protected String create(
			long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			float attackRange) {
		return menu.getSet().addHelmet3D(new CustomHelmet3D(
				internalType, nameField.getText(), aliasField.getText(), 
				getDisplayName(), lore, attributes, enchantments, maxUses, 
				allowEnchanting.isChecked(), allowAnvil.isChecked(), 
				repairItem.getIngredient(), textureSelect.getSelected(),
				itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, damageResistances, customModel, 
				playerEffects, targetEffects, equippedEffects,
				commands, conditions, op, extraNbt, attackRange), true
		);
	}
	
	@Override
	protected String apply(
			long maxUses, int entityHit, int blockBreak, float attackRange) {
		return menu.getSet().changeHelmet3D(
				(CustomHelmet3D) toModify, internalType, aliasField.getText(),
				getDisplayName(), lore, attributes, enchantments, 
				allowEnchanting.isChecked(), allowAnvil.isChecked(), 
				repairItem.getIngredient(), maxUses, textureSelect.getSelected(),
				itemFlags, entityHit, blockBreak, damageResistances,
				customModel, playerEffects, targetEffects, equippedEffects,
				commands, conditions, op, extraNbt, attackRange, true
		);
	}
}
