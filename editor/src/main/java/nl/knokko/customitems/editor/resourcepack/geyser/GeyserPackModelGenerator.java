package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.block.model.CustomBlockModel;
import nl.knokko.customitems.block.model.SidedBlockModel;
import nl.knokko.customitems.block.model.SimpleBlockModel;
import nl.knokko.customitems.item.KciBlockItem;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciShield;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.animated.AnimatedTexture;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GeyserPackModelGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    GeyserPackModelGenerator(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void generateCustomModels() throws IOException {
        for (KciItem item : itemSet.items) {
            GeyserCustomModel model = item.getGeyserModel();
            if (model != null) {
                zipOutput.putNextEntry(new ZipEntry("models/blocks/kci/" + item.getName() + ".model.json"));
                zipOutput.write(model.modelFile);
                zipOutput.closeEntry();
            }
        }

        for (KciBlock block : itemSet.blocks) {
            if (block.getModel() instanceof CustomBlockModel) {
                GeyserCustomModel model = ((CustomBlockModel) block.getModel()).getGeyserModel();
                if (model != null) {
                    zipOutput.putNextEntry(new ZipEntry("models/blocks/kci/blocks/" + block.getName() + ".model.json"));
                    zipOutput.write(model.modelFile);
                    zipOutput.closeEntry();
                }
            }
        }
    }

    void generateAnimationModel() throws IOException {
        if (itemSet.textures.stream().anyMatch(texture -> texture instanceof AnimatedTexture)) {
            IOHelper.propagate(
                    "kci_animated.geo.json", zipOutput,
                    "models/blocks/kci/animated.geo.json", null
            );
        }
    }

    void generateShieldModel() throws IOException {
        if (itemSet.items.stream().anyMatch(item -> item instanceof KciShield)) {
            IOHelper.propagate(
                    "kci_shield.geo.json", zipOutput,
                    "models/blocks/kci/shield.geo.json", null
            );
        }
    }

    void generateBlockModel() throws IOException {
        if (itemSet.items.stream().anyMatch(
                item -> item instanceof KciBlockItem && ((KciBlockItem) item).getBlock().getModel() instanceof SimpleBlockModel
        )) {
            IOHelper.propagate(
                    "same_block.geo.json", zipOutput,
                    "models/blocks/kci/same_block.geo.json", null
            );
        }
        if (itemSet.items.stream().anyMatch(
                item -> item instanceof KciBlockItem && ((KciBlockItem) item).getBlock().getModel() instanceof SidedBlockModel
        )) {
            IOHelper.propagate(
                    "sided_block.geo.json", zipOutput,
                    "models/blocks/kci/sided_block.geo.json", null
            );
        }
    }
}
