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

import nl.knokko.customitems.damage.DamageResistances;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomArmor;
import nl.knokko.customitems.editor.set.item.texture.ArmorTextures;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.ReadOnlyReference;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.ConditionalTextComponent;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditItemArmor extends EditItemTool {
	
	private static final int DEFAULT_RED = 160;
	private static final int DEFAULT_GREEN = 101;
	private static final int DEFAULT_BLUE = 64;
	
	protected final CustomArmor toModify;
	
	private final ColorEditField red;
	private final ColorEditField green;
	private final ColorEditField blue;
	
	protected DamageResistances damageResistances;
	protected ReadOnlyReference<ArmorTextures> wornTexture;

	public EditItemArmor(EditMenu menu, CustomArmor oldValues, CustomArmor toModify, Category toolCategory) {
		super(menu, oldValues, toModify, toolCategory);
		this.toModify = toModify;
		if (oldValues == null) {
			red = new ColorEditField(DEFAULT_RED);
			green = new ColorEditField(DEFAULT_GREEN);
			blue = new ColorEditField(DEFAULT_BLUE);
			damageResistances = new DamageResistances();
			// This is null by default and optional
			wornTexture = null;
		} else {
			red = new ColorEditField(oldValues.getRed());
			green = new ColorEditField(oldValues.getGreen());
			blue = new ColorEditField(oldValues.getBlue());
			damageResistances = oldValues.getDamageResistances();
			wornTexture = oldValues.getWornTexture();
		}
	}
	
	@Override
	protected AttributeModifier getExampleAttributeModifier() {
		double armor;
		Slot slot;
		CustomItemType i = internalType;
		if (i == CustomItemType.NETHERITE_HELMET) {
			armor = 3;
			slot = Slot.HEAD;
		} else if (i == CustomItemType.NETHERITE_CHESTPLATE) {
			armor = 8;
			slot = Slot.CHEST;
		} else if (i == CustomItemType.NETHERITE_LEGGINGS) {
			armor = 6;
			slot = Slot.LEGS;
		} else if (i == CustomItemType.NETHERITE_BOOTS) {
			armor = 3;
			slot = Slot.FEET;
		} else if (i == CustomItemType.DIAMOND_HELMET) {
			armor = 3;
			slot = Slot.HEAD;
		} else if (i == CustomItemType.DIAMOND_CHESTPLATE) {
			armor = 8;
			slot = Slot.CHEST;
		} else if (i == CustomItemType.DIAMOND_LEGGINGS) {
			armor = 6;
			slot = Slot.LEGS;
		} else if (i == CustomItemType.DIAMOND_BOOTS) {
			armor = 3;
			slot = Slot.FEET;
		} else if (i == CustomItemType.IRON_HELMET) {
			armor = 2;
			slot = Slot.HEAD;
		} else if (i == CustomItemType.IRON_CHESTPLATE) {
			armor = 6;
			slot = Slot.CHEST;
		} else if (i == CustomItemType.IRON_LEGGINGS) {
			armor = 5;
			slot = Slot.LEGS;
		} else if (i == CustomItemType.IRON_BOOTS) {
			armor = 2;
			slot = Slot.FEET;
		} else if (i == CustomItemType.CHAINMAIL_HELMET) {
			armor = 2;
			slot = Slot.HEAD;
		} else if (i == CustomItemType.CHAINMAIL_CHESTPLATE) {
			armor = 5;
			slot = Slot.CHEST;
		} else if (i == CustomItemType.CHAINMAIL_LEGGINGS) {
			armor = 4;
			slot = Slot.LEGS;
		} else if (i == CustomItemType.CHAINMAIL_BOOTS) {
			armor = 1;
			slot = Slot.FEET;
		} else if (i == CustomItemType.GOLD_HELMET) {
			armor = 2;
			slot = Slot.HEAD;
		} else if (i == CustomItemType.GOLD_CHESTPLATE) {
			armor = 5;
			slot = Slot.CHEST;
		} else if (i == CustomItemType.GOLD_LEGGINGS) {
			armor = 3;
			slot = Slot.LEGS;
		} else if (i == CustomItemType.GOLD_BOOTS) {
			armor = 1;
			slot = Slot.FEET;
		} else if (i == CustomItemType.LEATHER_HELMET) {
			armor = 1;
			slot = Slot.HEAD;
		} else if (i == CustomItemType.LEATHER_CHESTPLATE) {
			armor = 3;
			slot = Slot.CHEST;
		} else if (i == CustomItemType.LEATHER_LEGGINGS) {
			armor = 2;
			slot = Slot.LEGS;
		} else if (i == CustomItemType.LEATHER_BOOTS) {
			armor = 1;
			slot = Slot.FEET;
		} else {
			throw new IllegalArgumentException("Unknown item type: " + i.name());
		}
		
		return new AttributeModifier(Attribute.ARMOR, slot, Operation.ADD, armor);
	}
	
	private int getColorValue(ColorEditField field, int defaultValue) {
		if (field.isActive()) {
			Option.Int value = field.getComponent().getInt();
			if (value.hasValue()) {
				return value.getValue();
			} else {
				return -1;
			}
		} else {
			return defaultValue;
		}
	}
	
	@Override
	protected String create(
			long maxUses, int entityHitDurabilityLoss, int blockBreakDurabilityLoss,
			float attackRange) {
		int redValue = getColorValue(red, DEFAULT_RED);
		if (redValue == -1) return "The red must be an integer at least 0 and at most 255";
		int greenValue = getColorValue(green, DEFAULT_GREEN);
		if (greenValue == -1) return "The green must be an integer at least 0 and at most 255";
		int blueValue = getColorValue(blue, DEFAULT_BLUE);
		if (blueValue == -1) return "The blue must be an integer at least 0 and at most 255";
		if (internalType.isLeatherArmor()) {
			wornTexture = null;
		}
		return menu.getSet().addArmor(new CustomArmor(
				internalType, nameField.getText(), aliasField.getText(),
				getDisplayName(), lore, attributes, enchantments, maxUses, 
				allowEnchanting.isChecked(), allowAnvil.isChecked(), 
				repairItem.getIngredient(), textureSelect.getSelected(),
				redValue, greenValue, blueValue, itemFlags, 
				entityHitDurabilityLoss, blockBreakDurabilityLoss, 
				damageResistances, customModel, playerEffects, 
				targetEffects, equippedEffects,
				commands, conditions, op, extraNbt, wornTexture, attackRange), true
		);
	}
	
	@Override
	protected String apply(
			long maxUses, int entityHit, int blockBreak, float attackRange) {
		int redValue = getColorValue(red, DEFAULT_RED);
		if (redValue == -1) return "The red must be an integer at least 0 and at most 255";
		int greenValue = getColorValue(green, DEFAULT_GREEN);
		if (greenValue == -1) return "The green must be an integer at least 0 and at most 255";
		int blueValue = getColorValue(blue, DEFAULT_BLUE);
		if (blueValue == -1) return "The blue must be an integer at least 0 and at most 255";
		if (internalType.isLeatherArmor()) {
			wornTexture = null;
		}
		return menu.getSet().changeArmor(
				toModify, internalType, aliasField.getText(), getDisplayName(), 
				lore, attributes, enchantments, allowEnchanting.isChecked(),
				allowAnvil.isChecked(), repairItem.getIngredient(), maxUses, 
				textureSelect.getSelected(), redValue, greenValue, blueValue, 
				itemFlags, entityHit, blockBreak, damageResistances,
				customModel, playerEffects, targetEffects, equippedEffects,
				commands, conditions, op, extraNbt, wornTexture, attackRange, true
		);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextComponent("Damage resistances: ", EditProps.LABEL), 0.62f, 0.35f, 0.84f, 0.425f);
		addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
			state.getWindow().setMainComponent(new EditDamageResistances(damageResistances, () -> {
				state.getWindow().setMainComponent(this);
			}, (DamageResistances newResistances) -> {
				state.getWindow().setMainComponent(this);
				damageResistances = newResistances;
			}));
		}), 0.85f, 0.35f, 0.99f, 0.425f);
		if (!(this instanceof EditItemHelmet3D)) {
			addComponent(new ConditionalTextComponent(
					"Worn texture:", EditProps.LABEL, () -> !showColors()), 
					0.65f, 0.29f, 0.84f, 0.35f);
			addComponent(new ConditionalTextButton(
					"Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
						state.getWindow().setMainComponent(new SelectWornTexture(
								this, menu.getSet(), 
								newWornTexture -> wornTexture = newWornTexture
						));
					}, () -> !showColors()), 0.85f, 0.29f, 0.99f, 0.35f);
			addComponent(new ConditionalTextComponent("Red: ", EditProps.LABEL, () -> {return showColors();}), 0.78f, 0.29f, 0.84f, 0.35f);
			addComponent(new ConditionalTextComponent("Green: ", EditProps.LABEL, () -> {return showColors();}), 0.75f, 0.21f, 0.84f, 0.27f);
			addComponent(new ConditionalTextComponent("Blue: ", EditProps.LABEL, () -> {return showColors();}), 0.77f, 0.13f, 0.84f, 0.19f);
			addComponent(red, 0.85f, 0.28f, 0.9f, 0.35f);
			addComponent(green, 0.85f, 0.20f, 0.9f, 0.27f);
			addComponent(blue, 0.85f, 0.12f, 0.9f, 0.19f);
		}
		errorComponent.setProperties(EditProps.LABEL);
		errorComponent.setText("Hint: Use attribute modifiers to set the armor (toughness) of this piece.");
		
		// 3d helmets are a bit different
		if (!(this instanceof EditItemHelmet3D)) {
			HelpButtons.addHelpLink(this, "edit%20menu/items/edit/armor.html");
		}
	}
	
	private boolean showColors() {
		return internalType.isLeatherArmor();
	}
	
	private class ColorEditField extends WrapperComponent<IntEditField> {

		public ColorEditField(int initial) {
			super(new IntEditField(initial, 0, 255, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE));
		}
		
		@Override
		public boolean isActive() {
			return showColors();
		}
	}
}