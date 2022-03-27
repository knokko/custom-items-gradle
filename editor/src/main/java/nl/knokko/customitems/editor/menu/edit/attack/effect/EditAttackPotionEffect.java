package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.AttackEffectValues;
import nl.knokko.customitems.attack.effect.AttackPotionEffectValues;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.effect.EffectType;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditAttackPotionEffect extends EditAttackEffect {

    private final AttackPotionEffectValues currentValues;

    public EditAttackPotionEffect(
            AttackPotionEffectValues oldValues, Consumer<AttackEffectValues> changeValues, GuiComponent returnMenu
    ) {
        super(changeValues, returnMenu);
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextComponent("Potion effect:", LABEL), 0.4f, 0.7f, 0.55f, 0.8f);
        addComponent(EnumSelect.createSelectButton(
                EffectType.class, currentValues::setPotionEffectType, currentValues.getPotionEffect().getType()
        ), 0.6f, 0.7f, 0.8f, 0.8f);

        addComponent(new DynamicTextComponent("Duration:", LABEL), 0.4f, 0.55f, 0.5f, 0.65f);
        addComponent(new EagerIntEditField(
                currentValues.getPotionEffect().getDuration(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setDuration
        ), 0.525f, 0.55f, 0.6f, 0.65f);

        addComponent(new DynamicTextComponent("Level:", LABEL), 0.4f, 0.4f, 0.5f, 0.5f);
        addComponent(new EagerIntEditField(
                currentValues.getPotionEffect().getLevel(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setLevel
        ), 0.525f, 0.4f, 0.6f, 0.5f);

        // TODO Add documentation
    }

    @Override
    protected AttackEffectValues getCurrentValues() {
        return currentValues;
    }
}
