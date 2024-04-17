package nl.knokko.customitems.editor.menu.edit.attack.effect;

import nl.knokko.customitems.attack.effect.*;
import nl.knokko.customitems.editor.menu.edit.sound.EditSound;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.sound.KciSound;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateAttackEffect extends GuiMenu {

    private final Consumer<AttackEffect> addEffect;
    private final GuiComponent returnMenu;
    private final ItemSet itemSet;

    private final boolean allowIgnite;
    private final boolean allowDropWeapon;

    public CreateAttackEffect(
            Consumer<AttackEffect> addEffect, GuiComponent returnMenu, ItemSet itemSet,
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
                    new AttackEffectPotion(true), addEffect, returnMenu, itemSet
            ));
        }), 0.4f, 0.8f, 0.6f, 0.9f);
        if (allowIgnite) {
            addComponent(new DynamicTextButton("Set on fire", BUTTON, HOVER, () -> {
                state.getWindow().setMainComponent(new EditAttackIgnite(
                        new AttackEffectIgnite(true), addEffect, returnMenu, itemSet
                ));
            }), 0.4f, 0.65f, 0.55f, 0.75f);
        }
        if (allowDropWeapon) {
            addComponent(new DynamicTextButton("Drop weapon or shield", BUTTON, HOVER, () -> {
                addEffect.accept(new AttackEffectDropWeapon(false));
                state.getWindow().setMainComponent(returnMenu);
            }), 0.4f, 0.5f, 0.65f, 0.6f);
        }
        addComponent(new DynamicTextButton("Launch", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditAttackLaunch(
                    new AttackEffectLaunchProjectile(true), addEffect, returnMenu, itemSet
            ));
        }), 0.4f, 0.35f, 0.5f, 0.45f);
        addComponent(new DynamicTextButton("Deal damage", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditAttackDealDamage(
                    new AttackEffectDelayedDamage(true), addEffect, returnMenu, itemSet
            ));
        }), 0.4f, 0.2f, 0.55f, 0.3f);
        addComponent(new DynamicTextButton("Play sound", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditSound(
                    new KciSound(true), chosenSound -> {
                        addEffect.accept(AttackEffectPlaySound.createQuick(chosenSound));
                    }, returnMenu, itemSet
            ));
        }), 0.4f, 0.05f, 0.55f, 0.15f);

        HelpButtons.addHelpLink(this, "edit menu/attack/effect/create.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
