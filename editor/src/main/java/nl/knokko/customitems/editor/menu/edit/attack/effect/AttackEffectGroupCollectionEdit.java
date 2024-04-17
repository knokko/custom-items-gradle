package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.AttackEffectGroup;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.function.Consumer;

public class AttackEffectGroupCollectionEdit extends SelfDedicatedCollectionEdit<AttackEffectGroup> {

    private final boolean isForBlocking;
    private final ItemSet itemSet;

    public AttackEffectGroupCollectionEdit(
            Collection<AttackEffectGroup> oldCollection,
            Consumer<Collection<AttackEffectGroup>> changeCollection,
            boolean isForBlocking, GuiComponent returnMenu, ItemSet itemSet
    ) {
        super(oldCollection, changeCollection::accept, returnMenu);
        this.isForBlocking = isForBlocking;
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextButton("Add effects", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new EditAttackEffectGroup(
                    new AttackEffectGroup(true), this::addModel, isForBlocking, this, itemSet
            ));
        }), 0.025f, 0.2f, 0.2f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/attack/effect/group list.html");
    }

    @Override
    protected String getModelLabel(AttackEffectGroup model) {
        return model.getChance() + " for " + model.getAttackerEffects().size() + " attacker effects and "
                + model.getVictimEffects().size() + " victim effects";
    }

    @Override
    protected BufferedImage getModelIcon(AttackEffectGroup model) {
        return null;
    }

    @Override
    protected boolean canEditModel(AttackEffectGroup model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(AttackEffectGroup oldModelValues, Consumer<AttackEffectGroup> changeModelValues) {
        return new EditAttackEffectGroup(oldModelValues, changeModelValues, isForBlocking, this, itemSet);
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(AttackEffectGroup model) {
        return CopyMode.INSTANT;
    }
}
