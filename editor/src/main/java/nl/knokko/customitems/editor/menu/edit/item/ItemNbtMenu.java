package nl.knokko.customitems.editor.menu.edit.item;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.menu.TextListEditMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ItemNbtMenu extends GuiMenu {

	private final Supplier<List<String>> getNbt;
	private final Consumer<List<String>> changeNbt;
	private final GuiComponent returnMenu;
	private final String currentName;
	private final boolean hasDurability;
	
	public ItemNbtMenu(Supplier<List<String>> getNbt, Consumer<List<String>> changeNbt,
			GuiComponent returnMenu, String currentName, boolean hasDurability) {
		this.getNbt = getNbt;
		this.changeNbt = changeNbt;
		this.returnMenu = returnMenu;
		this.currentName = !currentName.isEmpty() ? currentName : "%NAME%";
		this.hasDurability = hasDurability;
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Back", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.05f, 0.8f, 0.175f, 0.9f);
		addComponent(new DynamicTextComponent(
				"Custom items will always have the following nbt tags:",
				EditProps.LABEL), 0.02f, 0.7f, 0.9f, 0.8f
		);
		String baseTag = "{KnokkosCustomItems:{Name:\"" + currentName + "\"";
		if (hasDurability) baseTag += ",Durability:%REMAINING_DURABILITY%";
		baseTag += "}}";
		addComponent(new DynamicTextComponent(baseTag, LABEL), 0.02f, 0.6f, 0.98f, 0.7f);

		addComponent(new DynamicTextComponent(
				"There are others as well, but these are not so interesting for users",
				EditProps.LABEL), 0.02f, 0.4f, 0.9f, 0.5f);
		addComponent(new DynamicTextComponent(
				"You can add more nbt tags below if you would like to:", 
				EditProps.LABEL), 0.02f, 0.3f, 0.7f, 0.4f);
		addComponent(new DynamicTextButton("Configure...", BUTTON, HOVER, () -> {
			state.getWindow().setMainComponent(new TextListEditMenu(
					this, changeNbt, BACKGROUND, CANCEL_BASE, CANCEL_HOVER,
					SAVE_BASE, SAVE_HOVER, EDIT_BASE, EDIT_ACTIVE, getNbt.get()
			));
		}), 0.35f, 0.15f, 0.55f, 0.25f);

		HelpButtons.addHelpLink(this, "edit menu/items/edit/nbt.html");
	}

	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
}
