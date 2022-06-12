package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.effect.PotionEffectValues;
import nl.knokko.customitems.item.CustomGunValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomWandValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.effect.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.wiki.WikiHelper.*;

class WikiProjectileGenerator {

    private final ItemSet itemSet;
    private final CustomProjectileValues projectile;

    WikiProjectileGenerator(ItemSet itemSet, CustomProjectileValues projectile) {
        this.itemSet = itemSet;
        this.projectile = projectile;
    }

    void generate(File destination) throws IOException {
        generateHtml(destination, "../projectiles.css", projectile.getName(), output -> {
            output.println("\t\t<h1>" + projectile.getName() + "</h1>");

            output.println("\t\t<h2>Basic properties</h2>");
            output.println("\t\tImpact damage: " + projectile.getDamage() + "<br>");
            output.println("\t\tLaunch angle: " + projectile.getMinLaunchAngle() + " to " + projectile.getMaxLaunchAngle() + " degrees<br>");
            output.println("\t\tLaunch speed: " + projectile.getMinLaunchSpeed() + " to " + projectile.getMaxLaunchSpeed() + " meters per tick<br>");
            output.println("\t\tMaximum lifetime: " + projectile.getMaxLifetime() + " ticks<br>");
            output.println("\t\tGravity: " + projectile.getGravity() + " meters per tick per tick<br>");
            output.println("\t\tLaunch knockback: " + projectile.getLaunchKnockback() + " meters per tick<br>");
            output.println("\t\tImpact knockback: " + projectile.getImpactKnockback() + " meters per tick<br>");

            if (!projectile.getImpactPotionEffects().isEmpty() || !projectile.getImpactEffects().isEmpty()) {
                output.println("\t\t<h2>Impact effects</h2>");
                output.println("\t\t<ul>");
                for (PotionEffectValues potionEffect : projectile.getImpactPotionEffects()) {
                    output.println("\t\t\t<li>Give " + describePotionEffect(potionEffect) + "</li>");
                }
                for (ProjectileEffectValues effect : projectile.getImpactEffects()) {
                    generateProjectileEffect(output, "\t\t\t", effect);
                }
                output.println("\t\t</ul>");
            }

            if (!projectile.getInFlightEffects().isEmpty()) {
                output.println("\t\t<h2>In-flight effects</h2>");
                output.println("\t\t<ul>");
                for (ProjectileEffectsValues effectWave : projectile.getInFlightEffects()) {
                    output.println("\t\t\t<li>");
                    output.println("\t\t\t\tThe following effects will be executed once every " + effectWave.getPeriod() + " ticks.");
                    output.println("\t\t\t\tThe first time will be " + effectWave.getDelay() + " ticks after the projectile is launched.");
                    output.println("\t\t\t\t<ul>");
                    for (ProjectileEffectValues effect : effectWave.getEffects()) {
                        generateProjectileEffect(output, "\t\t\t\t\t", effect);
                    }
                    output.println("\t\t\t\t</ul>");
                    output.println("\t\t\t</li>");
                }
                output.println("\t\t</ul>");
            }

            Collection<CustomItemValues> gunsAndWands = itemSet.getItems().stream().filter(candidateItem -> {
                if (candidateItem instanceof CustomWandValues) {
                    return ((CustomWandValues) candidateItem).getProjectile().getName().equals(projectile.getName());
                } else if (candidateItem instanceof CustomGunValues) {
                    return ((CustomGunValues) candidateItem).getProjectile().getName().equals(projectile.getName());
                } else {
                    return false;
                }
            }).collect(Collectors.toList());

            if (!gunsAndWands.isEmpty()) {
                output.println("\t\t<h2>Items that can launch this projectile</h2>");
                output.println("\t\t<ul>");
                for (CustomItemValues item : gunsAndWands) {
                    output.println("\t\t\t<li><a href=\"../items/" + item.getName() + ".html\">");
                    output.println("\t\t\t\t<img src=\"../textures/" + item.getTexture().getName() + ".png\" class=\"item-icon\" />");
                    output.println("\t\t\t\t" + stripColorCodes(item.getDisplayName()));
                    output.println("\t\t\t</a></li>");
                }
            }

            Collection<CustomProjectileValues> parentProjectiles = itemSet.getProjectiles().stream().filter(candidateProjectile -> {
               return candidateProjectile.getInFlightEffects().stream().anyMatch(effectWaves -> {
                   return effectWaves.getEffects().stream().anyMatch(effect -> {
                       return effect instanceof SubProjectilesValues && ((SubProjectilesValues) effect).getChild().getName().equals(projectile.getName());
                   });
               }) || candidateProjectile.getImpactEffects().stream().anyMatch(effect -> {
                   return effect instanceof SubProjectilesValues && ((SubProjectilesValues) effect).getChild().getName().equals(projectile.getName());
               });
            }).collect(Collectors.toList());

            if (!parentProjectiles.isEmpty()) {
                output.println("\t\t<h2>Projectiles that can spawn this projectile</h2>");
                output.println("\t\t<ul>");
                for (CustomProjectileValues parentProjectile : parentProjectiles) {
                    output.println("\t\t\t<li><a href=\"." + parentProjectile.getName() + ".html\">" + parentProjectile.getName() + "</a></li>");
                }
                output.println("\t\t</ul>");
            }
        });
    }

    private void generateProjectileEffect(PrintWriter output, String tabs, ProjectileEffectValues effect) {
        if (effect instanceof ColoredRedstoneValues) {
            output.println(tabs + "<li>Spawn " + ((ColoredRedstoneValues) effect).getAmount() + " colored redstone particles</li>");
        } else if (effect instanceof ExecuteCommandValues) {
            output.println(tabs + "<li>Execute this command: " + ((ExecuteCommandValues) effect).getCommand() + "</li>");
        } else if (effect instanceof ExplosionValues) {
            ExplosionValues explosion = (ExplosionValues) effect;
            output.println(tabs + "<li>Create an explosion with power " + explosion.getPower() +
                    (explosion.setsFire() ? " that sets fire" : "") + "</li>");
        } else if (effect instanceof PlaySoundValues) {
            output.println(tabs + "<li>Play the " + NameHelper.getNiceEnumName(((PlaySoundValues) effect).getSound().name()) + " sound</li>");
        } else if (effect instanceof PotionAuraValues) {
            PotionAuraValues aura = (PotionAuraValues) effect;
            output.println(tabs + "<li>");
            output.println(tabs + "\tGives the following potion effects to everyone within " + aura.getRadius() + " meters:");
            output.println(tabs + "\t<ul>");
            for (PotionEffectValues auraEffect : aura.getEffects()) {
                output.println(tabs + "\t\t<li>" + describePotionEffect(auraEffect) + "</li>");
            }
            output.println(tabs + "\t</ul>");
        } else if (effect instanceof PushOrPullValues) {
            PushOrPullValues pushOrPull = (PushOrPullValues) effect;
            if (pushOrPull.getStrength() > 0f) {
                output.println(tabs + "<li>Pushes everyone within " + pushOrPull.getRadius() +
                        " meters away with strength " + pushOrPull.getStrength() + "</li>");
            } else {
                output.println(tabs + "<li>Pulls everyone within " + pushOrPull.getRadius() +
                        " meters towards the projectile with strength " + (-pushOrPull.getStrength()) + "</li>");
            }
        } else if (effect instanceof RandomAccelerationValues) {
            RandomAccelerationValues acceleration = (RandomAccelerationValues) effect;
            output.println(tabs + "<li>Accelerates the projectile between " + acceleration.getMinAcceleration() + " and " +
                    acceleration.getMaxAcceleration() + " meters / tick / tick towards a random direction</li>");
        } else if (effect instanceof ShowFireworkValues) {
            output.println(tabs + "<li>Creates " + ((ShowFireworkValues) effect).getEffects().size() + " firework effects</li>");
        } else if (effect instanceof SimpleParticleValues) {
            SimpleParticleValues particles = (SimpleParticleValues) effect;
            output.println(tabs + "<li>Spawns " + particles.getAmount() + " " +
                    NameHelper.getNiceEnumName(particles.getParticle().name()) + " particles</li>");
        } else if (effect instanceof StraightAccelerationValues) {
            StraightAccelerationValues acceleration = (StraightAccelerationValues) effect;
            output.println(tabs + "<li>Accelerates the projectile between " + acceleration.getMinAcceleration() + " and " +
                    acceleration.getMaxAcceleration() + " meters / tick / tick forward</li>");
        } else if (effect instanceof SubProjectilesValues) {
            SubProjectilesValues subProjectiles = (SubProjectilesValues) effect;
            String childName = subProjectiles.getChild().getName();
            output.println(tabs + "<li>Spawns " + subProjectiles.getMinAmount() + " to " + subProjectiles.getMaxAmount() +
                    " <a href=\"." + childName + ".html\">" + childName + "</a> projectiles</li>");
        } else {
            output.println(tabs + "<li>An unknown effect. This is probably a bug.</li>");
        }
    }
}
