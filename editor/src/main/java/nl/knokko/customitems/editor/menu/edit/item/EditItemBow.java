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
import nl.knokko.customitems.editor.menu.edit.texture.BowTextureEdit;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.CustomBowValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.BowTextureValues;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EditItemBow extends EditItemTool<CustomBowValues> {
	
	private static final AttributeModifierValues EXAMPLE_ATTRIBUTE_MODIFIER = AttributeModifierValues.createQuick(
			AttributeModifierValues.Attribute.MOVEMENT_SPEED,
			AttributeModifierValues.Slot.OFFHAND,
			AttributeModifierValues.Operation.ADD_FACTOR,
			1.5
	);

	public EditItemBow(EditMenu menu, CustomBowValues oldValues, ItemReference toModify) {
		super(menu, oldValues, toModify);
	}

	@Override
	public boolean canHaveCustomModel() {
		return false;
	}

	@Override
	protected GuiComponent createLoadTextureMenu() {
		return new BowTextureEdit(menu.getSet(), this, null, new BowTextureValues(true));
	}

	@Override
	protected AttributeModifierValues getExampleAttributeModifier() {
		return EXAMPLE_ATTRIBUTE_MODIFIER;
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(
				new DynamicTextComponent("Durability loss on shooting:", EditProps.LABEL),
				0.55f, 0.35f, 0.84f, 0.425f
		);
		addComponent(
				new EagerIntEditField(currentValues.getShootDurabilityLoss(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setShootDurabilityLoss),
				0.85f, 0.35f, 0.9f, 0.425f
		);
		addComponent(
				new DynamicTextComponent("Damage multiplier: ", EditProps.LABEL),
				0.71f, 0.245f, 0.895f, 0.32f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getDamageMultiplier(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setDamageMultiplier),
				0.895f, 0.245f, 0.965f, 0.32f
		);
		addComponent(
				new DynamicTextComponent("Speed multiplier: ", EditProps.LABEL),
				0.71f, 0.17f, 0.88f, 0.245f
		);
		addComponent(
				new EagerFloatEditField(currentValues.getSpeedMultiplier(), -1000f, EDIT_BASE, EDIT_ACTIVE, currentValues::setSpeedMultiplier),
				0.895f, 0.17f, 0.965f, 0.245f
		);
		addComponent(
				new DynamicTextComponent("knockback strength: ", EditProps.LABEL),
				0.71f, 0.095f, 0.9f, 0.17f
		);
		addComponent(
				new EagerIntEditField(currentValues.getKnockbackStrength(), -1000, EDIT_BASE, EDIT_ACTIVE, currentValues::setKnockbackStrength),
				0.9f, 0.095f, 0.95f, 0.17f
		);
		addComponent(
				new DynamicTextComponent("Arrow gravity", EditProps.LABEL),
				0.8f, 0.02f, 0.95f, 0.095f
		);
		addComponent(
				new CheckboxComponent(currentValues.hasGravity(), currentValues::setGravity),
				0.75f, 0.02f, 0.775f, 0.045f
		);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/bow.html");
	}

	@Override
	protected boolean allowTexture(TextureReference texture) {
		return texture.get() instanceof BowTextureValues;
	}
}