package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Predicate;

import static nl.knokko.customitems.editor.wiki.WikiHelper.*;
import static nl.knokko.customitems.util.ColorCodes.stripColorCodes;

public class WikiIndexGenerator {

    private final ItemSet itemSet;
    private final String name;

    public WikiIndexGenerator(ItemSet itemSet, String name) {
        this.itemSet = itemSet;
        this.name = name;
    }

    public void generate(File indexFile) throws IOException {
        generateHtml(indexFile, "index.css", name, output -> {
            output.println("\t\t<h1>" + name + "</h1>");
            output.println("\t\t<p>");
            output.println("\t\t\tThis is the wiki of the " + name + " custom item set.");
            output.println("\t\t\tIt lists all custom items, custom projectiles, custom containers, and custom blocks.");
            output.println("\t\t</p>");
            output.println("\t\t<h2 id=\"items-header\" >Items</h2>");

            generateItems(output, item -> item instanceof SimpleCustomItemValues, "Simple items", "h3");
            output.println("\t\t<h3 class=\"item-abstract-category-header\">Tools</h3>");
            generateSpecificTools(output, CustomItemType.Category.PICKAXE, "Pickaxes");
            generateSpecificTools(output, CustomItemType.Category.AXE, "Axes");
            generateSpecificTools(output, CustomItemType.Category.SHOVEL, "Shovels");
            generateSpecificTools(output, CustomItemType.Category.HOE, "Hoes");
            generateSpecificTools(output, CustomItemType.Category.SHEAR, "Shears");
            output.println("\t\t<h3 class=\"item-abstract-category-header\">Weapons</h3>");
            generateSpecificTools(output, CustomItemType.Category.SWORD, "Swords");
            generateItems(output, item -> item instanceof CustomBowValues, "Bows", "h4");
            generateItems(output, item -> item instanceof CustomCrossbowValues, "Crossbows", "h4");
            generateItems(output, item -> item instanceof CustomTridentValues, "Tridents", "h4");
            generateItems(output, item -> item instanceof CustomWandValues, "Wands", "h4");
            generateItems(output, item -> item instanceof CustomGunValues, "Guns", "h4");
            generateItems(output, item -> item instanceof CustomThrowableValues, "Throwables", "h4");
            output.println("\t\t<h3 class=\"item-abstract-category-header\">Armor</h3>");
            generateItems(output, item ->
                            (item instanceof CustomArmorValues && item.getItemType().canServe(CustomItemType.Category.HELMET))
                                    || item instanceof CustomHelmet3dValues,
                    "Helmets", "h4");
            generateSpecificArmor(output, CustomItemType.Category.CHESTPLATE, "Chestplates");
            generateSpecificArmor(output, CustomItemType.Category.LEGGINGS, "Leggings");
            generateSpecificArmor(output, CustomItemType.Category.BOOTS, "Boots");
            generateSpecificArmor(output, CustomItemType.Category.ELYTRA, "Elytra");
            generateItems(output, item -> item instanceof CustomShieldValues, "Shields", "h4");

            generateItems(output, item -> item instanceof CustomFoodValues, "Food & potions", "h3");
            generateItems(output, item -> item instanceof CustomPocketContainerValues, "Pocket containers", "h3");
            generateItems(output, item -> item instanceof CustomBlockItemValues, "Block items", "h3");
            generateItems(output, item -> item instanceof CustomMusicDiscValues, "Music discs", "h3");

            if (itemSet.getContainers().size() > 0) {
                output.println("\t\t<h2 id=\"containers-header\">Containers</h2>");
                output.println("\t\t<ul class=\"custom-containers\">");
                for (CustomContainerValues container : itemSet.getContainers()) {
                    output.println("\t\t\t<li class=\"custom-container\"><a href=\"containers/"
                            + container.getName() + ".html\">" + getDisplayName(container) + "</a></li>");
                }
                output.println("\t\t</ul>");
            }

            if (itemSet.getBlocks().size() > 0) {
                output.println("\t\t<h2 id=\"blocks-header\">Blocks</h2>");
                output.println("\t\t<ul class=\"custom-blocks\">");
                for (CustomBlockValues block : itemSet.getBlocks()) {
                    String link = "blocks/" + block.getName() + ".html";
                    output.print("\t\t\t<li class=\"custom-block\"><a href=\"" + link + "\"><img src=\"textures/"
                            + block.getModel().getPrimaryTexture().get().getName());
                    output.println(".png\" class=\"block-icon\" />" + block.getName() + "</a></li>");
                }

                output.println("\t\t</ul>");
            }
        });
    }

    private void generateSpecificTools(PrintWriter output, CustomItemType.Category category, String categoryName) {
        generateItems(output, item -> item instanceof CustomToolValues && item.getItemType().canServe(category), categoryName, "h4");
    }

    private void generateSpecificArmor(PrintWriter output, CustomItemType.Category category, String categoryName) {
        generateItems(output, item -> item instanceof CustomArmorValues && item.getItemType().canServe(category), categoryName, "h4");
    }

    private void generateItems(PrintWriter output, Predicate<CustomItemValues> belongsToCategory, String categoryName, String headerType) {
        if (itemSet.getItems().stream().anyMatch(item -> item.getWikiVisibility() == WikiVisibility.VISIBLE && belongsToCategory.test(item))) {
            output.println("\t\t<" + headerType + " class=\"item-category-header\" >" + categoryName + "</" + headerType + ">");
            output.println("\t\t<ul class=\"item-list\" >");
            for (CustomItemValues item : itemSet.getItems()) {
                if (item.getWikiVisibility() == WikiVisibility.VISIBLE && belongsToCategory.test(item)) {
                    String link = "items/" + item.getName() + ".html";
                    output.print("\t\t\t<li><a href=\"" + link + "\"><img src=\"textures/" + item.getTexture().getName());
                    output.println(".png\" class=\"item-icon\" />" + stripColorCodes(item.getDisplayName()) + "</a></li>");
                }
            }
            output.println("\t\t</ul>");
        }
    }
}
