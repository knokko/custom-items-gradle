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

public class AttackEffectCollectionEdit extends SelfDedicatedCollectionEdit<AttackEffectValues> {

    private final ItemSet itemSet;

    public AttackEffectCollectionEdit(
            Collection<AttackEffectValues> oldCollection,
            Consumer<Collection<AttackEffectValues>> changeCollection,
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
                    liveCollection.stream().noneMatch(effect -> effect instanceof AttackIgniteValues),
                    liveCollection.stream().noneMatch(effect -> effect instanceof AttackDropWeaponValues)
            ));
        }), 0.025f, 0.2f, 0.2f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/attack/effect/list.html");
    }

    @Override
    protected String getModelLabel(AttackEffectValues model) {
        return model.toString();
    }

    @Override
    protected BufferedImage getModelIcon(AttackEffectValues model) {
        return null;
    }

    @Override
    protected boolean canEditModel(AttackEffectValues model) {
        return !(model instanceof AttackDropWeaponValues);
    }

    @Override
    protected GuiComponent createEditMenu(AttackEffectValues oldModelValues, Consumer<AttackEffectValues> changeModelValues) {
        if (oldModelValues instanceof AttackPotionEffectValues) {
            return new EditAttackPotionEffect((AttackPotionEffectValues) oldModelValues, changeModelValues, this, itemSet);
        } else if (oldModelValues instanceof AttackIgniteValues) {
            return new EditAttackIgnite((AttackIgniteValues) oldModelValues, changeModelValues, this, itemSet);
        } else if (oldModelValues instanceof AttackLaunchValues) {
            return new EditAttackLaunch((AttackLaunchValues) oldModelValues, changeModelValues, this, itemSet);
        } else if (oldModelValues instanceof AttackDealDamageValues) {
            return new EditAttackDealDamage((AttackDealDamageValues) oldModelValues, changeModelValues, this, itemSet);
        } else if (oldModelValues instanceof AttackPlaySoundValues) {
            return new EditSound(((AttackPlaySoundValues) oldModelValues).getSound(), newSound -> {
                changeModelValues.accept(AttackPlaySoundValues.createQuick(newSound));
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
    protected CopyMode getCopyMode(AttackEffectValues model) {
        if (model instanceof AttackDropWeaponValues || model instanceof AttackIgniteValues) {
            return CopyMode.DISABLED;
        } else {
            return CopyMode.INSTANT;
        }
    }
}
