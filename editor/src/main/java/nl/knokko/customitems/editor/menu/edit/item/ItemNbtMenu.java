package nl.knokko.customitems.editor.menu.edit.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.ExtraItemNbtValues;
import nl.knokko.customitems.item.nbt.NbtValueType;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ItemNbtMenu extends GuiMenu {

	private static final List<String> INITIAL_KEY = new ArrayList<>(1);

	static {
		INITIAL_KEY.add("");
	}

	private final ExtraItemNbtValues currentValues;
	private final Consumer<ExtraItemNbtValues> onApply;
	private final GuiComponent returnMenu;
	private final String currentName;
	private final boolean hasDurability;
	
	private final DynamicTextComponent errorComponent;
	
	public ItemNbtMenu(ExtraItemNbtValues oldNbt, Consumer<ExtraItemNbtValues> onApply,
			GuiComponent returnMenu, String currentName, boolean hasDurability) {
		this.currentValues = oldNbt.copy(true);
		this.onApply = onApply;
		this.returnMenu = returnMenu;
		this.currentName = !currentName.isEmpty() ? currentName : "%NAME%";
		this.hasDurability = hasDurability;
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
	}

	@Override
	protected void addComponents() {
		addComponent(errorComponent, 0f, 0.9f, 1f, 1f);
		addComponent(new DynamicTextComponent(
				"Custom items will always have the following nbt tag(s):", 
				EditProps.LABEL), 0.02f, 0.8f, 0.9f, 0.9f
		);
		addComponent(new DynamicTextComponent("KnokkosCustomItems:", 
				EditProps.LABEL), 0.02f, 0.7f, 0.5f, 0.8f);
		addComponent(new DynamicTextComponent("Name:", EditProps.LABEL), 
				0.5f, 0.6f, 0.6f, 0.7f);
		addComponent(new DynamicTextComponent(currentName, EditProps.LABEL),
				0.65f, 0.6f, 0.95f, 0.7f);
		if (hasDurability) {
			addComponent(new DynamicTextComponent("Durability:", EditProps.LABEL),
					0.5f, 0.5f, 0.7f, 0.6f);
			addComponent(new DynamicTextComponent("%REMAINING_DURABILITY%", 
					EditProps.LABEL), 0.75f, 0.5f, 1f, 0.6f);
		}
		
		addComponent(new DynamicTextComponent(
				"There are others as well, but these are not so interesting for users",
				EditProps.LABEL), 0.02f, 0.4f, 0.9f, 0.5f);
		addComponent(new DynamicTextComponent(
				"You can add more nbt tags below if you would like to:", 
				EditProps.LABEL), 0.02f, 0.3f, 0.7f, 0.4f);

		NewPairList pairList = new NewPairList();
		addComponent(new DynamicTextButton("Add integer",
				EditProps.BUTTON, EditProps.HOVER, () -> {

			pairList.addEntry(ExtraItemNbtValues.Entry.createQuick(INITIAL_KEY, new ExtraItemNbtValues.Value(1)));
			pairList.refresh();
		}), 0.1f, 0.25f, 0.3f, 0.3f);
		addComponent(new DynamicTextButton("Add string",
				EditProps.BUTTON, EditProps.HOVER, () -> {
			pairList.addEntry(ExtraItemNbtValues.Entry.createQuick(INITIAL_KEY, new ExtraItemNbtValues.Value("")));
			pairList.refresh();
		}), 0.35f, 0.25f, 0.55f, 0.3f);

		addComponent(pairList, 0f, 0f, 1f, 0.25f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/nbt.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}

	private class NewPairList extends InlineCollectionEdit<ExtraItemNbtValues.Entry> {

		NewPairList() {
			super(currentValues.getEntries(), newEntries -> {
				currentValues.setEntries(newEntries);
				ItemNbtMenu.this.onApply.accept(currentValues);
			}, ItemNbtMenu.this.returnMenu);
		}

		void addEntry(ExtraItemNbtValues.Entry newEntry) {
			ownCollection.add(newEntry);
		}

		@Override
		protected void refresh() {
			super.refresh();
		}

		@Override
		protected boolean showAddNewButton() {
			return false;
		}

		@Override
		protected void addRowComponents(int itemIndex, float minY, float maxY) {
			ExtraItemNbtValues.Entry entry = ownCollection.get(itemIndex);
			List<String> oldKey = entry.getKey();

			float stride = 0.16f;

			for (int keyIndex = 0; keyIndex < oldKey.size(); keyIndex++) {
				int rememberKeyIndex = keyIndex;
				addComponent(
						new EagerTextEditField(oldKey.get(keyIndex), EDIT_BASE, EDIT_ACTIVE, newText -> {
							List<String> currentKey = entry.getKey();
							currentKey.set(rememberKeyIndex, newText);
							entry.setKey(currentKey);
						}), 0.2f + stride * keyIndex, minY, 0.35f + stride * keyIndex, maxY
				);
			}

			addComponent(new DynamicTextButton("+",
					SAVE_BASE, SAVE_HOVER, () -> {
				List<String> newKey = entry.getKey();
				newKey.add("");
				entry.setKey(newKey);
				refresh();
			}), 0.21f + stride * oldKey.size(), minY, 0.26f + stride * oldKey.size(), maxY);
			addComponent(new ImageButton(deleteBase, deleteHover, () -> {
				removeItem(itemIndex);
			}), 0.27f + stride * oldKey.size(), minY, 0.32f + stride * oldKey.size(), maxY);

			GuiComponent valueField;
			if (entry.getValue().type == NbtValueType.INTEGER) {
				valueField = new EagerIntEditField(
						entry.getValue().getIntValue(), Integer.MIN_VALUE,
						EDIT_BASE, EDIT_ACTIVE, newValue -> {
							entry.setValue(new ExtraItemNbtValues.Value(newValue));
				});
			} else if (entry.getValue().type == NbtValueType.STRING) {
				valueField = new EagerTextEditField(entry.getValue().getStringValue(),
						EDIT_BASE, EDIT_ACTIVE, newValue -> {
					entry.setValue(new ExtraItemNbtValues.Value(newValue));
				});
			} else {
				throw new Error("Unknown value type: " + entry.getValue().type);
			}

			addComponent(new DynamicTextComponent("Value:", EditProps.LABEL),
					0.34f + stride * oldKey.size(), minY, 0.44f + stride * oldKey.size(), maxY
			);
			addComponent(valueField, 0.45f + stride * oldKey.size(), minY,
					0.6f + stride * oldKey.size(), maxY
			);
		}

		@Override
		protected ExtraItemNbtValues.Entry addNew() {
			throw new UnsupportedOperationException("Use the 'Add integer' or 'Add string' button");
		}

		@Override
		protected String getHelpPage() {
			return null;
		}
	}
}
