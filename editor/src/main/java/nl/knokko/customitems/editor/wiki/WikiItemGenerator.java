package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.drop.CustomBlockDropValues;
import nl.knokko.customitems.block.drop.RequiredItemValues;
import nl.knokko.customitems.container.ContainerRecipeValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.damage.DamageSource;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.MobDropValues;
import nl.knokko.customitems.effect.ChancePotionEffectValues;
import nl.knokko.customitems.effect.EquippedPotionEffectValues;
import nl.knokko.customitems.effect.PotionEffectValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.gun.DirectGunAmmoValues;
import nl.knokko.customitems.item.gun.IndirectGunAmmoValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ShapedRecipeValues;
import nl.knokko.customitems.recipe.ShapelessRecipeValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;
import nl.knokko.customitems.recipe.result.ResultValues;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.wiki.WikiHelper.*;

public class WikiItemGenerator {

    private final ItemSet itemSet;
    private final CustomItemValues item;

    public WikiItemGenerator(ItemSet itemSet, CustomItemValues item) {
        this.itemSet = itemSet;
        this.item = item;
    }

    public void generate(File file) throws IOException {
        generateHtml(file, "items.css", stripColorCodes(item.getDisplayName()), output -> {
            // TODO Create items.css
            output.println("\t\t<h1>" + stripColorCodes(item.getDisplayName()) + "</h1>");
            output.println("\t\t<img src=\"../textures/" + item.getTexture().getName() + ".png\" width=\"64px\" /><br>");
            output.println("\t\t<h2>Information</h2>");
            output.println("\t\tInternal name: " + item.getName() + "<br>");
            if (!item.getAlias().isEmpty()) {
                output.println("\t\tAlias: " + item.getAlias() + "<br>");
            }
            output.println("\t\tMaximum stacksize: " + item.getMaxStacksize() + "<br>");
            if (!item.getLore().isEmpty()) {
                output.println("\t\tLore:");
                output.println("\t\t<ol>");
                for (String line : item.getLore()) {
                    output.println("\t\t\t<li>" + stripColorCodes(line) + "</li>");
                }
                output.println("\t\t</ol>");
            }

            generateFoodProperties(output);
            generateWandProperties(output);
            generateGunProperties(output);
            generatePocketContainerProperties(output);
            generateBlockItemProperties(output);
            generateBowProperties(output);
            generateCrossbowProperties(output);
            generateTridentProperties(output);
            generateShieldProperties(output);
            generateHoeProperties(output);
            generateShearsProperties(output);
            generateArmorProperties(output);
            generateToolProperties(output);

            if (!item.getAttributeModifiers().isEmpty() || !item.getDefaultEnchantments().isEmpty()) {
                output.println("\t\t<h2>Basic properties</h2>");
                if (!item.getAttributeModifiers().isEmpty()) {
                    output.println("\t\tAttribute modifiers:");
                    output.println("\t\t<ul>");
                    for (AttributeModifierValues attributeModifier : item.getAttributeModifiers()) {
                        output.print("\t\t\t<li>" + attributeModifier.getOperation() + " " + attributeModifier.getValue());
                        output.println(" " + attributeModifier.getAttribute() + " in " + attributeModifier.getSlot() + "</li>");
                    }
                    output.println("\t\t</ul>");
                }
                if (!item.getDefaultEnchantments().isEmpty()) {
                    output.println("\t\tDefault enchantments:");
                    output.println("\t\t<ul>");
                    for (EnchantmentValues enchantment : item.getDefaultEnchantments()) {
                        output.println("\t\t\t<li>" + enchantment.getType() + " " + enchantment.getLevel() + "</li>");
                    }
                    output.println("\t\t</ul>");
                }
            }

            boolean hasPlayerEffects = !item.getOnHitPlayerEffects().isEmpty();
            boolean hasTargetEffects = !item.getOnHitTargetEffects().isEmpty();
            boolean hasEquippedEffects = !item.getEquippedEffects().isEmpty();
            boolean hasAttackRange = item.getAttackRange() != 1f;
            boolean hasSpecialDamage = item.getSpecialMeleeDamage() != null;
            // TODO Attack effects
            if (hasPlayerEffects || hasTargetEffects || hasEquippedEffects || hasAttackRange || hasSpecialDamage || item.shouldKeepOnDeath()) {
                output.println("\t\t<h2>Special properties</h2>");
                if (hasPlayerEffects) {
                    output.println("\t\tOn-hit player potion effects:");
                    generatePotionEffects(output, item.getOnHitPlayerEffects());
                }

                if (hasTargetEffects) {
                    output.println("\t\tOn-hit target potion effects:");
                    generatePotionEffects(output, item.getOnHitTargetEffects());
                }

                if (hasEquippedEffects) {
                    output.println("\t\tEquipped potion effects:");
                    output.println("\t\t<ul>");
                    for (EquippedPotionEffectValues effect : item.getEquippedEffects()) {
                        output.println("\t\t\t<li>" + effect.getType() + " " + effect.getLevel() + " when in " + effect.getSlot() + "</li>");
                    }
                    output.println("\t\t</ul>");
                }

                if (hasAttackRange) {
                    output.println("\t\tAttack range is " + String.format("%.2f", item.getAttackRange()) + " times the default attack range<br>");
                }

                if (item.shouldKeepOnDeath()) {
                    output.println("\t\tPlayers won't lose this item upon death");
                }
            }

            Collection<BlockDropValues> blockDrops = itemSet.getBlockDrops().stream().filter(
                    blockDrop -> hasThisItem(blockDrop.getDrop().getOutputTable())
            ).collect(Collectors.toList());

            Collection<MobDropValues> mobDrops = itemSet.getMobDrops().stream().filter(
                    mobDrop -> hasThisItem(mobDrop.getDrop().getOutputTable())
            ).collect(Collectors.toList());

            Collection<CustomBlockValues> blocks = itemSet.getBlocks().stream().filter(
                    block -> block.getDrops().stream().anyMatch(
                        blockDrop -> hasThisItem(blockDrop.getItemsToDrop())
                    )
            ).collect(Collectors.toList());

            Collection<CraftingRecipeValues> craftingRecipes = itemSet.getCraftingRecipes().stream().filter(recipe -> {
                if (isThisItem(recipe.getResult())) return true;

                if (recipe instanceof ShapedRecipeValues) {
                    ShapedRecipeValues shapedRecipe = (ShapedRecipeValues) recipe;
                    for (int x = 0; x < 3; x++) {
                        for (int y = 0; y < 3; y++) {
                            if (remainsThisItem(shapedRecipe.getIngredientAt(x, y))) return true;
                        }
                    }

                    return false;
                }

                if (recipe instanceof ShapelessRecipeValues) {
                    return ((ShapelessRecipeValues) recipe).getIngredients().stream().anyMatch(this::remainsThisItem);
                }

                throw new IllegalArgumentException("Unknown crafting recipe class: " + recipe.getClass());

            }).collect(Collectors.toList());

            Map<String, Collection<ContainerRecipeValues>> containerRecipes = new HashMap<>();
            for (CustomContainerValues container : itemSet.getContainers()) {

                Collection<ContainerRecipeValues> relevantRecipes = container.getRecipes().stream().filter(
                        recipe -> isThisItem(recipe.getManualOutput()) || recipe.getOutputs().values().stream().anyMatch(
                                this::hasThisItem
                        ) || recipe.getInputs().values().stream().anyMatch(this::remainsThisItem)
                ).collect(Collectors.toList());

                if (!relevantRecipes.isEmpty()) {
                    containerRecipes.put(container.getName(), relevantRecipes);
                }
            }

            if (!blockDrops.isEmpty() || !mobDrops.isEmpty() || !blocks.isEmpty() || !craftingRecipes.isEmpty() || !containerRecipes.isEmpty()) {
                output.println("\t\t<h2>Obtaining this item</h2>");

                if (!blockDrops.isEmpty() || !blocks.isEmpty()) {
                    output.println("\t\t<h3>Dropped by blocks</h3>");
                    output.println("\t\tThis item can be obtained by breaking one of the following blocks:");

                    for (BlockDropValues blockDrop : blockDrops) {
                        output.println("\t\t<h4>" + blockDrop.getBlockType() + "</h4>");

                        generateAllowedBiomes(output, blockDrop.getDrop().getAllowedBiomes());
                        generateRequiredHeldItems(output, blockDrop.getDrop().getRequiredHeldItems());
                        generateRelevantDrops(output, "\t\t", blockDrop.getDrop().getOutputTable());
                    }

                    for (CustomBlockValues block : blocks) {
                        // TODO Link
                        output.println("\t\t<h4>" + block.getName() + "</h4>");
                        output.println("\t\t<ul>");

                        for (CustomBlockDropValues blockDrop : block.getDrops()) {
                            if (hasThisItem(blockDrop.getItemsToDrop())) {
                                output.println("\t\t\t<li>");
                                output.println("\t\t\t\tSilk touch is " + blockDrop.getSilkTouchRequirement().name().toLowerCase(Locale.ROOT) + "<br>");
                                if (blockDrop.getRequiredItems().isEnabled()) {
                                    if (blockDrop.getRequiredItems().isInverted()) {
                                        output.println("\t\t\t\tYou can use any item, <b>except</b> the following items:");
                                    } else {
                                        output.println("\t\t\t\tYou must use one of the following items:");
                                    }
                                    output.println("\t\t\t\t<ul>");
                                    for (RequiredItemValues.VanillaEntry vanilla : blockDrop.getRequiredItems().getVanillaItems()) {
                                        output.print("\t\t\t\t\t<li>" + vanilla.getMaterial());
                                        if (vanilla.shouldAllowCustomItems()) {
                                            output.print(" or a custom item of this type");
                                        }
                                        output.println("</li>");
                                    }
                                    for (ItemReference itemRef : blockDrop.getRequiredItems().getCustomItems()) {
                                        output.print("\t\t\t\t\t<a href=\"./" + itemRef.get().getName() + ".html\">");
                                        output.println(stripColorCodes(itemRef.get().getDisplayName()) + "</a></li>");
                                    }
                                    output.println("\t\t\t\t</ul>");
                                }

                                generateRelevantDrops(output, "\t\t\t\t", blockDrop.getItemsToDrop());
                                output.println("\t\t\t</li>");
                            }
                        }

                        output.println("\t\t</ul>");
                    }

                }
            }
        });
    }

    private boolean isThisItem(ResultValues candidateResult) {
        return candidateResult instanceof CustomItemResultValues && ((CustomItemResultValues) candidateResult).getItem().getName().equals(item.getName());
    }

    private boolean remainsThisItem(IngredientValues candidateIngredient) {
        return candidateIngredient.getRemainingItem() instanceof  CustomItemResultValues &&
                ((CustomItemResultValues) candidateIngredient.getRemainingItem()).getItem().getName().equals(item.getName());
    }

    private boolean hasThisItem(OutputTableValues outputTable) {
        return outputTable.getEntries().stream().anyMatch(entry -> isThisItem(entry.getResult()));
    }

    private void generatePotionEffects(PrintWriter output, Collection<ChancePotionEffectValues> effects) {
        output.println("<ul>");
        for (ChancePotionEffectValues effect : effects) {
            output.println("\t\t\t<li>" + effect.getChance() + " to get " + effect.getType());
            output.println(" " + effect.getLevel() + " for " + effect.getDuration() + " ticks</li>");
        }
        output.println("</ul>");
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
                output.println("\t\t<ul>");
                for (PotionEffectValues effect : food.getEatEffects()) {
                    output.println("\t\t\t<li>" + effect.getType() + " " + effect.getLevel() + " for " + effect.getDuration() + " ticks</li>");
                }
                output.println("\t\t</ul>");
            }
        }
    }

    private void generateWandOrGunProperties(
            PrintWriter output, CustomProjectileValues projectile, int amountPerShot, int cooldown, String action) {
        // TODO Create a link to the projectile
        output.println("\t\tProjectile: " + projectile.getName() + "<br>");
        if (amountPerShot != 1) {
            output.println("\t\tFires " + amountPerShot + " projectiles per " + action + "<br>");
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
        }
    }

    private void generateGunProperties(PrintWriter output) {
        if (item instanceof CustomGunValues) {
            CustomGunValues gun = (CustomGunValues) item;

            output.println("\t\t<h2>Gun</h2>");
            generateWandOrGunProperties(output, gun.getProjectile(), gun.getAmountPerShot(), gun.getAmmo().getCooldown(), "shot");

            if (gun.getAmmo() instanceof DirectGunAmmoValues) {
                output.println("\t\tUses " + createTextBasedIngredientHtml(((DirectGunAmmoValues) gun.getAmmo()).getAmmoItem(), "../") + " as ammo<br>");
            }
            if (gun.getAmmo() instanceof IndirectGunAmmoValues) {
                IndirectGunAmmoValues ammo = (IndirectGunAmmoValues) gun.getAmmo();
                output.println("\t\t<h3>Ammo</h3>");
                output.println("\t\tReload item: " + createTextBasedIngredientHtml(ammo.getReloadItem(), "../") + "<br>");
                output.println("\t\tMaximum stored ammo: " + ammo.getStoredAmmo() + "<br>");
                output.println("\t\tReload time: " + ammo.getReloadTime() + " ticks<br>");
            }
        }
    }

    private void generatePocketContainerProperties(PrintWriter output) {
        if (item instanceof CustomPocketContainerValues) {

            CustomPocketContainerValues pocketContainer = (CustomPocketContainerValues) item;
            output.println("\t\t<h2>Pocket containers<h2>");
            output.println("\t\t<ul>");
            for (CustomContainerValues container : pocketContainer.getContainers()) {
                // TODO Add link
                output.println("\t\t\t<li>" + container.getName() + "</li>");
            }
            output.println("\t\t</ul>");
        }
    }

    private void generateBlockItemProperties(PrintWriter output) {
        if (item instanceof CustomBlockItemValues) {
            // TODO Add link
            output.println("\t\tPlaces block " + ((CustomBlockItemValues) item).getBlock().getName());
        }
    }

    private void generateBowOrCrossbowProperties(
            PrintWriter output, double arrowDamageMultiplier, double fireworkDamageMultiplier,
            double arrowSpeedMultiplier, double fireworkSpeedMultiplier,
            int arrowKnockbackStrength, int arrowDurabilityLoss, boolean hasArrowGravity
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
    }

    private void generateBowProperties(PrintWriter output) {
        if (item instanceof CustomBowValues) {
            CustomBowValues bow = (CustomBowValues) item;

            output.println("\t\t<h2>Bow</h2>");
            generateBowOrCrossbowProperties(
                    output, bow.getDamageMultiplier(), 1.0, bow.getSpeedMultiplier(), 1.0,
                    bow.getKnockbackStrength(), bow.getShootDurabilityLoss(), bow.hasGravity()
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
                    crossbow.getArrowKnockbackStrength(), crossbow.getArrowDurabilityLoss(), crossbow.hasArrowGravity()
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
        }
    }

    private void generateShieldProperties(PrintWriter output) {
        if (item instanceof CustomShieldValues) {
            CustomShieldValues shield = (CustomShieldValues) item;
            output.println("\t\t<h2>Shield</h2>");

            output.println("\t\tThreshold damage for durability loss: " + shield.getThresholdDamage());
            // TODO Blocking effects
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

    private void generateArmorProperties(PrintWriter output) {
        if (item instanceof CustomArmorValues) {
            CustomArmorValues armor = (CustomArmorValues) item;
            if (!armor.getDamageResistances().equals(new DamageResistanceValues(false))) {
                output.println("\t\t<h2>Armor damage resistances</h2>");
                output.println("\t\t<ul>");
                for (DamageSource damageSource : DamageSource.values()) {
                    short resistance = armor.getDamageResistances().getResistance(damageSource);
                    if (resistance != 0) {
                        output.println("\t\t\t<li>" + resistance + "% resistance against " + damageSource + " damage</li>");
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
                if (!(tool.getRepairItem() instanceof NoIngredientValues)) {
                    output.println("\t\tThis item can be repaired using " + createTextBasedIngredientHtml(tool.getRepairItem(), "../") + "<br>");
                }
            } else {
                output.println("\t\tThis item can't be manipulated in an anvil<br>");
            }
        }
    }
}
