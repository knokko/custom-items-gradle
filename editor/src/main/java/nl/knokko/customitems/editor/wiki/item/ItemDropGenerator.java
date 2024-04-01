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
import nl.knokko.customitems.item.WikiVisibility;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.result.CustomItemResultValues;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.wiki.item.ItemRecipeGenerator.hasItem;
import static nl.knokko.customitems.editor.wiki.item.ItemRecipeGenerator.isItem;
import static nl.knokko.customitems.util.ColorCodes.stripColorCodes;

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
                        blockDrop -> hasItem(item, blockDrop.getDrop().getOutputTable())
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
                generateRequiredItemsInfo(output, "\t\t", blockDrop.getDrop().getRequiredHeldItems());
                generateRequiredFortuneLevel(output, "\t\t", blockDrop.getMinFortuneLevel(), blockDrop.getMaxFortuneLevel());
                generateRelevantDrops(output, "\t\t", blockDrop.getDrop().getOutputTable());
            }

            for (CustomBlockValues block : blocks) {
                output.println("\t\t<h4><a href=\"../blocks/" + block.getName() + ".html\">" + block.getName() + "</a></h4>");
                output.println("\t\t<ul class=\"block-drop-list\">");

                for (CustomBlockDropValues blockDrop : block.getDrops()) {
                    if (hasItem(item, blockDrop.getDrop().getOutputTable())) {
                        output.println("\t\t\t<li class=\"block-drop-entry\">");
                        generateCustomBlockDropInfo(output, blockDrop);
                        generateRelevantDrops(output, "\t\t\t\t", blockDrop.getDrop().getOutputTable());
                        output.println("\t\t\t</li>");
                    }
                }

                output.println("\t\t</ul>");
            }
        }

        if (!mobDrops.isEmpty()) {
            output.println("\t\t<h3>Dropped by mobs</h3>");
            output.println("\t\tThis item can be obtained by killing one of the following mobs:");

            for (MobDropValues mobDrop : mobDrops) {
                output.println("\t\t<h4>" + NameHelper.getNiceEnumName(mobDrop.getEntityType().name()) + "</h4>");
                if (mobDrop.getRequiredName() != null) {
                    output.println("\t\tRequires a specific custom name");
                }

                generateAllowedBiomes(output, "\t\t", mobDrop.getDrop().getAllowedBiomes());
                generateRequiredItemsInfo(output, "\t\t", mobDrop.getDrop().getRequiredHeldItems());
                generateRelevantDrops(output, "\t\t", mobDrop.getDrop().getOutputTable());
            }
        }
    }

    public static void generateRequiredItemsInfo(PrintWriter output, String tabs, RequiredItemValues requiredItems) {
        if (requiredItems.isEnabled()) {
            if (requiredItems.isInverted()) {
                output.println(tabs + "You can use any item, <b>except</b> the following items:");
            } else {
                output.println(tabs + "You must use one of the following items:");
            }
            output.println(tabs + "<ul class=\"required-drop-items\">");
            for (RequiredItemValues.VanillaEntry vanilla : requiredItems.getVanillaItems()) {
                output.print(tabs + "\t<li class=\"required-vanilla-item\">" + NameHelper.getNiceEnumName(vanilla.getMaterial().name()));
                if (vanilla.shouldAllowCustomItems()) {
                    output.print(" or a custom item of this type");
                }
                output.println("</li>");
            }

            for (ItemReference itemRef : requiredItems.getCustomItems()) {
                if (itemRef.get().getWikiVisibility() == WikiVisibility.VISIBLE) {
                    output.print(tabs + "\t<li class=\"required-custom-item\"><a href=\"./" + itemRef.get().getName() + ".html\">");
                    output.println(stripColorCodes(itemRef.get().getDisplayName()) + "</a></li>");
                }
            }
            output.println(tabs + "</ul>");
        }
    }

    public static void generateCustomBlockDropInfo(PrintWriter output, CustomBlockDropValues blockDrop) {
        output.println("\t\t\t\tSilk touch is " + blockDrop.getSilkTouchRequirement().name().toLowerCase(Locale.ROOT) + "<br>");
        generateRequiredItemsInfo(output, "\t\t\t\t", blockDrop.getDrop().getRequiredHeldItems());
        generateRequiredFortuneLevel(
                output, "\t\t\t\t", blockDrop.getMinFortuneLevel(),
                blockDrop.getMaxFortuneLevel()
        );
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

    private static void generateRequiredFortuneLevel(
            PrintWriter output, String tabs, int minLevel, Integer maxLevel
    ) {
        if (minLevel > 0) {
            output.println(tabs + "Fortune enchantment level must be at least " + minLevel + "<br>");
        }
        if (maxLevel != null) {
            output.println(tabs + "Fortune enchantment level must be at most " + maxLevel + "<br>");
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
