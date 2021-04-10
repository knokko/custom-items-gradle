/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.IngredientComponent;
import nl.knokko.customitems.editor.set.item.CustomTool;
import nl.knokko.customitems.editor.set.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CustomItemDamage;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.customitems.item.CustomToolDurability;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditItemTool extends EditItemBase {

	private final CustomTool toModify;
	private final Category category;

	protected final CheckboxComponent allowEnchanting;
	protected final CheckboxComponent allowAnvil;
	protected final IntEditField durability;
	protected final IngredientComponent repairItem;
	protected final IntEditField entityHitDurabilityLoss;
	protected final IntEditField blockBreakDurabilityLoss;

	public EditItemTool(EditMenu menu, CustomTool oldValues, CustomTool toModify, Category toolCategory) {
		super(menu, oldValues, toModify, toolCategory);
		this.toModify = toModify;
		category = toolCategory;
		if (oldValues != null) {
			allowEnchanting = new CheckboxComponent(oldValues.allowEnchanting());
			allowAnvil = new CheckboxComponent(oldValues.allowAnvilActions());
			repairItem = new IngredientComponent("None", oldValues.getRepairItem(), this, menu.getSet());
			durability = new IntEditField(oldValues.getDurability(), CustomTool.UNBREAKABLE_TOOL_DURABILITY, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			entityHitDurabilityLoss = new IntEditField(oldValues.getEntityHitDurabilityLoss(), 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			blockBreakDurabilityLoss = new IntEditField(oldValues.getBlockBreakDurabilityLoss(), 0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		} else {
			allowEnchanting = new CheckboxComponent(true);
			allowAnvil = new CheckboxComponent(true);
			repairItem = new IngredientComponent("None", new NoIngredient(), this, menu.getSet());
			durability = new IntEditField(500, CustomTool.UNBREAKABLE_TOOL_DURABILITY, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			CustomItemType preferredType;
			if (toolCategory == Category.SWORD)
				preferredType = CustomItemType.IRON_SWORD;
			else if (toolCategory == Category.PICKAXE)
				preferredType = CustomItemType.IRON_PICKAXE;
			else if (toolCategory == Category.AXE)
				preferredType = CustomItemType.IRON_AXE;
			else if (toolCategory == Category.SHOVEL)
				preferredType = CustomItemType.IRON_SHOVEL;
			else if (toolCategory == Category.HOE)
				preferredType = CustomItemType.IRON_HOE;
			else if (toolCategory == Category.SHEAR)
				preferredType = CustomItemType.SHEARS;
			else if (toolCategory == Category.BOW)
				preferredType = CustomItemType.BOW;
			else if (toolCategory == Category.CROSSBOW)
				preferredType = CustomItemType.CROSSBOW;
			else if (toolCategory == Category.HELMET)
				preferredType = CustomItemType.IRON_HELMET;
			else if (toolCategory == Category.CHESTPLATE)
				preferredType = CustomItemType.IRON_CHESTPLATE;
			else if (toolCategory == Category.LEGGINGS)
				preferredType = CustomItemType.IRON_LEGGINGS;
			else if (toolCategory == Category.BOOTS)
				preferredType = CustomItemType.IRON_BOOTS;
			else if (toolCategory == Category.SHIELD)
				preferredType = CustomItemType.SHIELD;
			else if (toolCategory == Category.TRIDENT)
				preferredType = CustomItemType.TRIDENT;
			else
				throw new Error("Unsupported category for EditItemTool: " + toolCategory);
			internalType = preferredType;
			entityHitDurabilityLoss = new IntEditField(
					CustomToolDurability.defaultEntityHitDurabilityLoss(preferredType), 0, 
					EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
			blockBreakDurabilityLoss = new IntEditField(
					CustomToolDurability.defaultBlockBreakDurabilityLoss(preferredType), 0, 
					EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
	}
	
	@Override
	protected AttributeModifier getExampleAttributeModifier() {
		double attackDamage = CustomItemDamage.getDefaultAttackDamage(internalType);
		return new AttributeModifier(Attribute.ATTACK_DAMAGE, Slot.MAINHAND, Operation.ADD, attackDamage);
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		//internalType.setText(internalType.currentType.toString());
		addComponent(allowEnchanting, 0.75f, 0.8f, 0.775f, 0.825f);
		addComponent(new DynamicTextComponent("Allow enchanting", EditProps.LABEL), 0.8f, 0.8f, 0.95f, 0.875f);
		addComponent(allowAnvil, 0.75f, 0.725f, 0.775f, 0.75f);
		addComponent(new DynamicTextComponent("Allow anvil actions", EditProps.LABEL), 0.8f, 0.725f, 0.95f, 0.8f);
		addComponent(durability, 0.85f, 0.65f, 0.925f, 0.725f);
		addComponent(new DynamicTextComponent("Max uses: ", EditProps.LABEL), 0.71f, 0.65f, 0.84f, 0.725f);
		addComponent(new DynamicTextComponent("Repair item: ", EditProps.LABEL), 0.71f, 0.575f, 0.84f, 0.65f);
		addComponent(repairItem, 0.85f, 0.575f, 0.99f, 0.65f);
		addComponent(new DynamicTextComponent("Durability loss on attack:", EditProps.LABEL), 0.55f, 0.5f, 0.84f, 0.575f);
		addComponent(entityHitDurabilityLoss, 0.85f, 0.5f, 0.9f, 0.575f);
		addComponent(new DynamicTextComponent("Durability loss on block break:", EditProps.LABEL), 0.55f, 0.425f, 0.84f, 0.5f);
		addComponent(blockBreakDurabilityLoss, 0.85f, 0.425f, 0.9f, 0.5f);
		if (category == Category.SWORD) {
			errorComponent.setProperties(EditProps.LABEL);
			errorComponent.setText("Hint: Use attribute modifiers to set the damage this sword will deal.");
		} else {
			errorComponent.setProperties(EditProps.LABEL);
			errorComponent.setText("Hint: Set the 'Max uses' to -1 to make it unbreakable.");
		}
		
		// Subclasses have their own help menu
		if (getClass() == EditItemTool.class) {
			HelpButtons.addHelpLink(this, "edit%20menu/items/edit/tool.html");
		}
	}
	
	protected String create(
			long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			float attackRange) {
		return menu.getSet().addTool(new CustomTool(
				internalType, nameField.getText(), aliasField.getText(), 
				getDisplayName(), lore, attributes, enchantments, maxUses, 
				allowEnchanting.isChecked(), allowAnvil.isChecked(), 
				repairItem.getIngredient(), textureSelect.getSelected(), 
				itemFlags, entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				customModel, playerEffects, targetEffects, equippedEffects,
				commands, conditions, op, extraNbt, attackRange), true
		);
	}

	@Override
	protected String create(float attackRange) {
		Option.Long maybeDurability = this.durability.getLong();
		if (!maybeDurability.hasValue()) return "The durability must be an integer";
		long durability = maybeDurability.getValue();
		if (durability <= 0 && durability != CustomTool.UNBREAKABLE_TOOL_DURABILITY)
			return "The durability should be a positive integer or " + CustomTool.UNBREAKABLE_TOOL_DURABILITY + " to make it unbreakable";
		Option.Int entityHit = entityHitDurabilityLoss.getInt();
		if (!entityHit.hasValue()) return "The entity hit durability loss should be a positive integer";
		Option.Int blockBreak = blockBreakDurabilityLoss.getInt();
		if (!blockBreak.hasValue()) return "The block break durability loss should be a positive integer";
		return create(durability, entityHit.getValue(), blockBreak.getValue(), attackRange);
	}
	
	protected String apply(
			long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			float attackRange) {
		return menu.getSet().changeTool(
				toModify, internalType, aliasField.getText(), getDisplayName(), 
				lore, attributes, enchantments, allowEnchanting.isChecked(),
				allowAnvil.isChecked(), repairItem.getIngredient(), maxUses, 
				textureSelect.getSelected(), itemFlags, entityHitDurabilityLoss, 
				blockBreakDurabilityLoss, customModel, playerEffects, targetEffects, 
				equippedEffects, commands, conditions, op, extraNbt, attackRange, true
		);
	}

	@Override
	protected String apply(float attackRange) {
		Option.Long maybeDurability = this.durability.getLong();
		if (!maybeDurability.hasValue()) return "The durability must be an integer";
		long durability = maybeDurability.getValue();
		if (durability <= 0 && durability != CustomTool.UNBREAKABLE_TOOL_DURABILITY)
			return "The durability should be a positive integer or " + CustomTool.UNBREAKABLE_TOOL_DURABILITY + " to make it unbreakable";
		Option.Int entityHit = entityHitDurabilityLoss.getInt();
		if (!entityHit.hasValue()) return "The entity hit durability loss should be a positive integer";
		Option.Int blockBreak = blockBreakDurabilityLoss.getInt();
		if (!blockBreak.hasValue()) return "The block break durability loss should be a positive integer";
		return apply(durability, entityHit.getValue(), blockBreak.getValue(), attackRange);
	}

	@Override
	protected Category getCategory() {
		return category;
	}
}