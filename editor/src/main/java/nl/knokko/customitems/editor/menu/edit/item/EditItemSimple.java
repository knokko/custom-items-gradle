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