package nl.knokko.customitems.editor.wiki.item;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.drop.CustomBlockDropValues;
import nl.knokko.customitems.block.drop.RequiredItemValues;
import nl.knokko.customitems.drops.AllowedBiomesValues;
import nl.knokko.customitems.drops.BlockDropValues;
import nl.knokko.customitems.drops.CIBiome;
import nl.knokko.customitems.drops.MobDropValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.wiki.WikiHelper.stripColorCodes;
import static nl.knokko.customitems.editor.wiki.item.ItemRecipeGenerator.hasItem;
import static nl.knokko.customitems.editor.wiki.item.ItemRecipeGenerator.isItem;

public class ItemDropGenerator {

    private final CustomItemValues item;

    private final Collection<BlockDropValues> blockDrops;
    private final Collection<MobDropValues> mobDrops;
    private final Collection<CustomBlockValues> blocks;

    ItemDropGenerator(ItemSet itemSet, CustomItemValues item) {
        this.item = item;

        this.blockDrops = itemSet.getBlockDrops().stream().filter(
                blockDrop -> hasItem(item, blockDrop.getDrop().getOutputTable())
        ).collect(Collectors.toList());

        this.mobDrops = itemSet.getMobDrops().stream().filter(
                mobDrop -> hasItem(item, mobDrop.getDrop().getOutputTable())
        ).collect(Collectors.toList());

        this.blocks = itemSet.getBlocks().stream().filter(
                block -> block.getDrops().stream().anyMatch(
                        blockDrop -> hasItem(item, blockDrop.getItemsToDrop())
                )
        ).collect(Collectors.toList());
    }

    boolean shouldGenerate() {
        return !blockDrops.isEmpty() || !mobDrops.isEmpty() || !blocks.isEmpty();
    }

    void generate(PrintWriter output) {
        if (!blockDrops.isEmpty() || !blocks.isEmpty()) {
            output.println("\t\t<h3>Dropped by blocks</h3>");
            output.println("\t\tThis item can be obtained by breaking one of the following blocks:");

            for (BlockDropValues blockDrop : blockDrops) {
                output.println("\t\t<h4>" + NameHelper.getNiceEnumName(blockDrop.getBlockType().name()) + "</h4>");

                generateAllowedBiomes(output, "\t\t", blockDrop.getDrop().getAllowedBiomes());
                generateRequiredHeldItems(output, "\t\t", blockDrop.getDrop().getRequiredHeldItems());
                generateRelevantDrops(output, "\t\t", blockDrop.getDrop().getOutputTable());
            }

            for (CustomBlockValues block : blocks) {
                output.println("\t\t<h4><a href=\"../blocks/" + block.getName() + ".html\">" + block.getName() + "</a></h4>");
                output.println("\t\t<ul class=\"block-drop-list\">");

                for (CustomBlockDropValues blockDrop : block.getDrops()) {
                    if (hasItem(item, blockDrop.getItemsToDrop())) {
                        output.println("\t\t\t<li class=\"block-drop-entry\">");
                        generateCustomBlockDropInfo(output, blockDrop);
                        generateRelevantDrops(output, "\t\t\t\t", blockDrop.getItemsToDrop());
                        output.println("\t\t\t</li>");
                    }
                }

                output.println("\t\t</ul>");
            }
        }
    }

    public static void generateCustomBlockDropInfo(PrintWriter output, CustomBlockDropValues blockDrop) {
        output.println("\t\t\t\tSilk touch is " + blockDrop.getSilkTouchRequirement().name().toLowerCase(Locale.ROOT) + "<br>");
        if (blockDrop.getRequiredItems().isEnabled()) {
            if (blockDrop.getRequiredItems().isInverted()) {
                output.println("\t\t\t\tYou can use any item, <b>except</b> the following items:");
            } else {
                output.println("\t\t\t\tYou must use one of the following items:");
            }
            output.println("\t\t\t\t<ul class=\"required-drop-items\">");
            for (RequiredItemValues.VanillaEntry vanilla : blockDrop.getRequiredItems().getVanillaItems()) {
                output.print("\t\t\t\t\t<li class=\"required-vanilla-item\">" + NameHelper.getNiceEnumName(vanilla.getMaterial().name()));
                if (vanilla.shouldAllowCustomItems()) {
                    output.print(" or a custom item of this type");
                }
                output.println("</li>");
            }
            for (ItemReference itemRef : blockDrop.getRequiredItems().getCustomItems()) {
                output.print("\t\t\t\t\t<li class=\"required-custom-item\"><a href=\"./" + itemRef.get().getName() + ".html\">");
                output.println(stripColorCodes(itemRef.get().getDisplayName()) + "</a></li>");
            }
            output.println("\t\t\t\t</ul>");
        }
    }

    private void generateAllowedBiomes(PrintWriter output, String tabs, AllowedBiomesValues allowedBiomes) {
        if (allowedBiomes.getWhitelist().isEmpty()) {
            if (!allowedBiomes.getBlacklist().isEmpty()) {
                output.println(tabs + "When the block is broken in any biome, <b><i>except</i></b> 1 of the following:");
                generateBiomeList(output, tabs, allowedBiomes.getBlacklist());
            }
        } else {
            output.println(tabs + "When the block is broken in one of the following biomes:");
            Collection<CIBiome> biomes = new ArrayList<>(allowedBiomes.getWhitelist());
            biomes.removeAll(allowedBiomes.getBlacklist());
            generateBiomeList(output, tabs, biomes);
        }
    }

    private void generateBiomeList(PrintWriter output, String tabs, Collection<CIBiome> biomes) {
        output.println(tabs + "<ul class=\"drop-biomes\">");
        for (CIBiome biome : biomes) {
            output.println(tabs + "\t<li class=\"drop-biome\">" + biome + "</li>");
        }
        output.println(tabs + "</ul>");
    }

    private void generateRequiredHeldItems(PrintWriter output, String tabs, Collection<ItemReference> requiredItems) {
        if (!requiredItems.isEmpty()) {
            output.println(tabs + "When the player uses one of the following custom items:");
            output.println(tabs + "<ul class=\"required-held-items\">");
            for (ItemReference item : requiredItems) {
                output.println(tabs + "\t<li class=\"required-custom-item\"><a href=\"./" + item.get().getName() + ".html\">"
                        + stripColorCodes(item.get().getDisplayName())
                        + "</a></li>");
            }
            output.println(tabs + "</ul>");
        }
    }

    private void generateRelevantDrops(PrintWriter output, String tabs, OutputTableValues allDrops) {
        output.println(tabs + "Chances:");
        output.println(tabs + "<ul class=\"drop-chances\">");
        for (OutputTableValues.Entry candidateEntry : allDrops.getEntries()) {
            if (isItem(item, candidateEntry.getResult())) {
                int amount = ((CustomItemResultValues) candidateEntry.getResult()).getAmount();
                output.println(tabs + "\t<li class=\"drop-chance\">" + candidateEntry.getChance() + " to get " + amount + "</li>");
            }
        }
        output.println(tabs + "</ul>");
    }
}
