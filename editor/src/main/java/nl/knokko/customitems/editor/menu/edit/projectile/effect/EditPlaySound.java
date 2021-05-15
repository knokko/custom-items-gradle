package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effects.PlaySound;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.SimpleParticles;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

import java.util.Collection;

import static nl.knokko.customitems.editor.menu.edit.EditProps.SAVE_BASE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.SAVE_HOVER;

public class EditPlaySound extends EditProjectileEffect {

    protected final PlaySound oldValues, toModify;

    public EditPlaySound(
            PlaySound oldValues, PlaySound toModify, Collection<ProjectileEffect> backingCollection,
            GuiComponent returnMenu
    ) {
        super(backingCollection, returnMenu);
        this.oldValues = oldValues;
        this.toModify = toModify;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        FloatEditField volumeField = new FloatEditField(
                oldValues == null ? 1f : oldValues.volume, 0f,
                EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE
        );
        FloatEditField pitchField = new FloatEditField(
                oldValues == null ? 1f : oldValues.pitch, 0f,
                EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE
        );

        addComponent(new DynamicTextComponent("Volume:", EditProps.LABEL), 0.25f, 0.7f, 0.35f, 0.8f);
        addComponent(volumeField, 0.4f, 0.7f, 0.5f, 0.8f);
        addComponent(new DynamicTextComponent("Pitch:", EditProps.LABEL), 0.25f, 0.6f, 0.35f, 0.7f);
        addComponent(pitchField, 0.4f, 0.6f, 0.5f, 0.7f);

        CISound[] pSound = { toModify == null ? CISound.ENTITY_BLAZE_SHOOT : toModify.sound };
        addComponent(new DynamicTextComponent("Sound:", EditProps.LABEL), 0.25f, 0.4f, 0.35f, 0.5f);
        addComponent(EnumSelect.createSelectButton(CISound.class, newSound -> pSound[0] = newSound, pSound[0]),
                0.4f, 0.4f, 0.6f, 0.5f);

        addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {

            Option.Float volume = volumeField.getFloat();
            Option.Float pitch = pitchField.getFloat();

            String error = null;
            if (!volume.hasValue()) error = "The volume must be a positive number";
            if (!pitch.hasValue()) error = "The pitch must be a positive number";

            if (error == null) {
                PlaySound dummy = new PlaySound(pSound[0], volume.getValue(), pitch.getValue());
                error = dummy.validate();
                if (error == null) {
                    if (toModify == null) {
                        backingCollection.add(dummy);
                    } else {
                        toModify.volume = dummy.volume;
                        toModify.pitch = dummy.pitch;
                        toModify.sound = dummy.sound;
                    }
                    state.getWindow().setMainComponent(returnMenu);
                } else {
                    errorComponent.setText(error);
                }
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/sound.html");
    }
}
