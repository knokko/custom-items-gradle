package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.DamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.KciProjectile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.wiki.WikiHelper.generateHtml;
import static nl.knokko.customitems.util.ColorCodes.stripColorCodes;

public class WikiDamageSourceGenerator {

    public static String createLink(DamageSourceReference damageSource, String pathToRoot) {
        return "<a href=\"" + pathToRoot + "damage-sources/" + damageSource.get().getId() + ".html\">"
                + damageSource.get().getName() + "</a>";
    }

    private final DamageSourceReference damageSource;
    private final ItemSet itemSet;

    WikiDamageSourceGenerator(DamageSourceReference damageSource, ItemSet itemSet) {
        this.damageSource = damageSource;
        this.itemSet = itemSet;
    }

    public void generate(File file) throws IOException {
        generateHtml(file, "../damage-source.css", "Damage source " + damageSource.get().getName(), output -> {
            output.println("\t\t<h1>Damage source " + damageSource.get().getName() + "</h1>");
            output.println("\t\t<h2>Inflicted by</h2>");
            output.println("\t\t<ul>");

            for (KciItem item : itemSet.items) {
                List<String> ways = new ArrayList<>();
                if (damageSource.equals(item.getCustomMeleeDamageSourceReference())) ways.add("melee attack with");

                if (item instanceof KciArrow && damageSource.equals(((KciArrow) item).getCustomShootDamageSourceReference())) {
                    ways.add("struck by");
                }

                if (item instanceof KciBow && damageSource.equals(((KciBow) item).getCustomShootDamageSourceReference())) {
                    ways.add("shot with");
                }

                if (item instanceof KciCrossbow && damageSource.equals(((KciCrossbow) item).getCustomShootDamageSourceReference())) {
                    ways.add("shot with");
                }

                if (item instanceof KciTrident && damageSource.equals(((KciTrident) item).getCustomThrowDamageSourceReference())) {
                    ways.add("struck by");
                }

                for (String way : ways) {
                    output.println("\t\t\t<li class=\"item-source\">" + way + " <a href=\"../items/" + item.getName()
                            + ".html\"><img width=\"32px\" height=\"32px\" src=\"../textures/" +
                            item.getTexture().getName() + ".png\" class=\"item-icon\" />" +
                            stripColorCodes(item.getDisplayName()) + "</a></li>");
                }
            }

            for (KciProjectile projectile : itemSet.projectiles) {
                if (damageSource.equals(projectile.getCustomDamageSourceReference())) {
                    output.println("\t\t\t<li class=\"projectile-source\">struck by <a href=\"../projectiles/" +
                            projectile.getName() + ".html\">" + projectile.getName() + "</a></li>");
                }
            }

            output.println("\t\t</ul>");

            Collection<KciItem> resistingItems = itemSet.items.stream().filter(item ->
                    item instanceof KciArmor
                            && ((KciArmor) item).getDamageResistances().getResistance(damageSource) > 0
                            && item.getWikiVisibility() == WikiVisibility.VISIBLE
            ).collect(Collectors.toList());
            Collection<KciItem> vulnerableItems = itemSet.items.stream().filter(item ->
                    item instanceof KciArmor
                            && ((KciArmor) item).getDamageResistances().getResistance(damageSource) < 0
                            && item.getWikiVisibility() == WikiVisibility.VISIBLE
            ).collect(Collectors.toList());

            long numResistingUpgrades = itemSet.upgrades.stream().filter(upgrade ->
                    upgrade.getDamageResistances().getResistance(damageSource) > 0
            ).count();
            long numVulnerableUpgrades = itemSet.upgrades.stream().filter(upgrade ->
                    upgrade.getDamageResistances().getResistance(damageSource) < 0
            ).count();
            long numResistingEquipmentSets = itemSet.equipmentSets.stream().mapToLong(set ->
                    set.getBonuses().stream().filter(
                            bonuses -> bonuses.getDamageResistances().getResistance(damageSource) > 0
                    ).count()
            ).filter(value -> value > 0).count();
            long numVulnerableEquipmentSets = itemSet.equipmentSets.stream().mapToLong(set ->
                    set.getBonuses().stream().filter(
                            bonuses -> bonuses.getDamageResistances().getResistance(damageSource) < 0
                    ).count()
            ).filter(value -> value > 0).count();

            if (!resistingItems.isEmpty() || numResistingUpgrades > 0 || numResistingEquipmentSets > 0) {
                output.println("\t\t<h2>Resisted by</h2>");
                generateList(output, resistingItems, numResistingUpgrades, numResistingEquipmentSets);
            }

            if (!vulnerableItems.isEmpty() || numVulnerableUpgrades > 0 || numVulnerableEquipmentSets > 0) {
                output.println("\t\t<h2>Strong against</h2>");
                generateList(output, vulnerableItems, numVulnerableUpgrades, numVulnerableEquipmentSets);
            }
        });
    }

    private void generateList(
            PrintWriter output, Collection<KciItem> items, long numUpgrades, long numEquipmentSets
    ) {
        output.println("\t\t<ul>");
        for (KciItem item : items) {
            String link = "../items/" + item.getName() + ".html";
            output.print("\t\t\t<li><a href=\"" + link + "\"><img src=\"../textures/" + item.getTexture().getName());
            output.println(".png\" class=\"item-icon\" />" + stripColorCodes(item.getDisplayName()) + "</a></li>");
        }
        if (numUpgrades > 0) {
            output.println("\t\t\t<li>" + numUpgrades + " upgrades</li>");
        }
        if (numEquipmentSets > 0) {
            output.println("\t\t\t<li>" + numEquipmentSets + " equipment sets</li>");
        }
        output.println("\t\t</ul>");
    }
}
