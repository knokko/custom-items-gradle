package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.AttackEffectGroupValues;
import nl.knokko.customitems.editor.util.FixedPointEditField;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.util.Validation;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.util.Validation.toErrorString;

public class EditAttackEffectGroup extends GuiMenu {

    private final AttackEffectGroupValues currentValues;
    private final Consumer<AttackEffectGroupValues> changeValues;
    private final boolean isForBlocking;
    private final GuiComponent returnMenu;

    public EditAttackEffectGroup(
            AttackEffectGroupValues oldValues, Consumer<AttackEffectGroupValues> changeValues,
            boolean isForBlocking, GuiComponent returnMenu
    ) {
        this.currentValues = oldValues.copy(true);
        this.changeValues = changeValues;
        this.isForBlocking = isForBlocking;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.05f, 0.8f, 0.175f, 0.9f);

        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            String error = toErrorString(currentValues::validate);
            if (error == null) {
                changeValues.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.05f, 0.2f, 0.175f, 0.3f);

        addComponent(new DynamicTextComponent("Attacker effects", LABEL), 0.2f, 0.7f, 0.4f, 0.8f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new AttackEffectCollectionEdit(
                    currentValues.getAttackerEffects(), currentValues::setAttackerEffects, this
            ));
        }), 0.45f, 0.7f, 0.55f, 0.8f);

        addComponent(new DynamicTextComponent("Victim effects", LABEL), 0.2f, 0.55f, 0.4f, 0.65f);
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new AttackEffectCollectionEdit(
                    currentValues.getVictimEffects(), currentValues::setVictimEffects, this
            ));
        }), 0.45f, 0.55f, 0.55f, 0.65f);

        addComponent(new DynamicTextComponent("Chance:", LABEL), 0.2f, 0.4f, 0.3f, 0.5f);
        addComponent(new FixedPointEditField(
                Chance.NUM_BACK_DIGITS, currentValues.getChance().getRawValue(), 0, 100,
                newRawChance -> currentValues.setChance(new Chance(newRawChance))
        ), 0.325f, 0.4f, 0.45f, 0.5f);
        addComponent(new DynamicTextComponent("%", LABEL), 0.45f, 0.4f, 0.48f, 0.5f);

        addComponent(new DynamicTextComponent("Original damage threshold:", LABEL), 0.2f, 0.29f, 0.5f, 0.39f);
        addComponent(new EagerFloatEditField(
                currentValues.getOriginalDamageThreshold(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setOriginalDamageThreshold
        ), 0.525f, 0.29f, 0.6f, 0.39f);

        // When blocking, the final damage is always 0.0, so this wouldn't make any sense
        if (!isForBlocking) {
            addComponent(new DynamicTextComponent("Final damage threshold:", LABEL), 0.2f, 0.18f, 0.5f, 0.28f);
            addComponent(new EagerFloatEditField(
                    currentValues.getFinalDamageThreshold(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setFinalDamageThreshold
            ), 0.525f, 0.18f, 0.6f, 0.28f);
        }

        HelpButtons.addHelpLink(this, "edit menu/attack/effect/group edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
