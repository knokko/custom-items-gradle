package nl.knokko.customitems.editor.menu.edit.recipe.result;

import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.recipe.result.MimicResultValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.menu.edit.EditProps.SAVE_HOVER;

public class ChooseMimicResult extends GuiMenu {

    private final GuiComponent returnMenu;
    private final Consumer<MimicResultValues> onSelect;

    private final MimicResultValues result;

    public ChooseMimicResult(GuiComponent returnMenu, Consumer<MimicResultValues> onSelect) {
        this.returnMenu = returnMenu;
        this.onSelect = onSelect;
        this.result = new MimicResultValues(true);
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.05f, 0.7f, 0.15f, 0.8f);

        addComponent(new DynamicTextComponent("Item:", LABEL), 0.3f, 0.6f, 0.4f, 0.7f);
        addComponent(
                new EagerTextEditField(result.getItemId(), EDIT_BASE, EDIT_ACTIVE, result::setItemId),
                0.45f, 0.6f, 0.75f, 0.7f
        );
        addComponent(new DynamicTextComponent("Amount:", LABEL), 0.3f, 0.4f, 0.4f, 0.5f);
        addComponent(
                new EagerIntEditField(result.getAmount(), 1, 64, EDIT_BASE, EDIT_ACTIVE, result::setAmount),
                0.45f, 0.4f, 0.55f, 0.5f
        );

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            String error = Validation.toErrorString(result::validateIndependent);
            if (error == null) {
                onSelect.accept(result);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.05f, 0.2f, 0.15f, 0.3f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
