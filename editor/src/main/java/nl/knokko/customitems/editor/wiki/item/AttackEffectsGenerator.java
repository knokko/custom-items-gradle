package nl.knokko.customitems.editor.wiki.item;

import nl.knokko.customitems.attack.effect.*;
import nl.knokko.customitems.editor.wiki.WikiHelper;
import nl.knokko.customitems.util.Chance;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

class AttackEffectsGenerator {

    private final Collection<AttackEffectGroup> attackEffects;

    AttackEffectsGenerator(Collection<AttackEffectGroup> attackEffects) {
        this.attackEffects = attackEffects;
    }

    void generate(PrintWriter output, String tabs) {
        output.println(tabs + "<ul class=\"attack-effect-group-list\">");
        for (AttackEffectGroup group : attackEffects) {
            output.println(tabs + "\t<li class=\"attack-effect-group\">");
            generateGroup(output, tabs + "\t\t", group);
            output.println(tabs + "\t</li>");
        }
        output.println(tabs + "</ul>");
    }

    private void generateGroup(PrintWriter output, String tabs, AttackEffectGroup group) {
        if (group.getOriginalDamageThreshold() > 0f || group.getFinalDamageThreshold() > 0f) {
            output.println(tabs + "Requirements:");
            output.println(tabs + "<ul class=\"attack-effect-group-requirement-list\">");
            if (group.getOriginalDamageThreshold() > 0f) {
                output.println(tabs + "\t<li class=\"attack-effect-group-requirement\">Original damage >= " + (group.getOriginalDamageThreshold() / 2f) + " hearts</li>");
            }
            if (group.getFinalDamageThreshold() > 0f) {
                output.println(tabs + "\t<li class=\"attack-effect-group-requirement\">Final damage >= " + (group.getFinalDamageThreshold() / 2f) + " hearts</li>");
            }
            output.println(tabs + "</ul>");
        }

        if (!group.getChance().equals(Chance.percentage(100))) {
            output.println(tabs + "Chance: " + group.getChance());
        }
        generateEffectList(output, tabs, group.getAttackerEffects(), "Attacker effects");
        generateEffectList(output, tabs, group.getVictimEffects(), "Victim effects");
    }

    private void generateEffectList(PrintWriter output, String tabs, Collection<AttackEffect> effects, String description) {
        if (!effects.isEmpty()) {
            output.println(tabs + description + ":");
            output.println(tabs + "<ul class=\"attack-effect-list\">");
            for (AttackEffect effect : effects) {
                output.println(tabs + "\t<li class=\"attack-effect\">");
                generateEffect(output, tabs + "\t\t", effect);
                output.println(tabs + "\t</li>");
            }
            output.println(tabs + "</ul>");
        }
    }

    private void generateEffect(PrintWriter output, String tabs, AttackEffect effect) {
        if (effect instanceof AttackEffectDelayedDamage) {
            AttackEffectDelayedDamage damageEffect = (AttackEffectDelayedDamage) effect;
            output.println(tabs + "Takes " + (damageEffect.getDamage() / 2f) + " hearts damage after " + damageEffect.getDelay() + " ticks");
        } else if (effect instanceof AttackEffectDropWeapon) {
            output.println(tabs + "Drops weapon or shield");
        } else if (effect instanceof AttackEffectIgnite) {
            output.println(tabs + "Is set on fire for " + ((AttackEffectIgnite) effect).getDuration() + " ticks");
        } else if (effect instanceof AttackEffectLaunchProjectile) {
            AttackEffectLaunchProjectile launchEffect = (AttackEffectLaunchProjectile) effect;
            output.println(tabs + "Is launched in direction " + launchEffect.getDirection().name().toLowerCase(Locale.ROOT) + " with speed " + launchEffect.getSpeed());
        } else if (effect instanceof AttackEffectPlaySound) {
            output.println(tabs + "Hears sound " + ((AttackEffectPlaySound) effect).getSound());
        } else if (effect instanceof AttackEffectPotion) {
            output.println(tabs + "Gets " + WikiHelper.describePotionEffect(((AttackEffectPotion) effect).getPotionEffect()));
        } else {
            output.println(tabs + "Unknown effect");
            System.err.println("Unknown effect: " + effect.getClass());
        }
    }
}
