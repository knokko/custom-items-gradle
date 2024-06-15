package nl.knokko.customitems.editor.wiki.item;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.editor.wiki.WikiDamageSourceGenerator;
import nl.knokko.customitems.editor.wiki.WikiProtector;
import nl.knokko.customitems.effect.PotionEffectValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.gun.DirectGunAmmoValues;
import nl.knokko.customitems.item.gun.IndirectGunAmmoValues;
import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;

import java.io.PrintWriter;

import static nl.knokko.customitems.editor.wiki.WikiHelper.*;

class ItemSubclassGenerator {

    private final CustomItemValues item;

    ItemSubclassGenerator(CustomItemValues item) {
        this.item = item;
    }

    void generate(PrintWriter output, ItemSet itemSet) {
        generateFoodProperties(output);
        generateMusicDiscProperties(output);
        generateWandProperties(output);
        generateGunProperties(output);
        generatePocketContainerProperties(output);
        generateBlockItemProperties(output);
        generateArrowProperties(output);
        generateBowProperties(output);
        generateCrossbowProperties(output);
        generateTridentProperties(output);
        generateShieldProperties(output);
        generateHoeProperties(output);
        generateShearsProperties(output);
        generateElytraProperties(output);
        generateArmorProperties(output, itemSet);
        generateToolProperties(output);
    }

    private void generateFoodProperties(PrintWriter output) {
        if (item instanceof CustomFoodValues) {
            CustomFoodValues food = (CustomFoodValues) item;

            output.println("\t\t<h2>Food</h2>");
            if (food.getFoodValue() != 0) {
                output.println("\t\tRestores " + food.getFoodValue() + " half hunger bar chunks<br>");
            }
            if (!food.getEatEffects().isEmpty()) {
                output.println("\t\tEat effects:");
                output.println("\t\t<ul class=\"eat-effects\">");
                for (PotionEffectValues effect : food.getEatEffects()) {
                    output.println("\t\t\t<li class=\"eat-effect\">" + describePotionEffect(effect) + "</li>");
                }
                output.println("\t\t</ul>");
            }
        }
    }

    private void generateMusicDiscProperties(PrintWriter output) {
        if (item instanceof CustomMusicDiscValues) {
            output.println("\t\t<h2>Music</h2>");
            CustomMusicDiscValues musicDisc = (CustomMusicDiscValues) item;
            generateAudio(output, "\t\t", "../", musicDisc.getMusic());
        }
    }

    private void generateWandOrGunProperties(
            PrintWriter output, CustomProjectileValues projectile, int amountPerShot, int cooldown, String action) {
        if (projectile != null) {
            output.println("\t\tProjectile: <a href=\"../projectiles/" + projectile.getName() + ".html\">" + projectile.getName() + "</a><br>");
            if (amountPerShot != 1) {
                output.println("\t\tFires " + amountPerShot + " projectiles per " + action + "<br>");
            }
        }
        if (cooldown > 1) {
            output.println("\t\tCooldown: " + cooldown + " ticks<br>");
        }
    }

    private void generateWandProperties(PrintWriter output) {
        if (item instanceof CustomWandValues) {
            CustomWandValues wand = (CustomWandValues) item;

            output.println("\t\t<h2>Wand</h2>");
            generateWandOrGunProperties(output, wand.getProjectile(), wand.getAmountPerShot(), wand.getCooldown(), "swing");
            if (wand.getCharges() != null) {
                output.println("\t\t" + wand.getCharges().getMaxCharges() + " with " + wand.getCharges().getRechargeTime() + " ticks recharge time<br>");
            }
            if (wand.getManaCost() > 0f) {
                output.println(String.format("\t\tUses %.2f mana per swing<br>", wand.getManaCost()));
            }
            if (!wand.getMagicSpells().isEmpty()) {
                output.println("\t\tThis wand has the following spells:");
                output.println("\t\t<ul>");
                for (String spell : wand.getMagicSpells()) output.println("\t\t\t<li>" + spell + "</li>");
                output.println("\t\t</ul>");
            }
            if (wand.requiresPermission()){
                output.println("\t\t<h3>Permissions: </h3>");
                output.println("\t\tPlayers need <b>customitems.shootall</b> or <b>customitems.shoot." +item.getName() + "</b> to use this wand.");
            }
        }
    }

    private void generateGunProperties(PrintWriter output) {
        if (item instanceof CustomGunValues) {
            CustomGunValues gun = (CustomGunValues) item;

            output.println("\t\t<h2>Gun</h2>");
            generateWandOrGunProperties(output, gun.getProjectile(), gun.getAmountPerShot(), gun.getAmmo().getCooldown(), "shot");

            if (gun.getAmmo() instanceof DirectGunAmmoValues) {
                IngredientValues ammoItem = ((DirectGunAmmoValues) gun.getAmmo()).getAmmoItem();
                if (!WikiProtector.isIngredientSecret(ammoItem)) {
                    output.println("\t\tUses " + createTextBasedIngredientHtml(ammoItem, "../") + " as ammo<br>");
                }
            }
            if (gun.getAmmo() instanceof IndirectGunAmmoValues) {
                IndirectGunAmmoValues ammo = (IndirectGunAmmoValues) gun.getAmmo();
                output.println("\t\t<h3>Ammo</h3>");
                if (!WikiProtector.isIngredientSecret(ammo.getReloadItem())) {
                    output.println("\t\tReload item: " + createTextBasedIngredientHtml(ammo.getReloadItem(), "../") + "<br>");
                }
                output.println("\t\tMaximum stored ammo: " + ammo.getStoredAmmo() + "<br>");
                output.println("\t\tReload time: " + ammo.getReloadTime() + " ticks<br>");
            }
            if(gun.requiresPermission()){
                output.println("\t\t<h3>Permissions: </h3>");
                output.println("\t\tPlayers need <b>customitems.shootall</b> or <b>customitems.shoot."+item.getName()+"</b> to shoot with this gun.");
            }
        }
    }

    private void generatePocketContainerProperties(PrintWriter output) {
        if (item instanceof CustomPocketContainerValues) {

            CustomPocketContainerValues pocketContainer = (CustomPocketContainerValues) item;
            output.println("\t\t<h2>Pocket containers<h2>");
            output.println("\t\t<ul class=\"pocket-containers\">");
            for (CustomContainerValues container : pocketContainer.getContainers()) {
                output.println("\t\t\t<li class=\"pocket-container\"><a href=\"../containers/" + container.getName() +
                        ".html\">" + getDisplayName(container) + "</a></li>");
            }
            output.println("\t\t</ul>");
        }
    }

    private void generateBlockItemProperties(PrintWriter output) {
        if (item instanceof CustomBlockItemValues) {
            String blockName = ((CustomBlockItemValues) item).getBlock().getName();
            output.println("\t\tPlaces block <a href=\"../blocks/" + blockName + ".html\">" + blockName + "</a>");
        }
    }

    private void generateBowOrCrossbowProperties(
            PrintWriter output, double arrowDamageMultiplier, double fireworkDamageMultiplier,
            double arrowSpeedMultiplier, double fireworkSpeedMultiplier,
            int arrowKnockbackStrength, int arrowDurabilityLoss,
            boolean hasArrowGravity, CustomDamageSourceReference customShootDamageSource
    ) {
        if (arrowDamageMultiplier != 1.0) {
            output.print("\t\tArrows deal " + String.format("%.2f", arrowDamageMultiplier));
            output.println(" times the default damage <br>");
        }
        if (fireworkDamageMultiplier != 1.0) {
            output.print("\t\tFirework rockets fired with this crossbow deal " + String.format("%.2f", fireworkDamageMultiplier));
            output.println(" times the default damage<br>");
        }
        if (arrowSpeedMultiplier != 1.0) {
            output.println("\t\tArrows fly " + String.format("%.2f", arrowSpeedMultiplier) + " times as fast<br>");
        }
        if (fireworkSpeedMultiplier != 1.0) {
            output.println("\t\tFirework rockets fly " + String.format("%.2f", fireworkSpeedMultiplier) + " times as fast <br>");
        }
        if (arrowKnockbackStrength != 0) {
            output.println("\t\tArrows have " + arrowKnockbackStrength + " knockback strength<br>");
        }
        if (!hasArrowGravity) {
            output.println("\t\tArrows ignore gravity<br>");
        }

        if (((CustomToolValues) item).getMaxDurabilityNew() != null) {
            output.println("\t\tFiring an arrow decreases the durability by " + arrowDurabilityLoss + "<br>");
        }
        if (customShootDamageSource != null) {
            output.println("\t\tDeals " + WikiDamageSourceGenerator.createLink(customShootDamageSource, "../")
                    + " damage<br>");
        }
    }

    private void generateArrowProperties(PrintWriter output) {
        if (item instanceof CustomArrowValues) {
            CustomArrowValues arrow = (CustomArrowValues) item;

            output.println("\t\t<h2>Arrow</h2>");
            if (arrow.getDamageMultiplier() != 1f) {
                output.println("\t\tThis arrow deals " + String.format("%.2f", arrow.getDamageMultiplier())
                        + " times the damage of normal arrows<br>");
            }
            if (arrow.getSpeedMultiplier() != 1f) {
                output.println("\t\tThis arrow flies " + String.format("%.2f", arrow.getSpeedMultiplier())
                        + " times as fast as normal arrows<br>");
            }
            if (arrow.getKnockbackStrength() != 0) {
                output.println("\t\tThis arrow has " + arrow.getKnockbackStrength() + " knockback strength<br>");
            }
            if (!arrow.shouldHaveGravity()) {
                output.println("\t\tThis arrow ignores gravity<br>");
            }
            if (arrow.getCustomShootDamageSourceReference() != null) {
                output.println("\t\tThis arrow deals "
                        + WikiDamageSourceGenerator.createLink(arrow.getCustomShootDamageSourceReference(), "../")
                        + " damage<br>"
                );
            }
            if (!arrow.getShootEffects().isEmpty()) {
                output.println("\t\tSpecial shoot effects:");
                new AttackEffectsGenerator(arrow.getShootEffects()).generate(output, "\t\t");
            }
        }
    }

    private void generateBowProperties(PrintWriter output) {
        if (item instanceof CustomBowValues) {
            CustomBowValues bow = (CustomBowValues) item;

            output.println("\t\t<h2>Bow</h2>");
            generateBowOrCrossbowProperties(
                    output, bow.getDamageMultiplier(), 1.0, bow.getSpeedMultiplier(),
                    1.0, bow.getKnockbackStrength(), bow.getShootDurabilityLoss(),
                    bow.hasGravity(), bow.getCustomShootDamageSourceReference()
            );
        }
    }

    private void generateCrossbowProperties(PrintWriter output) {
        if (item instanceof CustomCrossbowValues) {
            CustomCrossbowValues crossbow = (CustomCrossbowValues) item;

            output.println("\t\t<h2>Crossbow</h2>");
            generateBowOrCrossbowProperties(
                    output, crossbow.getArrowDamageMultiplier(), crossbow.getFireworkDamageMultiplier(),
                    crossbow.getArrowSpeedMultiplier(), crossbow.getFireworkSpeedMultiplier(),
                    crossbow.getArrowKnockbackStrength(), crossbow.getArrowDurabilityLoss(),
                    crossbow.hasArrowGravity(), crossbow.getCustomShootDamageSourceReference()
            );
            if (crossbow.getMaxDurabilityNew() != null) {
                output.println("\t\tFiring a firework rocket decreases the durability by " + crossbow.getFireworkDurabilityLoss() + "<br>");
            }
        }
    }

    private void generateTridentProperties(PrintWriter output) {
        if (item instanceof CustomTridentValues) {
            CustomTridentValues trident = (CustomTridentValues) item;

            output.println("\t\t<h2>Trident</h2>");
            if (trident.getThrowDamageMultiplier() != 1.0) {
                output.print("\t\tThis trident deals " + String.format("%.2f", trident.getThrowDamageMultiplier()));
                output.println(" times the default damage when thrown<br>");
            }
            if (trident.getThrowSpeedMultiplier() != 1.0) {
                output.print("\t\tThis trident can be thrown " + String.format("%.2f", trident.getThrowSpeedMultiplier()));
                output.println(" times as fast as regular tridents.<br>");
            }
            if (trident.getMaxDurabilityNew() != null) {
                output.println("\t\tThrowing this trident will decrease its durability by " + trident.getThrowDurabilityLoss() + "<br>");
            }
            if (trident.getCustomThrowDamageSourceReference() != null) {
                output.println("\t\tDeals "
                        + WikiDamageSourceGenerator.createLink(trident.getCustomThrowDamageSourceReference(), "../")
                        + " damage (thrown)<br>"
                );
            }
        }
    }

    private void generateShieldProperties(PrintWriter output) {
        if (item instanceof CustomShieldValues) {
            CustomShieldValues shield = (CustomShieldValues) item;
            output.println("\t\t<h2>Shield</h2>");

            output.println("\t\tThreshold damage for durability loss: " + shield.getThresholdDamage() + "<br>");
            if (!shield.getBlockingEffects().isEmpty()) {
                output.println("\t\tBlocking effects:");
                new AttackEffectsGenerator(shield.getBlockingEffects()).generate(output, "\t\t");
            }
        }
    }

    private void generateHoeProperties(PrintWriter output) {
        if (item instanceof CustomHoeValues) {
            CustomHoeValues hoe = (CustomHoeValues) item;
            if (hoe.getMaxDurabilityNew() != null) {
                output.println("\t\tTilling dirt decreases its durability by " + hoe.getTillDurabilityLoss() + "<br>");
            }
        }
    }

    private void generateShearsProperties(PrintWriter output) {
        if (item instanceof CustomShearsValues) {
            CustomShearsValues shears = (CustomShearsValues) item;
            if (shears.getMaxDurabilityNew() != null) {
                output.println("\t\tShearing sheep decreases its durability by " + shears.getShearDurabilityLoss() + "<br>");
            }
        }
    }

    private void generateElytraProperties(PrintWriter output) {
        if (item instanceof CustomElytraValues) {
            if (!((CustomElytraValues) item).getVelocityModifiers().isEmpty()) {
                output.println("\t\t<p>This elytra has custom gliding mechanics</p>");
            }
        }
    }

    private void generateArmorProperties(PrintWriter output, ItemSet itemSet) {
        if (item instanceof CustomArmorValues) {
            CustomArmorValues armor = (CustomArmorValues) item;
            if (!armor.getDamageResistances().equals(new DamageResistanceValues(false))) {
                output.println("\t\t<h2>Armor damage resistances</h2>");
                output.println("\t\t<ul class=\"armor-damage-resistances\">");
                for (DamageSource damageSource : DamageSource.values()) {
                    short resistance = armor.getDamageResistances().getResistance(damageSource);
                    if (resistance != 0) {
                        output.println("\t\t\t<li class=\"armor-damage-resistance\">" + resistance +
                                "% resistance against " + damageSource + " damage</li>");
                    }
                }
                for (CustomDamageSourceReference damageSource : itemSet.getDamageSources().references()) {
                    short resistance = armor.getDamageResistances().getResistance(damageSource);
                    if (resistance != 0) {
                        output.println("\t\t\t<li class=\"armor-damage-resistance\">" + resistance + "% resistance against "
                                + WikiDamageSourceGenerator.createLink(damageSource, "../") + " damage</li>"
                        );
                    }
                }
                output.println("\t\t</ul>");
            }
        }
    }

    private void generateToolProperties(PrintWriter output) {
        if (item instanceof CustomToolValues) {
            CustomToolValues tool = (CustomToolValues) item;

            output.println("\t\t<h2>Tool properties</h2>");
            if (tool.getMaxDurabilityNew() != null) {
                output.println("\t\tMaximum durability: " + tool.getMaxDurabilityNew() + "<br>");
                output.println("\t\tBreaking blocks decreases its durability by " + tool.getBlockBreakDurabilityLoss() + "<br>");
                output.println("\t\tHitting entities decreases its durability by " + tool.getEntityHitDurabilityLoss() + "<br>");
            } else {
                output.println("\t\tThis item is unbreakable<br>");
            }

            if (!tool.allowEnchanting() && tool.getDefaultEnchantments().isEmpty()) {
                output.println("\t\tThis item can't be enchanted<br>");
            }
            if (tool.allowAnvilActions()) {
                if (!(tool.getRepairItem() instanceof NoIngredientValues) && !WikiProtector.isIngredientSecret(tool.getRepairItem())) {
                    output.println("\t\tThis item can be repaired using " + createTextBasedIngredientHtml(tool.getRepairItem(), "../") + "<br>");
                }
            } else {
                output.println("\t\tThis item can't be manipulated in an anvil<br>");
            }
        }
    }
}
