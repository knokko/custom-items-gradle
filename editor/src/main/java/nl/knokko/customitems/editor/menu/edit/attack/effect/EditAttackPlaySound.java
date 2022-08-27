package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.AttackEffectValues;
import nl.knokko.customitems.attack.effect.AttackPlaySoundValues;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.sound.VanillaSoundType;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditAttackPlaySound extends EditAttackEffect {

    private final AttackPlaySoundValues currentValues;

    public EditAttackPlaySound(
            AttackPlaySoundValues oldValues, Consumer<AttackEffectValues> changeValues, GuiComponent returnMenu
    ) {
        super(changeValues, returnMenu);
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextComponent("Sound:", LABEL), 0.4f, 0.7f, 0.5f, 0.8f);
        addComponent(EnumSelect.createSelectButton(
                VanillaSoundType.class, currentValues::setSound, currentValues.getSound()
        ), 0.55f, 0.7f, 0.75f, 0.8f);

        addComponent(new DynamicTextComponent("Volume:", LABEL), 0.4f, 0.55f, 0.5f, 0.65f);
        addComponent(new EagerFloatEditField(
                currentValues.getVolume(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setVolume
        ), 0.525f, 0.55f, 0.6f, 0.65f);

        addComponent(new DynamicTextComponent("Pitch:", LABEL), 0.4f, 0.4f, 0.5f, 0.5f);
        addComponent(new EagerFloatEditField(
                currentValues.getPitch(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setPitch
        ), 0.525f, 0.4f, 0.6f, 0.5f);

        HelpButtons.addHelpLink(this, "edit menu/attack/effect/play sound.html");
    }

    @Override
    protected AttackEffectValues getCurrentValues() {
        return currentValues;
    }
}
