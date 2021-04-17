package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effects.PlaySound;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.PushOrPull;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.FloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

import java.util.Collection;

import static nl.knokko.customitems.editor.menu.edit.EditProps.SAVE_BASE;
import static nl.knokko.customitems.editor.menu.edit.EditProps.SAVE_HOVER;

public class EditPushOrPull extends EditProjectileEffect {

    protected final PushOrPull oldValues, toModify;

    public EditPushOrPull(
            PushOrPull oldValues, PushOrPull toModify,
            Collection<ProjectileEffect> backingCollection, GuiComponent returnMenu
    ) {
        super(backingCollection, returnMenu);
        this.oldValues = oldValues;
        this.toModify = toModify;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        FloatEditField strengthField = new FloatEditField(
                oldValues == null ? 0.3f : oldValues.strength, -100f,
                EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE
        );
        FloatEditField radiusField = new FloatEditField(
                oldValues == null ? 2f : oldValues.radius, 0f,
                EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE
        );

        addComponent(new DynamicTextComponent("Push strength:", EditProps.LABEL), 0.2f, 0.6f, 0.35f, 0.7f);
        addComponent(strengthField, 0.375f, 0.6f, 0.5f, 0.7f);
        addComponent(new DynamicTextComponent("Radius:", EditProps.LABEL), 0.2f, 0.5f, 0.3f, 0.6f);
        addComponent(radiusField, 0.325f, 0.5f, 0.45f, 0.6f);

        addComponent(new DynamicTextComponent(
                "Use a positive push strength to push entities away from the projectile", EditProps.LABEL
        ), 0.05f, 0.4f, 0.95f, 0.5f);
        addComponent(new DynamicTextComponent(
                "Use a negative push strength to pull entities towards the projectile", EditProps.LABEL
        ), 0.05f, 0.3f, 0.95f, 0.4f);

        addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {

            Option.Float strength = strengthField.getFloat();
            Option.Float radius = radiusField.getFloat();

            String error = null;
            if (!strength.hasValue()) error = "The strength must be a number";
            if (!radius.hasValue()) error = "The radius must be a positive number";

            if (error == null) {
                PushOrPull dummy = new PushOrPull(strength.getValue(), radius.getValue());
                error = dummy.validate();
                if (error == null) {
                    if (toModify == null) {
                        backingCollection.add(dummy);
                    } else {
                        toModify.strength = dummy.strength;
                        toModify.radius = dummy.radius;
                    }
                    state.getWindow().setMainComponent(returnMenu);
                } else {
                    errorComponent.setText(error);
                }
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        // TODO Test this help link after merging docs into master
        HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/push.html");
    }
}
