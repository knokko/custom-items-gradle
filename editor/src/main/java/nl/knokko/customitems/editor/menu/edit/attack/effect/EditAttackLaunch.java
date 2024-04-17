package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.AttackEffect;
import nl.knokko.customitems.attack.effect.AttackEffectLaunchProjectile;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditAttackLaunch extends EditAttackEffect {

    private final AttackEffectLaunchProjectile currentValues;

    public EditAttackLaunch(
            AttackEffectLaunchProjectile oldValues, Consumer<AttackEffect> changeValues,
            GuiComponent returnMenu, ItemSet itemSet
    ) {
        super(changeValues, returnMenu, itemSet);
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextComponent("Direction:", LABEL), 0.4f, 0.7f, 0.55f, 0.8f);
        addComponent(EnumSelect.createSelectButton(
                AttackEffectLaunchProjectile.LaunchDirection.class, currentValues::setDirection, currentValues.getDirection()
        ), 0.6f, 0.7f, 0.8f, 0.8f);

        addComponent(new DynamicTextComponent("Speed:", LABEL), 0.4f, 0.5f, 0.5f, 0.6f);
        addComponent(new EagerFloatEditField(
                currentValues.getSpeed(), -100f, 100f, EDIT_BASE, EDIT_ACTIVE, currentValues::setSpeed
        ), 0.525f, 0.5f, 0.6f, 0.6f);

        HelpButtons.addHelpLink(this, "edit menu/attack/effect/launch.html");
    }

    @Override
    protected AttackEffect getCurrentValues() {
        return currentValues;
    }
}
