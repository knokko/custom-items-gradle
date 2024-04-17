package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.effect.KciPotionEffect;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.customitems.projectile.effect.*;
import nl.knokko.customitems.sound.KciSound;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.wiki.WikiHelper.*;
import static nl.knokko.customitems.util.ColorCodes.stripColorCodes;

class WikiProjectileGenerator {

    private final ItemSet itemSet;
    private final KciProjectile projectile;

    WikiProjectileGenerator(ItemSet itemSet, KciProjectile projectile) {
        this.itemSet = itemSet;
        this.projectile = projectile;
    }

    void generate(File destination) throws IOException {
        generateHtml(destination, "../projectiles.css", projectile.getName(), output -> {
            output.println("\t\t<h1>" + projectile.getName() + "</h1>");

            output.println("\t\t<h2 id=\"basic-properties-header\">Basic properties</h2>");
            output.println("\t\tImpact damage: " + projectile.getDamage() + "<br>");
            output.println("\t\tLaunch angle: " + projectile.getMinLaunchAngle() + " to " + projectile.getMaxLaunchAngle() + " degrees<br>");
            output.println("\t\tLaunch speed: " + projectile.getMinLaunchSpeed() + " to " + projectile.getMaxLaunchSpeed() + " meters per tick<br>");
            output.println("\t\tMaximum lifetime: " + projectile.getMaxLifetime() + " ticks<br>");
            output.println("\t\tGravity: " + projectile.getGravity() + " meters per tick per tick<br>");
            output.println("\t\tLaunch knockback: " + projectile.getLaunchKnockback() + " meters per tick<br>");
            output.println("\t\tImpact knockback: " + projectile.getImpactKnockback() + " meters per tick<br>");
            if (projectile.getMaxPiercedEntities() == 1) output.println("\t\tCan pierce through 1 entity<br>");
            if (projectile.getMaxPiercedEntities() > 1) {
                output.println("\t\tCan pierce through " + projectile.getMaxPiercedEntities() + " entities<br>");
            }
            if (projectile.getCustomDamageSourceReference() != null) {
                output.println("\t\tDeals "
                        + WikiDamageSourceGenerator.createLink(projectile.getCustomDamageSourceReference(), "../")
                        + " damage<br>"
                );
            }

            if (!projectile.getImpactPotionEffects().isEmpty() || !projectile.getImpactEffects().isEmpty()) {
                output.println("\t\t<h2 id=\"impact-effects-header\">Impact effects</h2>");
                output.println("\t\t<ul class=\"impact-effects\">");
                for (KciPotionEffect potionEffect : projectile.getImpactPotionEffects()) {
                    output.println("\t\t\t<li class=\"impact-potion-effect\">Give "
                            + describePotionEffect(potionEffect) + "</li>");
                }
                for (ProjectileEffect effect : projectile.getImpactEffects()) {
                    generateProjectileEffect(output, "\t\t\t", effect);
                }
                output.println("\t\t</ul>");
                if (projectile.shouldApplyImpactEffectsAtExpiration()) {
                    output.println("\t\tThese impact effects are also activated when the projectile expires<br>");
                }
                if (projectile.getMaxPiercedEntities() > 0 && projectile.shouldApplyImpactEffectsAtPierce()) {
                    output.println("\t\tThese impact effects are also activated when the projectile pierces an entity<br>");
                }
            }

            if (!projectile.getInFlightEffects().isEmpty()) {
                output.println("\t\t<h2 id=\"in-flight-effects-header\">In-flight effects</h2>");
                output.println("\t\t<ul class=\"in-flight-waves\">");
                for (ProjectileEffects effectWave : projectile.getInFlightEffects()) {
                    output.println("\t\t\t<li class=\"in-flight-wave\">");
                    output.println("\t\t\t\tThe following effects will be executed once every " + effectWave.getPeriod() + " ticks.");
                    output.println("\t\t\t\tThe first time will be " + effectWave.getDelay() + " ticks after the projectile is launched.");
                    output.println("\t\t\t\t<ul class=\"in-flight-effects\">");
                    for (ProjectileEffect effect : effectWave.getEffects()) {
                        generateProjectileEffect(output, "\t\t\t\t\t", effect);
                    }
                    output.println("\t\t\t\t</ul>");
                    output.println("\t\t\t</li>");
                }
                output.println("\t\t</ul>");
            }

            Collection<KciItem> sources = itemSet.items.stream().filter(candidateItem -> {
                if (candidateItem instanceof KciWand) {
                    KciProjectile wandProjectile = ((KciWand) candidateItem).getProjectile();
                    return wandProjectile != null && wandProjectile.getName().equals(projectile.getName());
                } else if (candidateItem instanceof KciGun) {
                    return ((KciGun) candidateItem).getProjectile().getName().equals(projectile.getName());
                } else if (candidateItem instanceof KciThrowable) {
                    return ((KciThrowable) candidateItem).getProjectile().getName().equals(projectile.getName());
                } else {
                    return false;
                }
            }).filter(candidateItem -> candidateItem.getWikiVisibility() == WikiVisibility.VISIBLE).collect(Collectors.toList());

            if (!sources.isEmpty()) {
                output.println("\t\t<h2 id=\"launch-items-header\">Items that can launch this projectile</h2>");
                output.println("\t\t<ul class=\"launch-items\">");
                for (KciItem item : sources) {
                    output.println("\t\t\t<li class=\"launch-item\"><a href=\"../items/" + item.getName() + ".html\">");
                    output.println("\t\t\t\t<img src=\"../textures/" + item.getTexture().getName() + ".png\" class=\"item-icon\" />");
                    output.println("\t\t\t\t" + stripColorCodes(item.getDisplayName()));
                    output.println("\t\t\t</a></li>");
                }
            }

            Collection<KciProjectile> parentProjectiles = itemSet.projectiles.stream().filter(candidateProjectile -> {
               return candidateProjectile.getInFlightEffects().stream().anyMatch(effectWaves -> {
                   return effectWaves.getEffects().stream().anyMatch(effect -> {
                       return effect instanceof PESubProjectiles && ((PESubProjectiles) effect).getChild().getName().equals(projectile.getName());
                   });
               }) || candidateProjectile.getImpactEffects().stream().anyMatch(effect -> {
                   return effect instanceof PESubProjectiles && ((PESubProjectiles) effect).getChild().getName().equals(projectile.getName());
               });
            }).collect(Collectors.toList());

            if (!parentProjectiles.isEmpty()) {
                output.println("\t\t<h2 id=\"parent-projectiles-header\">Projectiles that can spawn this projectile</h2>");
                output.println("\t\t<ul class=\"parent-projectiles\">");
                for (KciProjectile parentProjectile : parentProjectiles) {
                    output.println("\t\t\t<li class=\"parent-projectile\"><a href=\"." + parentProjectile.getName() +
                            ".html\">" + parentProjectile.getName() + "</a></li>");
                }
                output.println("\t\t</ul>");
            }
        });
    }

    private void generateProjectileEffect(PrintWriter output, String tabs, ProjectileEffect effect) {
        if (effect instanceof PEColoredRedstone) {
            output.println(tabs + "<li class=\"projectile-effect\">Spawn " + ((PEColoredRedstone) effect).getAmount()
                    + " colored redstone particles</li>");
        } else if (effect instanceof PEExecuteCommand) {
            output.println(tabs + "<li class=\"projectile-effect\">Execute this command: " +
                    ((PEExecuteCommand) effect).getCommand() + "</li>");
        } else if (effect instanceof PECreateExplosion) {
            PECreateExplosion explosion = (PECreateExplosion) effect;
            output.println(tabs + "<li class=\"projectile-effect\">Create an explosion with power " + explosion.getPower() +
                    (explosion.setsFire() ? " that sets fire" : "") + "</li>");
        } else if (effect instanceof PEPlaySound) {
            KciSound sound = ((PEPlaySound) effect).getSound();

            String soundName;
            if (sound.getVanillaSound() != null) soundName = NameHelper.getNiceEnumName(sound.getVanillaSound().name());
            else soundName = sound.getCustomSound().getName();

            output.println(tabs + "<li class=\"projectile-effect\">Play the " + soundName + " sound</li>");
        } else if (effect instanceof PEPotionAura) {
            PEPotionAura aura = (PEPotionAura) effect;
            output.println(tabs + "<li class=\"projectile-effect\">");
            output.println(tabs + "\tGives the following potion effects to everyone within " + aura.getRadius() + " meters:");
            output.println(tabs + "\t<ul class=\"projectile-aura-effects\">");
            for (KciPotionEffect auraEffect : aura.getEffects()) {
                output.println(tabs + "\t\t<li class=\"projectile-aura-effect\">" + describePotionEffect(auraEffect) + "</li>");
            }
            output.println(tabs + "\t</ul>");
        } else if (effect instanceof PEPushOrPull) {
            PEPushOrPull pushOrPull = (PEPushOrPull) effect;
            if (pushOrPull.getStrength() > 0f) {
                output.println(tabs + "<li class=\"projectile-effect\">Pushes everyone within " + pushOrPull.getRadius() +
                        " meters away with strength " + pushOrPull.getStrength() + "</li>");
            } else {
                output.println(tabs + "<li class=\"projectile-effect\">Pulls everyone within " + pushOrPull.getRadius() +
                        " meters towards the projectile with strength " + (-pushOrPull.getStrength()) + "</li>");
            }
        } else if (effect instanceof PERandomAcceleration) {
            PERandomAcceleration acceleration = (PERandomAcceleration) effect;
            output.println(tabs + "<li class=\"projectile-effect\">Accelerates the projectile between "
                    + acceleration.getMinAcceleration() + " and " +
                    acceleration.getMaxAcceleration() + " meters / tick / tick towards a random direction</li>");
        } else if (effect instanceof PEShowFireworks) {
            output.println(tabs + "<li class=\"projectile-effect\">Creates " +
                    ((PEShowFireworks) effect).getEffects().size() + " firework effects</li>");
        } else if (effect instanceof PESimpleParticle) {
            PESimpleParticle particles = (PESimpleParticle) effect;
            output.println(tabs + "<li class=\"projectile-effect\">Spawns " + particles.getAmount() + " " +
                    NameHelper.getNiceEnumName(particles.getParticle().name()) + " particles</li>");
        } else if (effect instanceof PEStraightAcceleration) {
            PEStraightAcceleration acceleration = (PEStraightAcceleration) effect;
            output.println(tabs + "<li class=\"projectile-effect\">Accelerates the projectile between "
                    + acceleration.getMinAcceleration() + " and " +
                    acceleration.getMaxAcceleration() + " meters / tick / tick forward</li>");
        } else if (effect instanceof PESubProjectiles) {
            PESubProjectiles subProjectiles = (PESubProjectiles) effect;
            String childName = subProjectiles.getChild().getName();
            output.println(tabs + "<li class=\"projectile-effect\">Spawns " + subProjectiles.getMinAmount() + " to "
                    + subProjectiles.getMaxAmount() +
                    " <a href=\"." + childName + ".html\">" + childName + "</a> projectiles</li>");
        } else {
            output.println(tabs + "<li>An unknown effect. This is probably a bug.</li>");
        }
    }
}
