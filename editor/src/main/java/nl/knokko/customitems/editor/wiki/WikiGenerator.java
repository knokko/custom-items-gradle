package nl.knokko.customitems.editor.wiki;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.energy.EnergyTypeValues;
import nl.knokko.customitems.editor.wiki.item.WikiItemGenerator;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.WikiVisibility;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;
import nl.knokko.customitems.itemset.CustomDamageSourceReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        copyResource("damage-source.css", new File(destinationFolder + "/damage-source.css"));

        List<EquipmentSetValues> equipmentSets = itemSet.getEquipmentSets().stream().collect(Collectors.toList());
        File itemsFolder = new File(destinationFolder + "/items");
        if (!itemsFolder.exists() && !itemsFolder.mkdir()) throw new IOException("Failed to create items folder");
        for (CustomItemValues item : itemSet.getItems()) {
            if (item.getWikiVisibility() == WikiVisibility.VISIBLE) {
                new WikiItemGenerator(itemSet, item, equipmentSets).generate(
                        new File(itemsFolder + "/" + item.getName() + ".html")
                );
            }
        }

        File equipmentSetsFolder = new File(itemsFolder + "/equipment");
        if (!equipmentSetsFolder.exists() && !equipmentSetsFolder.mkdirs()) throw new IOException("Failed to create equipment sets folder");
        for (int index = 0; index < equipmentSets.size(); index++) {
            new WikiEquipmentSetGenerator(equipmentSets.get(index)).generate(
                    new File(equipmentSetsFolder + "/set" + index + ".html"), itemSet
            );
        }

        File damageSourcesFolder = new File(destinationFolder + "/damage-sources");
        if (!damageSourcesFolder.exists() && !damageSourcesFolder.mkdirs()) throw new IOException("Failed to create damage sources folder");
        for (CustomDamageSourceReference damageSource : itemSet.getDamageSources().references()) {
            new WikiDamageSourceGenerator(damageSource, itemSet).generate(new File(damageSourcesFolder + "/" + damageSource.get().getId() + ".html"));
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

        File energyFolder = new File(containersFolder + "/energy");
        if (!energyFolder.exists() && !energyFolder.mkdir()) throw new IOException("Failed to create container energy folder");
        for (EnergyTypeValues energyType : itemSet.getEnergyTypes()) {
            new WikiEnergyTypeGenerator(itemSet, energyType).generate(new File(energyFolder + "/" + energyType.getName() + ".html"));
        }

        File blocksFolder = new File(destinationFolder + "/blocks");
        if (!blocksFolder.exists() && !blocksFolder.mkdir()) throw new IOException("Failed to create blocks folder");
        for (CustomBlockValues block : itemSet.getBlocks()) {
            new WikiBlockGenerator(itemSet, block).generate(new File(blocksFolder + "/" + block.getName() + ".html"));
        }

        File soundsFolder = new File(destinationFolder + "/sounds");
        if (!soundsFolder.exists() && !soundsFolder.mkdirs()) throw new IOException("Failed to create sounds folder");
        new WikiSoundGenerator(itemSet).generate(soundsFolder);
    }
}
