package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.QuickCollectionEdit;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.EnchantmentType;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class EnchantmentCollectionEdit extends QuickCollectionEdit<Enchantment> {

	public EnchantmentCollectionEdit(Collection<Enchantment> currentCollection,
			Consumer<Collection<Enchantment>> onApply, GuiComponent returnMenu) {
		super(currentCollection, onApply, returnMenu);
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		Enchantment original = ownCollection.get(itemIndex);
		addComponent(new ImageButton(deleteBase, deleteHover, () -> {
			removeItem(itemIndex);
		}), 0.3f, minY, 0.4f, maxY);
		addComponent(EnumSelect.createSelectButton(EnchantmentType.class, (EnchantmentType newType) -> {
			Enchantment previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new Enchantment(newType, previous.getLevel()));
		}, original.getType()), 0.45f, minY, 0.65f, maxY);
		addComponent(new DynamicTextComponent("Level: ", EditProps.LABEL), 0.7f, minY, 0.85f, maxY);
		addComponent(new EagerIntEditField(original.getLevel(), 0, 
				EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, newLevel -> {
			Enchantment previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new Enchantment(previous.getType(), newLevel));
		}), 0.86f, minY, 0.975f, maxY);
	}

	@Override
	protected Enchantment addNew() {
		return new Enchantment(EnchantmentType.DURABILITY, 2);
	}

	@Override
	protected String getHelpPage() {
		return "edit%20menu/items/edit/enchantments.html";
	}
}
