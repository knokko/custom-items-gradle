package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.AttackEffectGroupValues;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.projectile.effect.EditProjectileEffects;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.projectile.effect.ProjectileEffectsValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.function.Consumer;

public class AttackEffectGroupCollectionEdit extends SelfDedicatedCollectionEdit<AttackEffectGroupValues> {

    private final boolean isForBlocking;

    public AttackEffectGroupCollectionEdit(
            Collection<AttackEffectGroupValues> oldCollection,
            Consumer<Collection<AttackEffectGroupValues>> changeCollection,
            boolean isForBlocking, GuiComponent returnMenu
    ) {
        super(oldCollection, changeCollection::accept, returnMenu);
        this.isForBlocking = isForBlocking;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextButton("Add effects", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new EditAttackEffectGroup(
                    new AttackEffectGroupValues(true), this::addModel, isForBlocking, this
            ));
        }), 0.025f, 0.2f, 0.2f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/attack/effect/group list.html");
    }

    @Override
    protected String getModelLabel(AttackEffectGroupValues model) {
        return model.getChance() + " for " + model.getAttackerEffects().size() + " attacker effects and "
                + model.getVictimEffects().size() + " victim effects";
    }

    @Override
    protected BufferedImage getModelIcon(AttackEffectGroupValues model) {
        return null;
    }

    @Override
    protected boolean canEditModel(AttackEffectGroupValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(AttackEffectGroupValues oldModelValues, Consumer<AttackEffectGroupValues> changeModelValues) {
        return new EditAttackEffectGroup(oldModelValues, changeModelValues, isForBlocking, this);
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(AttackEffectGroupValues model) {
        return CopyMode.INSTANT;
    }
}
