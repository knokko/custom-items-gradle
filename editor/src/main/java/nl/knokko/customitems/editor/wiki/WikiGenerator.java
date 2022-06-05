package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.editor.wiki.item.WikiItemGenerator;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.File;
import java.io.IOException;

import static nl.knokko.customitems.editor.wiki.WikiHelper.copyResource;

public class WikiGenerator {

    private final ItemSet itemSet;
    private final String name;

    public WikiGenerator(ItemSet itemSet, String name) {
        this.itemSet = itemSet;
        this.name = name;
    }

    public void generate(File destinationFolder) throws IOException {
        if (!destinationFolder.isDirectory() && !destinationFolder.mkdirs()) throw new IOException("Can't create destination folder " + destinationFolder);

        new WikiIndexGenerator(itemSet, name).generate(new File(destinationFolder + "/index.html"));
        new WikiTextureGenerator(itemSet).generate(new File(destinationFolder + "/textures"));
        copyResource("index.css", new File(destinationFolder + "/index.css"));
        copyResource("items.css", new File(destinationFolder + "/items.css"));
        copyResource("recipe.css", new File(destinationFolder + "/recipe.css"));
        copyResource("recipe-arrow.png", new File(destinationFolder + "/recipe-arrow.png"));

        File itemsFolder = new File(destinationFolder + "/items");
        if (!itemsFolder.exists() && !itemsFolder.mkdir()) throw new IOException("Failed to create items folder");
        for (CustomItemValues item : itemSet.getItems()) {
            new WikiItemGenerator(itemSet, item).generate(new File(itemsFolder + "/" + item.getName() + ".html"));
        }

        // TODO Projectiles

        // TODO Containers

        // TODO Blocks
    }
}
