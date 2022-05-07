package nl.knokko.customitems.editor.menu.edit.item.model;

import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BACKGROUND;

public class EditItemModel extends GuiMenu {

    private final CustomItemValues item;
    private final GuiComponent returnMenu;

    public EditItemModel(CustomItemValues item, GuiComponent returnMenu) {
        this.item = item;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {

    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
