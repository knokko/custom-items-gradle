package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.editor.wiki.item.WikiItemGenerator;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;

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
        copyResource("projectiles.css", new File(destinationFolder + "/projectiles.css"));
        copyResource("containers.css", new File(destinationFolder + "/containers.css"));
        copyResource("blocks.css", new File(destinationFolder + "/blocks.css"));

        File itemsFolder = new File(destinationFolder + "/items");
        if (!itemsFolder.exists() && !itemsFolder.mkdir()) throw new IOException("Failed to create items folder");
        for (CustomItemValues item : itemSet.getItems()) {
            new WikiItemGenerator(itemSet, item).generate(new File(itemsFolder + "/" + item.getName() + ".html"));
        }

        File projectilesFolder = new File(destinationFolder + "/projectiles");
        if (!projectilesFolder.exists() && !projectilesFolder.mkdir()) throw new IOException("Failed to create projectiles folder");
        for (CustomProjectileValues projectile : itemSet.getProjectiles()) {
            new WikiProjectileGenerator(itemSet, projectile).generate(new File(projectilesFolder + "/" + projectile.getName() + ".html"));
        }

        File containersFolder = new File(destinationFolder + "/containers");
        if (!containersFolder.exists() && !containersFolder.mkdir()) throw new IOException("Failed to create containers folder");
        for (CustomContainerValues container : itemSet.getContainers()) {
            new WikiContainerGenerator(itemSet, container).generate(new File(containersFolder + "/" + container.getName() + ".html"));
        }

        File blocksFolder = new File(destinationFolder + "/blocks");
        if (!blocksFolder.exists() && !blocksFolder.mkdir()) throw new IOException("Failed to create blocks folder");
        for (CustomBlockValues block : itemSet.getBlocks()) {
            new WikiBlockGenerator(itemSet, block).generate(new File(blocksFolder + "/" + block.getName() + ".html"));
        }
    }
}