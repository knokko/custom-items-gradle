package nl.knokko.customitems.editor.menu.edit.projectile.effect;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.item.EffectsCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effect.PotionAuraValues;
import nl.knokko.customitems.projectile.effect.ProjectileEffectValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditPotionAura extends EditProjectileEffect<PotionAuraValues> {

    public EditPotionAura(
            PotionAuraValues oldValues, Consumer<ProjectileEffectValues> applyChanges, GuiComponent returnMenu
    ) {
        super(oldValues, applyChanges, returnMenu);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(
                new DynamicTextComponent("Radius:", EditProps.LABEL),
                0.2f, 0.7f, 0.3f, 0.8f
        );
        addComponent(
                new EagerFloatEditField(currentValues.getRadius(), 0f, EDIT_BASE, EDIT_ACTIVE, currentValues::setRadius),
                0.32f, 0.7f, 0.42f, 0.8f
        );

        addComponent(new DynamicTextButton("Manage effects...", EDIT_BASE, EDIT_ACTIVE, () -> {
            state.getWindow().setMainComponent(new EffectsCollectionEdit(currentValues.getEffects(), currentValues::setEffects, this));
        }), 0.2f, 0.5f, 0.35f, 0.6f);

        HelpButtons.addHelpLink(this, "edit%20menu/projectiles/effects/edit/potion aura.html");
    }
}
