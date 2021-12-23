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
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.customitems.item.SimpleCustomItemValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EditItemSimple extends EditItemBase<SimpleCustomItemValues> {
	
	private static final AttributeModifierValues EXAMPLE_MODIFIER = AttributeModifierValues.createQuick(
			AttributeModifierValues.Attribute.ATTACK_DAMAGE,
			AttributeModifierValues.Slot.MAINHAND,
			AttributeModifierValues.Operation.ADD,
			5.0
	);

	public EditItemSimple(EditMenu menu, SimpleCustomItemValues oldValues, ItemReference toModify) {
		super(menu, oldValues, toModify);
	}
	
	@Override
	protected AttributeModifierValues getExampleAttributeModifier() {
		return EXAMPLE_MODIFIER;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(
				new DynamicTextComponent("Max stacksize:", EditProps.LABEL),
				0.71f, 0.35f, 0.895f, 0.45f
		);
		addComponent(
				new EagerIntEditField(
						currentValues.getMaxStacksize(), 1, 64, EDIT_BASE, EDIT_ACTIVE,
						newStacksize -> currentValues.setMaxStacksize((byte) newStacksize)
				),
				0.9f, 0.35f, 0.975f, 0.45f
		);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/simple.html");
	}

	@Override
	protected Category getCategory() {
		return Category.DEFAULT;
	}
}