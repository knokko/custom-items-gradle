package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effect.ProjectileEffectValues;
import nl.knokko.customitems.projectile.effect.PushOrPullValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditPushOrPull extends EditProjectileEffect<PushOrPullValues> {

    public EditPushOrPull(
            PushOrPullValues oldValues, Consumer<ProjectileEffectValues> changeValues, GuiComponent returnMenu
    ) {
        super(oldValues, changeValues, returnMenu);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(
                new DynamicTextComponent("Push strength:", EditProps.LABEL),
                0.2f, 0.6f, 0.35f, 0.7f
        );
        addComponent(
                new EagerFloatEditField(currentValues.getStrength(), -100f, EDIT_BASE, EDIT_ACTIVE, currentValues::setStrength),
                0.375f, 0.6f, 0.5f, 0.7f
        );
        addComponent(
                new DynamicTextComponent("Radius:", EditProps.LABEL),
                0.2f, 0.5f, 0.3f, 0.6f
        );
        addComponent(
                new EagerFloatEditField(currentValues.getRadius(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setRadius),
                0.325f, 0.5f, 0.45f, 0.6f
        );

        addComponent(new DynamicTextComponent(
                "Use a positive push strength to push entities away from the projectile", EditProps.LABEL
        ), 0.05f, 0.4f, 0.95f, 0.5f);
        addComponent(new DynamicTextComponent(
                "Use a negative push strength to pull entities towards the projectile", EditProps.LABEL
        ), 0.05f, 0.3f, 0.95f, 0.4f);

        HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/push.html");
    }
}
