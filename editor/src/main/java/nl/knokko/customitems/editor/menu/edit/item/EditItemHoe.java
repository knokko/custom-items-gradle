package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.KciAttributeModifier.Attribute;
import nl.knokko.customitems.item.KciAttributeModifier.Operation;
import nl.knokko.customitems.item.KciAttributeModifier.Slot;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.KciHoe;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_ACTIVE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.EDIT_BASE;

public class EditItemHoe extends EditItemTool<KciHoe> {
	
	private static final KciAttributeModifier EXAMPLE_ATTRIBUTE_MODIFIER = KciAttributeModifier.createQuick(
			Attribute.MOVEMENT_SPEED, Slot.OFFHAND, Operation.ADD_FACTOR, 1.5
	);

	public EditItemHoe(ItemSet itemSet, GuiComponent returnMenu, KciHoe oldValues, ItemReference toModify) {
		super(itemSet, returnMenu, oldValues, toModify);
	}
	
	@Override
	protected KciAttributeModifier getExampleAttributeModifier() {
		return EXAMPLE_ATTRIBUTE_MODIFIER;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(
				new DynamicTextComponent("Durability loss on tilling:", EditProps.LABEL),
				0.55f, 0.35f, 0.84f, 0.425f
		);
		addComponent(
				new EagerIntEditField(currentValues.getTillDurabilityLoss(), 0, EDIT_BASE, EDIT_ACTIVE, currentValues::setTillDurabilityLoss),
				0.85f, 0.35f, 0.9f, 0.425f
		);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/hoe.html");
	}
}
