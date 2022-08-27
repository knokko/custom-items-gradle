package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.AttackEffectValues;
import nl.knokko.customitems.attack.effect.AttackIgniteValues;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditAttackIgnite extends EditAttackEffect {

    private final AttackIgniteValues currentValues;

    public EditAttackIgnite(
            AttackIgniteValues oldValues, Consumer<AttackEffectValues> changeValues,
            GuiComponent returnMenu, ItemSet itemSet
    ) {
        super(changeValues, returnMenu, itemSet);
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextComponent("Duration:", LABEL), 0.4f, 0.6f, 0.55f, 0.7f);
        addComponent(new EagerIntEditField(
                currentValues.getDuration(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setDuration
        ), 0.6f, 0.6f, 0.7f, 0.7f);

        HelpButtons.addHelpLink(this, "edit menu/attack/effect/ignite.html");
    }

    @Override
    protected AttackEffectValues getCurrentValues() {
        return currentValues;
    }
}
