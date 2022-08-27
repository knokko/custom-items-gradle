package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.AttackEffectValues;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.menu.edit.EditProps.BACKGROUND;

public abstract class EditAttackEffect extends GuiMenu {

    private final Consumer<AttackEffectValues> changeValues;
    private final GuiComponent returnMenu;
    private final ItemSet itemSet;

    public EditAttackEffect(
            Consumer<AttackEffectValues> changeValues, GuiComponent returnMenu, ItemSet itemSet
    ) {
        this.changeValues = changeValues;
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            AttackEffectValues currentValues = getCurrentValues();
            String error = Validation.toErrorString(() -> currentValues.validate(itemSet));
            if (error == null) {
                changeValues.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);
    }

    protected abstract AttackEffectValues getCurrentValues();

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
