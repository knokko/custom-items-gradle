package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.AttackDealDamageValues;
import nl.knokko.customitems.attack.effect.AttackEffectValues;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditAttackDealDamage extends EditAttackEffect {

    private final AttackDealDamageValues currentValues;

    public EditAttackDealDamage(
            AttackDealDamageValues oldValues, Consumer<AttackEffectValues> changeValues,
            GuiComponent returnMenu, ItemSet itemSet
    ) {
        super(changeValues, returnMenu, itemSet);
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextComponent("Damage:", LABEL), 0.4f, 0.5f, 0.5f, 0.6f);
        addComponent(new EagerFloatEditField(
                currentValues.getDamage(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setDamage
        ), 0.525f, 0.5f, 0.6f, 0.6f);

        addComponent(new DynamicTextComponent("Delay:", LABEL), 0.4f, 0.3f, 0.5f, 0.4f);
        addComponent(new EagerIntEditField(
                currentValues.getDelay(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setDelay
        ), 0.525f, 0.3f, 0.6f, 0.4f);

        HelpButtons.addHelpLink(this, "edit menu/attack/effect/deal damage.html");
    }

    @Override
    protected AttackEffectValues getCurrentValues() {
        return currentValues;
    }
}
