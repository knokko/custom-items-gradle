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
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.ChooseIngredient;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EditItemTool extends EditItemBase {

	private final CustomToolValues currentValues;

	public EditItemTool(EditMenu menu, CustomToolValues oldValues, ItemReference toModify) {
		super(menu, oldValues, toModify);
		this.currentValues = oldValues.copy(true);
	}
	
	@Override
	protected AttributeModifierValues getExampleAttributeModifier() {
		double attackDamage = CustomItemDamage.getDefaultAttackDamage(currentValues.getItemType());
		return AttributeModifierValues.createQuick(
				AttributeModifierValues.Attribute.ATTACK_DAMAGE,
				AttributeModifierValues.Slot.MAINHAND,
				AttributeModifierValues.Operation.ADD,
				attackDamage
		);
	}

	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(
				new CheckboxComponent(currentValues.allowEnchanting(), currentValues::setAllowEnchanting),
				0.75f, 0.8f, 0.775f, 0.825f
		);
		addComponent(
				new DynamicTextComponent("Allow enchanting", EditProps.LABEL),
				0.8f, 0.8f, 0.95f, 0.875f
		);
		addComponent(
				new CheckboxComponent(currentValues.allowAnvilActions(), currentValues::setAllowAnvilActions),
				0.75f, 0.725f, 0.775f, 0.75f
		);
		addComponent(
				new DynamicTextComponent("Allow anvil actions", EditProps.LABEL),
				0.8f, 0.725f, 0.95f, 0.8f
		);
		addComponent(
				new EagerIntEditField(
						currentValues.getMaxDurabilityNew() == null ? -1 : currentValues.getMaxDurabilityNew(),
						-1,
						EDIT_BASE,
						EDIT_ACTIVE,
						newMaxDurability -> {
							if (newMaxDurability == -1) {
								currentValues.setMaxDurabilityNew(null);
							} else {
								currentValues.setMaxDurabilityNew((long) newMaxDurability);
							}
						}
				),
				0.85f, 0.65f, 0.925f, 0.725f
		);
		addComponent(
				new DynamicTextComponent("Max uses: ", EditProps.LABEL),
				0.71f, 0.65f, 0.84f, 0.725f
		);
		addComponent(
				new DynamicTextComponent("Repair item: ", EditProps.LABEL),
				0.71f, 0.575f, 0.84f, 0.65f
		);
		addComponent(
				new ChooseIngredient(this, currentValues::setRepairItem, true, menu.getSet()),
				0.85f, 0.575f, 0.99f, 0.65f
		);
		addComponent(
				new DynamicTextComponent("Durability loss on attack:", EditProps.LABEL),
				0.55f, 0.5f, 0.84f, 0.575f
		);
		addComponent(
				new EagerIntEditField(currentValues.getEntityHitDurabilityLoss(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setEntityHitDurabilityLoss),
				0.85f, 0.5f, 0.9f, 0.575f
		);
		addComponent(
				new DynamicTextComponent("Durability loss on block break:", EditProps.LABEL),
				0.55f, 0.425f, 0.84f, 0.5f
		);
		addComponent(
				new EagerIntEditField(currentValues.getBlockBreakDurabilityLoss(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setBlockBreakDurabilityLoss),
				0.85f, 0.425f, 0.9f, 0.5f
		);
		if (currentValues.getItemType().getMainCategory() == Category.SWORD) {
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

	@Override
	protected Category getCategory() {
		return currentValues.getItemType().getMainCategory();
	}
}