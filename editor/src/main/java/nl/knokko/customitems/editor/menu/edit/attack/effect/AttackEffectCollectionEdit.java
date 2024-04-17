package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.*;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.sound.EditSound;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.function.Consumer;

public class AttackEffectCollectionEdit extends SelfDedicatedCollectionEdit<AttackEffect> {

    private final ItemSet itemSet;

    public AttackEffectCollectionEdit(
            Collection<AttackEffect> oldCollection,
            Consumer<Collection<AttackEffect>> changeCollection,
            GuiComponent returnMenu, ItemSet itemSet
    ) {
        super(oldCollection, changeCollection::accept, returnMenu);
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextButton("Add effect", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new CreateAttackEffect(
                    this::addModel, this, itemSet,
                    liveCollection.stream().noneMatch(effect -> effect instanceof AttackEffectIgnite),
                    liveCollection.stream().noneMatch(effect -> effect instanceof AttackEffectDropWeapon)
            ));
        }), 0.025f, 0.2f, 0.2f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/attack/effect/list.html");
    }

    @Override
    protected String getModelLabel(AttackEffect model) {
        return model.toString();
    }

    @Override
    protected BufferedImage getModelIcon(AttackEffect model) {
        return null;
    }

    @Override
    protected boolean canEditModel(AttackEffect model) {
        return !(model instanceof AttackEffectDropWeapon);
    }

    @Override
    protected GuiComponent createEditMenu(AttackEffect oldModelValues, Consumer<AttackEffect> changeModelValues) {
        if (oldModelValues instanceof AttackEffectPotion) {
            return new EditAttackPotionEffect((AttackEffectPotion) oldModelValues, changeModelValues, this, itemSet);
        } else if (oldModelValues instanceof AttackEffectIgnite) {
            return new EditAttackIgnite((AttackEffectIgnite) oldModelValues, changeModelValues, this, itemSet);
        } else if (oldModelValues instanceof AttackEffectLaunchProjectile) {
            return new EditAttackLaunch((AttackEffectLaunchProjectile) oldModelValues, changeModelValues, this, itemSet);
        } else if (oldModelValues instanceof AttackEffectDelayedDamage) {
            return new EditAttackDealDamage((AttackEffectDelayedDamage) oldModelValues, changeModelValues, this, itemSet);
        } else if (oldModelValues instanceof AttackEffectPlaySound) {
            return new EditSound(((AttackEffectPlaySound) oldModelValues).getSound(), newSound -> {
                changeModelValues.accept(AttackEffectPlaySound.createQuick(newSound));
            }, this, itemSet);
        } else {
            throw new Error("Unknown AttackEffectValues sublcass: " + oldModelValues.getClass());
        }
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(AttackEffect model) {
        if (model instanceof AttackEffectDropWeapon || model instanceof AttackEffectIgnite) {
            return CopyMode.DISABLED;
        } else {
            return CopyMode.INSTANT;
        }
    }
}
