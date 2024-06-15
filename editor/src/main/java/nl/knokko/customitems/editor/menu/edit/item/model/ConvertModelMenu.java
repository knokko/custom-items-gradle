package nl.knokko.customitems.editor.menu.edit.item.model;

import nl.knokko.customitems.editor.ModelConverter;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.ConditionalTextComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ConvertModelMenu extends GuiMenu {

    private final GuiComponent returnMenu;
    private final KciItem item;
    private final ModelConverter.Progress progress;
    private final DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);

    public ConvertModelMenu(GuiComponent returnMenu, KciItem item) {
        this.returnMenu = returnMenu;
        this.item = item;
        progress = ModelConverter.convert(item);
    }


    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        addComponent(new ConditionalTextComponent(
                "Connected", LABEL, () -> progress.connected
        ), 0.4f, 0.7f, 0.6f, 0.8f);
        addComponent(new ConditionalTextComponent(
                "Sent Java model", LABEL, () -> progress.sent
        ), 0.35f, 0.55f, 0.65f, 0.65f);
        addComponent(new ConditionalTextComponent(
                "Receiving Bedrock model...", LABEL, () -> progress.receiving
        ), 0.3f, 0.4f, 0.7f, 0.5f);

        addComponent(new ConditionalTextButton("Done", BUTTON, HOVER, () -> {
            item.setGeyserModel(progress.result);
            state.getWindow().setMainComponent(returnMenu);
        }, () -> progress.result != null), 0.45f, 0.15f, 0.55f, 0.25f);
    }

    @Override
    public void update() {
        super.update();

        String error = progress.error;
        if (error != null && errorComponent.getText().isEmpty()) errorComponent.setText(error);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
