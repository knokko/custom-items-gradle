package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.KciAttributeModifier.Attribute;
import nl.knokko.customitems.item.KciAttributeModifier.Operation;
import nl.knokko.customitems.item.KciAttributeModifier.Slot;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.Kci3dHelmet;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.itemset.ItemReference;

public class EditItemHelmet3D extends EditItemArmor<Kci3dHelmet> {

	public EditItemHelmet3D(EditMenu menu, Kci3dHelmet oldValues, ItemReference toModify) {
		/*
		 * Minecrafts (interesting) resourcepack design allows any item to have
		 * a custom model when in the head/helmet slot, except helmets. Thus, 3d
		 * helmet custom items can't use helmets as internal item type, so it should
		 * instead be something like a HOE.
		 * 
		 * Because hoes normally can't be put in the helmet slot, the plug-in will
		 * use some dirty event handling to bypass this restriction.
		 */
		super(menu, oldValues, toModify);
	}

	@Override
	protected KciAttributeModifier getExampleAttributeModifier() {
		return KciAttributeModifier.createQuick(
				Attribute.ARMOR, Slot.HEAD, Operation.ADD, 6
		);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/helmet3d.html");
	}

	@Override
	protected KciItemType.Category getCategory() {
		return KciItemType.Category.DEFAULT;
	}
}
