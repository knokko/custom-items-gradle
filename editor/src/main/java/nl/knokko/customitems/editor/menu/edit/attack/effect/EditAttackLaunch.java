package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.AttackEffectValues;
import nl.knokko.customitems.attack.effect.AttackLaunchValues;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditAttackLaunch extends EditAttackEffect {

    private final AttackLaunchValues currentValues;

    public EditAttackLaunch(
            AttackLaunchValues oldValues, Consumer<AttackEffectValues> changeValues, GuiComponent returnMenu
    ) {
        super(changeValues, returnMenu);
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextComponent("Direction:", LABEL), 0.4f, 0.7f, 0.55f, 0.8f);
        addComponent(EnumSelect.createSelectButton(
                AttackLaunchValues.LaunchDirection.class, currentValues::setDirection, currentValues.getDirection()
        ), 0.6f, 0.7f, 0.8f, 0.8f);

        addComponent(new DynamicTextComponent("Speed:", LABEL), 0.4f, 0.5f, 0.5f, 0.6f);
        addComponent(new EagerFloatEditField(
                currentValues.getSpeed(), -100f, 100f, EDIT_BASE, EDIT_ACTIVE, currentValues::setSpeed
        ), 0.525f, 0.5f, 0.6f, 0.6f);

        // TODO Documentation
    }

    @Override
    protected AttackEffectValues getCurrentValues() {
        return currentValues;
    }
}
