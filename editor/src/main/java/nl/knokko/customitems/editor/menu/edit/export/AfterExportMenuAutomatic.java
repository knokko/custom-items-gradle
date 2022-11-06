package nl.knokko.customitems.editor.menu.edit.export;

import nl.knokko.customitems.editor.menu.commandhelp.CommandBlockHelpOverview;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class AfterExportMenuAutomatic extends AfterExportMenu {

    private final String resourcePackHash;

    public AfterExportMenuAutomatic(GuiComponent returnMenu, String resourcePackHash) {
        super(returnMenu);
        this.resourcePackHash = resourcePackHash;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);

        addComponent(new DynamicTextComponent(
                "Run the following command on your server", LABEL
        ), 0.025f, 0.8f, 0.6f, 0.9f);
        String command = "kci reload " + resourcePackHash;
        addComponent(new DynamicTextComponent(
                "/" + command, LABEL
        ), 0.1f, 0.65f, 0.8f, 0.75f);
        addComponent(new DynamicTextButton("Click here to copy it, INCLUDING the /", BUTTON, HOVER, () -> {
            String error = CommandBlockHelpOverview.setClipboard("/" + command);
            if (error != null) errorComponent.setText(error);
        }), 0.025f, 0.5f, 0.5f, 0.6f);
        addComponent(new DynamicTextButton("Click here to copy it, EXCLUDING the /", BUTTON, HOVER, () -> {
            String error = CommandBlockHelpOverview.setClipboard(command);
            if (error != null) errorComponent.setText(error);
        }), 0.025f, 0.35f, 0.5f, 0.45f);
    }
}
