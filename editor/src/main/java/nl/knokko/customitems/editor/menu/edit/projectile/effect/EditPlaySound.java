package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effect.PlaySoundValues;
import nl.knokko.customitems.projectile.effect.ProjectileEffectValues;
import nl.knokko.customitems.sound.VanillaSoundType;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditPlaySound extends EditProjectileEffect<PlaySoundValues> {

    public EditPlaySound(
            PlaySoundValues oldValues, Consumer<ProjectileEffectValues> applyChanges, GuiComponent returnMenu
    ) {
        super(oldValues, applyChanges, returnMenu);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(
                new DynamicTextComponent("Volume:", EditProps.LABEL),
                0.25f, 0.7f, 0.35f, 0.8f
        );
        addComponent(
                new EagerFloatEditField(currentValues.getVolume(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setVolume),
                0.4f, 0.7f, 0.5f, 0.8f
        );
        addComponent(
                new DynamicTextComponent("Pitch:", EditProps.LABEL),
                0.25f, 0.6f, 0.35f, 0.7f
        );
        addComponent(
                new EagerFloatEditField(currentValues.getPitch(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setPitch),
                0.4f, 0.6f, 0.5f, 0.7f
        );

        addComponent(
                new DynamicTextComponent("Sound:", EditProps.LABEL),
                0.25f, 0.4f, 0.35f, 0.5f
        );
        addComponent(
                EnumSelect.createSelectButton(VanillaSoundType.class, currentValues::setSound, currentValues.getSound()),
                0.4f, 0.4f, 0.6f, 0.5f
        );

        HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/sound.html");
    }
}
