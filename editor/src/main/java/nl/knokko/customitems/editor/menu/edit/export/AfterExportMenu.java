package nl.knokko.customitems.editor.menu.edit.export;

import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.awt.*;
import java.io.IOException;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public abstract class AfterExportMenu extends GuiMenu {

    private final GuiComponent returnMenu;

    public AfterExportMenu(GuiComponent returnMenu) {
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Exit editor", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().stopRunning();
        }), 0.1f, 0.1f, 0.25f, 0.2f);
        addComponent(new DynamicTextButton("Back to main menu", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(MainMenu.INSTANCE);
        }), 0.35f, 0.1f, 0.55f, 0.2f);
        addComponent(new DynamicTextButton("Back to edit menu", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.65f, 0.1f, 0.85f, 0.2f);
    }

    protected void addExportedFilesInfo() {
        addComponent(new DynamicTextComponent(
                "The files items.cis.txt and resource-pack.zip have been exported to", LABEL
        ), 0.025f, 0.8f, 0.9f, 0.9f);
        addComponent(new DynamicTextButton(EditorFileManager.FOLDER.getAbsolutePath(), LINK_BASE, LINK_HOVER, () -> {
            try {
                Desktop.getDesktop().open(EditorFileManager.FOLDER);
            } catch (IOException e) {
                System.err.println("Couldn't open export destination folder: " + e.getLocalizedMessage());
            }
        }), 0.025f, 0.7f, 0.5f, 0.8f);
        addComponent(new DynamicTextComponent(
                "You should copy items.cis.txt to YourServerFolder/plugins/CustomItems/items.cis.txt", LABEL
        ), 0.025f, 0.6f, 0.975f, 0.7f);
        // Note that the step for resource-pack.zip is intentionally left out because it differs per export mode
        addComponent(new DynamicTextComponent(
                "Finally, you should execute the command /kci reload", LABEL
        ), 0.025f, 0.4f, 0.6f, 0.5f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
