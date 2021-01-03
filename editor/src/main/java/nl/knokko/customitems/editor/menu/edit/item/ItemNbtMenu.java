package nl.knokko.customitems.editor.menu.edit.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.item.nbt.NbtKey;
import nl.knokko.customitems.item.nbt.NbtPair;
import nl.knokko.customitems.item.nbt.NbtValue;
import nl.knokko.customitems.item.nbt.NbtValueType;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class ItemNbtMenu extends GuiMenu {
	
	private final Consumer<ExtraItemNbt> onApply;
	private final GuiComponent returnMenu;
	private final String currentName;
	private final boolean hasDurability;
	
	private final DynamicTextComponent errorComponent;
	
	private final List<String[]> keys;
	private final List<NbtValue> values;

	public ItemNbtMenu(ExtraItemNbt oldNbt, Consumer<ExtraItemNbt> onApply,
			GuiComponent returnMenu, String currentName, boolean hasDurability) {
		this.onApply = onApply;
		this.returnMenu = returnMenu;
		this.currentName = !currentName.isEmpty() ? currentName : "%NAME%";
		this.hasDurability = hasDurability;
		this.errorComponent = new DynamicTextComponent("", EditProps.ERROR);
		
		Collection<NbtPair> pairs = oldNbt.getPairs();
		this.keys = new ArrayList<>(pairs.size());
		this.values = new ArrayList<>(pairs.size());
		for (NbtPair pair : pairs) {
			this.keys.add(pair.getKey().getParts());
			this.values.add(pair.getValue());
		}
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
		addComponent(new PairList(), 0f, 0f, 1f, 0.3f);
		
		HelpButtons.addHelpLink(this, "edit%20menu/items/edit/nbt.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	protected class PairList extends GuiMenu {

		@Override
		protected void addComponents() {
			for (int index = 0; index < keys.size(); index++) {
				
				final int rememberIndex = index;
				String[] key = keys.get(index);
				NbtValue value = values.get(index);
				
				float minY = 0.9f - 0.15f * index;
				float maxY = 1f - 0.15f * index;
				
				for (int keyIndex = 0; keyIndex < key.length; keyIndex++) {
					
					int rememberKeyIndex = keyIndex;
					addComponent(new EagerTextEditField(key[keyIndex], 
							EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, 
							newText -> {
								key[rememberKeyIndex] = newText;
							}
					), 0.05f + keyIndex * 0.2f, minY, 0.2f + keyIndex * 0.2f, maxY);
				}
				
				addComponent(new DynamicTextButton("+", 
						EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
					String[] newKey = Arrays.copyOf(key, key.length + 1);
					newKey[key.length] = "";
					keys.set(rememberIndex, newKey);
					refresh();
				}), 0.1f + 0.2f * key.length, minY, 0.15f + 0.2f * key.length, maxY);
				addComponent(new DynamicTextButton("X", 
						EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
					keys.remove(rememberIndex);
					values.remove(rememberIndex);
					refresh();
				}), 0.2f + 0.2f * key.length, minY, 0.25f + 0.2f * key.length, maxY);
				
				GuiComponent valueField;
				if (value.getType() == NbtValueType.INTEGER) {
					valueField = new EagerIntEditField(
							value.getIntValue(), Integer.MIN_VALUE, 
							EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, newValue -> {
								values.set(rememberIndex, new NbtValue(newValue));
							});
				} else if (value.getType() == NbtValueType.STRING) {
					valueField = new EagerTextEditField(value.getStringValue(),
							EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, newValue -> {
								values.set(rememberIndex, new NbtValue(newValue));
							});
				} else {
					throw new Error("Unknown value type: " + value.getType());
				}
				
				addComponent(new DynamicTextComponent("Value:", EditProps.LABEL),
						0.3f + 0.2f * key.length, minY, 0.4f + 0.2f * key.length, maxY
				);
				addComponent(valueField, 0.45f + 0.2f * key.length, minY, 
						0.6f + 0.2f * key.length, maxY
				);
			}
			
			float minY = 0.85f - 0.2f * keys.size();
			float maxY = 1f - 0.2f * keys.size();
			addComponent(new DynamicTextButton("Add integer", 
					EditProps.BUTTON, EditProps.HOVER, () -> {
				keys.add(new String[] {""});
				values.add(new NbtValue(1));
				refresh();
			}), 0.1f, minY, 0.3f, maxY);
			addComponent(new DynamicTextButton("Add string", 
					EditProps.BUTTON, EditProps.HOVER, () -> {
				keys.add(new String[] {""});
				values.add(new NbtValue(""));
				refresh();
			}), 0.35f, minY, 0.55f, maxY);
			addComponent(new DynamicTextButton("Cancel", 
					EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
				state.getWindow().setMainComponent(returnMenu);
			}), 0.6f, minY, 0.75f, maxY);
			addComponent(new DynamicTextButton("Apply", 
					EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
				try {
					Collection<NbtPair> newPairs = new ArrayList<>(keys.size());
					for (int index = 0; index < keys.size(); index++) {
						NbtKey key = new NbtKey(keys.get(index));
						newPairs.add(new NbtPair(key, values.get(index)));
					}
					onApply.accept(new ExtraItemNbt(newPairs));
					state.getWindow().setMainComponent(returnMenu);
				} catch (ValidationException invalid) {
					errorComponent.setText(invalid.getMessage());
				}
			}), 0.8f, minY, 0.95f, maxY);
		}
		
		protected void refresh() {
			clearComponents();
			addComponents();
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND2;
		}
	}
}
