package nl.knokko.customitems.editor.menu.edit.export;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BACKGROUND;
import static nl.knokko.customitems.editor.menu.edit.EditProps.LABEL;
import static nl.knokko.customitems.editor.menu.edit.export.ExportProgress.*;

public class ExportLoadingScreen extends GuiMenu {

    private final GuiComponent failureMenu;
    private final DynamicTextComponent errorComponent;
    private final ExportProgress progress;

    private int lastStatus = 0;
    private boolean didUploadResourcepack;

    public ExportLoadingScreen(GuiComponent failureMenu, DynamicTextComponent errorComponent, ExportProgress progress) {
        this.failureMenu = failureMenu;
        this.errorComponent = errorComponent;
        this.progress = progress;
    }

    @Override
    public void update() {
        super.update();

        if (progress.nextMenu != null) state.getWindow().setMainComponent(progress.nextMenu);
        if (progress.error != null) {
            errorComponent.setText(progress.error);
            state.getWindow().setMainComponent(failureMenu);
        }

        int currentStatus = progress.status;
        if (currentStatus != lastStatus) {
            lastStatus = currentStatus;
            state.getWindow().markChange();
        }

        if (currentStatus == STATUS_UPLOADING_RESOURCEPACK) didUploadResourcepack = true;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextComponent("Export is in progress...", LABEL), 0.3f, 0.9f, 0.7f, 1f);

        addComponent(new ConditionalTextComponent(
                "Generating resourcepack...", LABEL, () -> progress.status == STATUS_GENERATING_RESOURCEPACK
        ), 0.4f, 0.7f, 0.6f, 0.78f);
        addComponent(new ConditionalTextComponent(
                "Generated resourcepack", LABEL, () -> progress.status > STATUS_GENERATING_RESOURCEPACK
        ), 0.4f, 0.7f, 0.6f, 0.78f);

        addComponent(new ConditionalTextComponent(
                "Uploading resourcepack...", LABEL, () -> progress.status == STATUS_UPLOADING_RESOURCEPACK
        ), 0.4f, 0.62f, 0.6f, 0.7f);
        addComponent(new ConditionalTextComponent(
                "Uploaded resourcepack", LABEL, () -> progress.status > STATUS_UPLOADING_RESOURCEPACK && didUploadResourcepack
        ), 0.4f, 0.62f, 0.6f, 0.7f);

        addComponent(new ConditionalTextComponent(
                "Saving item set...", LABEL, () -> progress.status == STATUS_SAVING_ITEM_SET
        ), 0.4f, 0.54f, 0.56f, 0.62f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
