package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.io.IOException;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ConfirmQuitMenu extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final String itemSetName;

    public ConfirmQuitMenu(GuiComponent returnMenu, ItemSet itemSet, String itemSetName) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.itemSetName = itemSetName;
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextComponent(
                "Would you like to save before you quit?", LABEL
        ), 0.05f, 0.8f, 0.95f, 0.9f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.2f, 0.1f, 0.35f, 0.2f);
        addComponent(new DynamicTextButton("No", QUIT_BASE, QUIT_HOVER, () -> {
            state.getWindow().stopRunning();
        }), 0.4f, 0.1f, 0.5f, 0.2f);
        addComponent(new DynamicTextButton("Yes", SAVE_BASE, SAVE_HOVER, () -> {
            try {
                EditorFileManager.saveAndBackUp(itemSet, itemSetName);
                state.getWindow().stopRunning();
            } catch (IOException e) {
                errorComponent.setText(e.getLocalizedMessage());
            }
        }), 0.55f, 0.1f, 0.65f, 0.2f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
