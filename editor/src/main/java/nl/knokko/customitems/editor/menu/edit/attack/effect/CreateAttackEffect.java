package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.*;
import nl.knokko.customitems.editor.menu.edit.sound.EditSound;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.SoundValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateAttackEffect extends GuiMenu {

    private final Consumer<AttackEffectValues> addEffect;
    private final GuiComponent returnMenu;
    private final ItemSet itemSet;

    private final boolean allowIgnite;
    private final boolean allowDropWeapon;

    public CreateAttackEffect(
            Consumer<AttackEffectValues> addEffect, GuiComponent returnMenu, ItemSet itemSet,
            boolean allowIgnite, boolean allowDropWeapon
    ) {
        this.addEffect = addEffect;
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.allowIgnite = allowIgnite;
        this.allowDropWeapon = allowDropWeapon;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Give potion effect", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditAttackPotionEffect(
                    new AttackPotionEffectValues(true), addEffect, returnMenu, itemSet
            ));
        }), 0.4f, 0.8f, 0.6f, 0.9f);
        if (allowIgnite) {
            addComponent(new DynamicTextButton("Set on fire", BUTTON, HOVER, () -> {
                state.getWindow().setMainComponent(new EditAttackIgnite(
                        new AttackIgniteValues(true), addEffect, returnMenu, itemSet
                ));
            }), 0.4f, 0.65f, 0.55f, 0.75f);
        }
        if (allowDropWeapon) {
            addComponent(new DynamicTextButton("Drop weapon or shield", BUTTON, HOVER, () -> {
                addEffect.accept(new AttackDropWeaponValues(false));
                state.getWindow().setMainComponent(returnMenu);
            }), 0.4f, 0.5f, 0.65f, 0.6f);
        }
        addComponent(new DynamicTextButton("Launch", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditAttackLaunch(
                    new AttackLaunchValues(true), addEffect, returnMenu, itemSet
            ));
        }), 0.4f, 0.35f, 0.5f, 0.45f);
        addComponent(new DynamicTextButton("Deal damage", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditAttackDealDamage(
                    new AttackDealDamageValues(true), addEffect, returnMenu, itemSet
            ));
        }), 0.4f, 0.2f, 0.55f, 0.3f);
        addComponent(new DynamicTextButton("Play sound", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditSound(
                    new SoundValues(true), chosenSound -> {
                        addEffect.accept(AttackPlaySoundValues.createQuick(chosenSound));
                    }, returnMenu, itemSet
            ));
        }), 0.4f, 0.05f, 0.55f, 0.15f);

        // TODO Update help menu
        HelpButtons.addHelpLink(this, "edit menu/attack/effect/create.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
