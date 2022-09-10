package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.NameHelper;
import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.drop.CustomBlockDropValues;
import nl.knokko.customitems.block.miningspeed.CustomMiningSpeedEntry;
import nl.knokko.customitems.block.miningspeed.VanillaMiningSpeedEntry;
import nl.knokko.customitems.drops.AllowedBiomesValues;
import nl.knokko.customitems.drops.CIBiome;
import nl.knokko.customitems.editor.wiki.item.ItemDropGenerator;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomBlockItemValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.BlockReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.worldgen.BlockProducerValues;
import nl.knokko.customitems.worldgen.CITreeType;
import nl.knokko.customitems.worldgen.OreVeinGeneratorValues;
import nl.knokko.customitems.worldgen.TreeGeneratorValues;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.stream.Collectors;

import static nl.knokko.customitems.editor.wiki.WikiHelper.generateHtml;
import static nl.knokko.customitems.editor.wiki.WikiHelper.stripColorCodes;
import static nl.knokko.customitems.editor.wiki.WikiRecipeGenerator.generateOutputTable;

class WikiBlockGenerator {

    private final ItemSet itemSet;
    private final CustomBlockValues block;

    WikiBlockGenerator(ItemSet itemSet, CustomBlockValues block) {
        this.itemSet = itemSet;
        this.block = block;
    }

    void generate(File destination) throws IOException {
        generateHtml(destination, "../blocks.css", block.getName(), output -> {
            output.println("\t\t<h1>" + block.getName() + "</h1>");
            output.println("\t\t<img src=\"../textures/" + block.getModel().getPrimaryTexture().get().getName() + ".png\" class=\"block-icon\" /><br>");

            Collection<CustomItemValues> placingItems = itemSet.getItems().stream().filter(
                    item -> item instanceof CustomBlockItemValues && ((CustomBlockItemValues) item).getBlock().getName().equals(block.getName())
            ).collect(Collectors.toList());
            if (!placingItems.isEmpty()) {
                output.println("\t\t<h2>Placing this block</h2>");
                output.println("\t\tYou can place this block by using 1 of these items:");
                output.println("\t\t<ul class=\"block-items\">");
                for (CustomItemValues placingItem : placingItems) {
                    output.println("\t\t\t<li class=\"block-item\"><a href=\"../items/" + placingItem.getName()
                            + ".html\"><img src=\"../textures/" +
                            placingItem.getTexture().getName() + ".png\" class=\"item-icon\" />" +
                            stripColorCodes(placingItem.getDisplayName()) + "</a></li>");
                }
                output.println("\t\t</ul>");
            }

            output.println("\t\t<h2>Mining speed</h2>");
            output.println("\t\tDefault mining speed: " + displayMiningSpeed(block.getMiningSpeed().getDefaultValue()));
            if (!block.getMiningSpeed().getVanillaEntries().isEmpty() || !block.getMiningSpeed().getCustomEntries().isEmpty()) {
                output.println("\t\t<ul>");

                for (VanillaMiningSpeedEntry vanillaEntry : block.getMiningSpeed().getVanillaEntries()) {
                    String prefix = "\t\t\t<li>Mining speed when using a ";
                    if (!vanillaEntry.shouldAcceptCustomItems()) {
                        prefix += "vanilla ";
                    }
                    output.println(prefix + NameHelper.getNiceEnumName(vanillaEntry.getMaterial().name())
                            + ": " + displayMiningSpeed(vanillaEntry.getValue()) + "</li>");
                }

                for (CustomMiningSpeedEntry customEntry : block.getMiningSpeed().getCustomEntries()) {
                    output.println("\t\t\t<li>Mining speed when using a <a href=\"../items/"
                            + customEntry.getItem().getName() + ".html\">"
                            + stripColorCodes(customEntry.getItem().getDisplayName()) + "</a>: "
                            + displayMiningSpeed(customEntry.getValue()) + "</li>");
                }

                output.println("\t\t</ul>");
            }

            if (!block.getDrops().isEmpty()) {
                output.println("\t\t<h2>Drops</h2>");
                output.println("\t\t<ul class=\"custom-block-drops\">");
                for (CustomBlockDropValues drop : block.getDrops()) {
                    output.println("\t\t\t<li class=\"custom-block-drop\">");
                    ItemDropGenerator.generateCustomBlockDropInfo(output, drop);
                    output.println("\t\t\t\tThe following items will be dropped:");
                    output.println("\t\t\t\t<ul class=\"custom-block-drop-items\">");
                    generateOutputTable(output, "\t\t\t\t\t<li class=\"custom-block-drop-item\">", "</li>",
                            drop.getItemsToDrop());
                    output.println("\t\t\t\t</ul>");
                    output.println("\t\t\t</li>");
                }
                output.println("\t\t</ul>");
            }

            boolean isGeneratedInTrees = itemSet.getTreeGenerators().stream().anyMatch(
                    generator -> canProduceBlock(generator.getLogMaterial()) || canProduceBlock(generator.getLeavesMaterial())
            );
            boolean isGeneratedAsOre = itemSet.getOreVeinGenerators().stream().anyMatch(
                    generator -> canProduceBlock(generator.getOreMaterial())
            );
            if (isGeneratedInTrees || isGeneratedAsOre) {
                output.println("\t\t<h2>World generation</h2>");

                if (isGeneratedInTrees) {
                    output.println("\t\t<h3>Trees</h3>");
                    output.println("\t\t<ul>");
                    for (TreeGeneratorValues tree : itemSet.getTreeGenerators()) {
                        if (canProduceBlock(tree.getLogMaterial())) {
                            generateTreeGenerationInfo(output, tree, tree.getLogMaterial(), "log");
                        }
                        if (canProduceBlock(tree.getLeavesMaterial())) {
                            generateTreeGenerationInfo(output, tree, tree.getLeavesMaterial(), "leaves");
                        }
                    }
                    output.println("\t\t</ul>");
                }

                if (isGeneratedAsOre) {
                    output.println("\t\t<h3>Ore</h3>");
                    output.println("\t\t<ul>");

                    for (OreVeinGeneratorValues generator : itemSet.getOreVeinGenerators()) {
                        if (canProduceBlock(generator.getOreMaterial())) {
                            output.println("\t\t\t<li>");
                            output.println("\t\t\t\tThis block has " + getGenerationChance(generator.getOreMaterial())
                                    + " chance to be generated in ore veins that can replace the following blocks:");
                            output.println("\t\t\t\t<ul>");
                            for (CIMaterial vanillaBlock : generator.getBlocksToReplace().getVanillaBlocks()) {
                                output.println("\t\t\t\t\t<li>" + NameHelper.getNiceEnumName(vanillaBlock.name()) + "</li>");
                            }
                            for (BlockReference customBlock : generator.getBlocksToReplace().getCustomBlocks()) {
                                output.println("\t\t\t\t\t<li><a href=\"" + customBlock.get().getName() + ".html\">"
                                        + customBlock.get().getName() + "</a></li>");
                            }
                            output.println("\t\t\t\t</ul>");
                            output.println("\t\t\t\tThese ore veins can only be generated between Y=" + generator.getMinY()
                                    + " and Y=" + generator.getMaxY() + ".<br>");
                            output.println("\t\t\t\tThere is " + generator.getChance() + " chance that " + generator.getMinNumVeins()
                                    + " to " + generator.getMaxNumVeins() + " veins will be spawned per chunk.<br>");
                            output.println("\t\t\t\tThe expected size of these veins is " + generator.getMinVeinSize()
                                    + " to " + generator.getMaxVeinSize() + " blocks.<br>");
                            generateAllowedBiomes(output, "\t\t\t\t", generator.getAllowedBiomes());
                            output.println("\t\t\t</li>");
                        }
                    }

                    output.println("\t\t</ul>");
                }
            }
        });
    }

    private String displayMiningSpeed(int value) {
        if (value <= -4) return value + " (extremely slow)";
        if (value == -3) return "-3 (very slow)";
        if (value == -2) return "-2 (slow)";
        if (value == -1) return "-1 (normal)";
        if (value == 0) return "0 (fast)";
        if (value <= 20) return value + " (very fast)";
        return value + " (breaks (almost) instantly)";
    }

    private void generateTreeGenerationInfo(
            PrintWriter output, TreeGeneratorValues tree,
            BlockProducerValues producer, String description
    ) {
        output.println("\t\t\t<li>");
        output.println("\t\t\t\tThis block has " + getGenerationChance(producer)
                + " chance to be generated as " + description + " of custom " + getNiceTreeName(tree.getTreeType()) + ".");
        generateAllowedBiomes(output, "\t\t\t\t", tree.getAllowedBiomes());
        output.println("\t\t\t</li>");
    }

    private void generateAllowedBiomes(PrintWriter output, String tabs, AllowedBiomesValues biomes) {
        if (biomes.getWhitelist().isEmpty()) {
            if (biomes.getBlacklist().isEmpty()) {
                output.println(tabs + "These can be generated in all biomes.");
            } else {
                output.println(tabs + "These can be generated in all biomes, except");
                generateBiomeList(output, tabs, biomes.getBlacklist());
            }
        } else {
            output.println(tabs + "These can be generated in the following biomes:");
            generateBiomeList(output, tabs, biomes.getWhitelist());
            if (!biomes.getBlacklist().isEmpty()) {
                output.println(tabs + "except");
                generateBiomeList(output, tabs, biomes.getBlacklist());
            }
        }
    }

    private void generateBiomeList(PrintWriter output, String tabs, Collection<CIBiome> biomes) {
        output.println(tabs + "<ul>");
        for (CIBiome forbiddenBiome : biomes) {
            output.println(tabs + "\t<li>" + NameHelper.getNiceEnumName(forbiddenBiome.name()) + "</li>");
        }
        output.println(tabs + "</ul>");
    }

    private String getNiceTreeName(CITreeType treeType) {
        if (treeType.name().contains("TREE")) {
            return NameHelper.getNiceEnumName(treeType.name()) + "s";
        } else {
            return NameHelper.getNiceEnumName(treeType.name()) + " trees";
        }
    }

    private Chance getGenerationChance(BlockProducerValues producer) {
        for (BlockProducerValues.Entry entry : producer.getEntries()) {
            if (entry.getBlock().isCustom() && entry.getBlock().getCustomBlock().get().getInternalID() == block.getInternalID()) {
                return entry.getChance();
            }
        }
        return null;
    }

    private boolean canProduceBlock(BlockProducerValues producer) {
        return getGenerationChance(producer) != null;
    }
}
