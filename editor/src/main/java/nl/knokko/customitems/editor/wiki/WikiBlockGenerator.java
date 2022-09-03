package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.drop.CustomBlockDropValues;
import nl.knokko.customitems.editor.wiki.item.ItemDropGenerator;
import nl.knokko.customitems.item.CustomBlockItemValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.File;
import java.io.IOException;
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
        });
    }
}
