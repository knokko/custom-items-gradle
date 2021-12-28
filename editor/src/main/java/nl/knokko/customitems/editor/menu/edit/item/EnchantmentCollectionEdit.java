package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.item.EnchantmentType;
import nl.knokko.customitems.item.EnchantmentValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EnchantmentCollectionEdit extends InlineCollectionEdit<EnchantmentValues> {

	public EnchantmentCollectionEdit(Collection<EnchantmentValues> currentCollection,
			Consumer<Collection<EnchantmentValues>> onApply, GuiComponent returnMenu) {
		super(returnMenu, currentCollection, onApply);
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		EnchantmentValues enchantment = ownCollection.get(itemIndex);
		addComponent(new ImageButton(deleteBase, deleteHover, () -> {
			removeItem(itemIndex);
		}), 0.3f, minY, 0.4f, maxY);
		addComponent(
				EnumSelect.createSelectButton(EnchantmentType.class, enchantment::setType, enchantment.getType()),
				0.45f, minY, 0.65f, maxY
		);
		addComponent(new DynamicTextComponent("Level: ", LABEL), 0.7f, minY, 0.85f, maxY);
		addComponent(
				new EagerIntEditField(enchantment.getLevel(), 0, EDIT_BASE, EDIT_ACTIVE, enchantment::setLevel),
				0.86f, minY, 0.975f, maxY
		);
	}

	@Override
	protected EnchantmentValues addNew() {
		return EnchantmentValues.createQuick(EnchantmentType.DURABILITY, 2);
	}

	@Override
	protected String getHelpPage() {
		return "edit%20menu/items/edit/enchantments.html";
	}
}
