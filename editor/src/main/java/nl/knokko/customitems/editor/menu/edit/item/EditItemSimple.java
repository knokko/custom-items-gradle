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
import nl.knokko.customitems.editor.set.item.SimpleCustomItem;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditItemSimple extends EditItemBase {
	
	private static final AttributeModifier EXAMPLE_MODIFIER = new AttributeModifier(Attribute.ATTACK_DAMAGE, Slot.MAINHAND, Operation.ADD, 5.0);
	
	private final SimpleCustomItem toModify;
	
	private final IntEditField maxStacksize;

	public EditItemSimple(EditMenu menu, SimpleCustomItem oldValues, SimpleCustomItem toModify) {
		super(menu, oldValues, toModify, Category.DEFAULT);
		this.toModify = toModify;
		if (oldValues == null) {
			maxStacksize = new IntEditField(64, 1, 64, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		} else {
			maxStacksize = new IntEditField(oldValues.getMaxStacksize(), 1, 64, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
		}
	}
	
	@Override
	protected AttributeModifier getExampleAttributeModifier() {
		return EXAMPLE_MODIFIER;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(new DynamicTextComponent("Max stacksize:", EditProps.LABEL), 0.71f, 0.35f, 0.895f, 0.45f);
		addComponent(maxStacksize, 0.9f, 0.35f, 0.975f, 0.45f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/simple.html");
	}

	@Override
	protected String create(float attackRange) {
		Option.Int stackSize = maxStacksize.getInt();
		if (!stackSize.hasValue()) return "The max stacksize should be an integer at least 1 and at most 64";
		return menu.getSet().addSimpleItem(new SimpleCustomItem(
				internalType, nameField.getText(), aliasField.getText(),
				getDisplayName(), lore, attributes, enchantments, 
				stackSize.getValue(), textureSelect.getSelected(), itemFlags, 
				customModel, playerEffects, targetEffects, equippedEffects,
				commands, conditions, op, extraNbt, attackRange
		));
	}

	@Override
	protected String apply(float attackRange) {
		Option.Int stackSize = maxStacksize.getInt();
		if (!stackSize.hasValue()) return "The max stacksize should be an integer at least 1 and at most 64";
		return menu.getSet().changeSimpleItem(
				toModify, internalType, aliasField.getText(), getDisplayName(), lore, 
				attributes, enchantments, textureSelect.getSelected(), 
				stackSize.getValue(), itemFlags, customModel, playerEffects, 
				targetEffects, equippedEffects, commands, conditions, op, 
				extraNbt, attackRange, true
		);
	}

	@Override
	protected Category getCategory() {
		return Category.DEFAULT;
	}
}