package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.container.KciContainer;
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

            generateItems(output, item -> item instanceof KciSimpleItem, "Simple items", "h3");
            output.println("\t\t<h3 class=\"item-abstract-category-header\">Tools</h3>");
            generateSpecificTools(output, KciItemType.Category.PICKAXE, "Pickaxes");
            generateSpecificTools(output, KciItemType.Category.AXE, "Axes");
            generateSpecificTools(output, KciItemType.Category.SHOVEL, "Shovels");
            generateSpecificTools(output, KciItemType.Category.HOE, "Hoes");
            generateSpecificTools(output, KciItemType.Category.SHEAR, "Shears");
            output.println("\t\t<h3 class=\"item-abstract-category-header\">Weapons</h3>");
            generateSpecificTools(output, KciItemType.Category.SWORD, "Swords");
            generateItems(output, item -> item instanceof KciBow, "Bows", "h4");
            generateItems(output, item -> item instanceof KciCrossbow, "Crossbows", "h4");
            generateItems(output, item -> item instanceof KciTrident, "Tridents", "h4");
            generateItems(output, item -> item instanceof KciWand, "Wands", "h4");
            generateItems(output, item -> item instanceof KciGun, "Guns", "h4");
            generateItems(output, item -> item instanceof KciThrowable, "Throwables", "h4");
            output.println("\t\t<h3 class=\"item-abstract-category-header\">Armor</h3>");
            generateItems(output, item ->
                            (item instanceof KciArmor && item.getItemType().canServe(KciItemType.Category.HELMET))
                                    || item instanceof Kci3dHelmet,
                    "Helmets", "h4");
            generateSpecificArmor(output, KciItemType.Category.CHESTPLATE, "Chestplates");
            generateSpecificArmor(output, KciItemType.Category.LEGGINGS, "Leggings");
            generateSpecificArmor(output, KciItemType.Category.BOOTS, "Boots");
            generateSpecificArmor(output, KciItemType.Category.ELYTRA, "Elytra");
            generateItems(output, item -> item instanceof KciShield, "Shields", "h4");

            generateItems(output, item -> item instanceof KciFood, "Food & potions", "h3");
            generateItems(output, item -> item instanceof KciPocketContainer, "Pocket containers", "h3");
            generateItems(output, item -> item instanceof KciBlockItem, "Block items", "h3");
            generateItems(output, item -> item instanceof KciMusicDisc, "Music discs", "h3");

            if (!itemSet.containers.isEmpty()) {
                output.println("\t\t<h2 id=\"containers-header\">Containers</h2>");
                output.println("\t\t<ul class=\"custom-containers\">");
                for (KciContainer container : itemSet.containers) {
                    output.println("\t\t\t<li class=\"custom-container\"><a href=\"containers/"
                            + container.getName() + ".html\">" + getDisplayName(container) + "</a></li>");
                }
                output.println("\t\t</ul>");
            }

            if (!itemSet.blocks.isEmpty()) {
                output.println("\t\t<h2 id=\"blocks-header\">Blocks</h2>");
                output.println("\t\t<ul class=\"custom-blocks\">");
                for (KciBlock block : itemSet.blocks) {
                    String link = "blocks/" + block.getName() + ".html";
                    output.print("\t\t\t<li class=\"custom-block\"><a href=\"" + link + "\"><img src=\"textures/"
                            + block.getModel().getPrimaryTexture().get().getName());
                    output.println(".png\" class=\"block-icon\" />" + block.getName() + "</a></li>");
                }

                output.println("\t\t</ul>");
            }
        });
    }

    private void generateSpecificTools(PrintWriter output, KciItemType.Category category, String categoryName) {
        generateItems(output, item -> item instanceof KciTool && item.getItemType().canServe(category), categoryName, "h4");
    }

    private void generateSpecificArmor(PrintWriter output, KciItemType.Category category, String categoryName) {
        generateItems(output, item -> item instanceof KciArmor && item.getItemType().canServe(category), categoryName, "h4");
    }

    private void generateItems(PrintWriter output, Predicate<KciItem> belongsToCategory, String categoryName, String headerType) {
        if (itemSet.items.stream().anyMatch(item -> item.getWikiVisibility() == WikiVisibility.VISIBLE && belongsToCategory.test(item))) {
            output.println("\t\t<" + headerType + " class=\"item-category-header\" >" + categoryName + "</" + headerType + ">");
            output.println("\t\t<ul class=\"item-list\" >");
            for (KciItem item : itemSet.items) {
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
