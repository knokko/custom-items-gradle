package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.recipe.ingredient.EditIngredient;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.CustomItemType.Category;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditItemTool<V extends CustomToolValues> extends EditItemBase<V> {

	public EditItemTool(EditMenu menu, V oldValues, ItemReference toModify) {
		super(menu, oldValues, toModify);
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

		DynamicTextButton[] pChangeRepairItemButton = { null };
		pChangeRepairItemButton[0] = new DynamicTextButton(
				currentValues.getRepairItem().toString("None"), BUTTON, HOVER, () -> {
					state.getWindow().setMainComponent(new EditIngredient(
							this, newRepairItem -> {
								currentValues.setRepairItem(newRepairItem);
								pChangeRepairItemButton[0].setText(newRepairItem.toString("None"));
					}, currentValues.getRepairItem(), true, menu.getSet()
					));
				}
		);
		addComponent(pChangeRepairItemButton[0], 0.85f, 0.575f, 0.99f, 0.65f);
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