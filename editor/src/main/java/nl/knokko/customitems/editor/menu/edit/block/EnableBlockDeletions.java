package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EnableBlockDeletions extends GuiMenu {

    private final EditMenu menu;

    public EnableBlockDeletions(EditMenu menu) {
        this.menu = menu;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextComponent("Warning", ERROR), 0.4f, 0.9f, 0.6f, 1f);
        addComponent(new DynamicTextComponent(
                "Deleting a custom block will remove its texture and properties,", LABEL
        ), 0.05f, 0.8f, 0.7f, 0.9f);
        addComponent(new DynamicTextComponent(
                "but it will NOT delete the custom blocks that have been placed on your server.", LABEL
        ), 0.05f, 0.7f, 0.9f, 0.8f);
        addComponent(new DynamicTextComponent(
                "Such 'garbage blocks' can even be confused with future custom blocks!", LABEL
        ), 0.05f, 0.6f, 0.75f, 0.7f);
        addComponent(new DynamicTextComponent(
                "You should only delete custom block when you are sure that they have never been placed.", LABEL
        ), 0.05f, 0.5f, 0.95f, 0.6f);

        addComponent(new DynamicTextButton("Cancel", SAVE_BASE, SAVE_HOVER, () -> {
            state.getWindow().setMainComponent(new BlockCollectionEdit(menu, false));
        }), 0.2f, 0.2f, 0.35f, 0.3f);
        addComponent(new DynamicTextButton("Proceed anyway", QUIT_BASE, QUIT_HOVER, () -> {
            state.getWindow().setMainComponent(new BlockCollectionEdit(menu, true));
        }), 0.6f, 0.2f, 0.8f, 0.3f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
