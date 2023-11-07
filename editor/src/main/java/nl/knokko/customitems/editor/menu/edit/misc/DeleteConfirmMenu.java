package nl.knokko.customitems.editor.menu.edit.misc;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class DeleteConfirmMenu extends GuiMenu {

    private final GuiComponent returnMenu;
    private final String description;
    private final Runnable delete;

    public DeleteConfirmMenu(GuiComponent returnMenu, String description, Runnable delete) {
        this.returnMenu = returnMenu;
        this.description = description;
        this.delete = delete;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextComponent(
                "Are you sure you want to delete " + description + "?", LABEL
        ), 0.2f, 0.7f, 0.8f, 0.8f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.3f, 0.5f, 0.45f, 0.6f);
        addComponent(new DynamicTextButton("Delete", QUIT_BASE, QUIT_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
            delete.run();
        }), 0.55f, 0.5f, 0.7f, 0.6f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
