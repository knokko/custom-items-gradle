package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.block.model.CustomBlockModel;
import nl.knokko.customitems.item.KciBlockItem;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciShield;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.BowTexture;
import nl.knokko.customitems.texture.animated.AnimatedTexture;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class GeyserPackAnimationGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    GeyserPackAnimationGenerator(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void copyAnimations() throws IOException {
        if (itemSet.textures.stream().anyMatch(texture -> texture instanceof AnimatedTexture)) {
            IOHelper.propagate(
                    "kci_animated.animation.json", zipOutput,
                    "animations/kci/animated.animation.json", null
            );
        }

        if (itemSet.items.stream().anyMatch(item -> item instanceof KciBlockItem)) {
            IOHelper.propagate(
                    "kci_block.animation.json", zipOutput,
                    "animations/kci/block.animation.json", null
            );
        }

        if (itemSet.items.stream().anyMatch(item -> item instanceof KciShield)) {
            IOHelper.propagate(
                    "kci_shield.animation.json", zipOutput,
                    "animations/kci/shield.animation.json", null
            );
            IOHelper.propagate(
                    "kci_shield_blocking.animation.json", zipOutput,
                    "animations/kci/shield_blocking.animation.json", null
            );
        }

        if (itemSet.items.stream().anyMatch(
                item -> item.getGeyserModel() != null || item instanceof KciBlockItem ||
                        item.getTexture() instanceof AnimatedTexture
        )) {
            IOHelper.propagate(
                    "animation.geyser_custom.disable.json", zipOutput,
                    "animations/kci/disable.animation.json", null
            );
        }
    }

    void writeCustomModelAnimations() throws IOException {
        for (KciItem item : itemSet.items) {
            GeyserCustomModel model = item.getGeyserModel();
            if (model != null) {
                zipOutput.putNextEntry(new ZipEntry("animations/kci/custom/" + item.getName() + ".animation.json"));
                zipOutput.write(model.animationFile);
                zipOutput.closeEntry();
            }
        }

        for (KciBlock block : itemSet.blocks) {
            if (block.getModel() instanceof CustomBlockModel) {
                GeyserCustomModel geyserModel = ((CustomBlockModel) block.getModel()).getGeyserModel();
                if (geyserModel != null) {
                    zipOutput.putNextEntry(new ZipEntry("animations/kci/custom/blocks/" + block.getName() + ".animation.json"));
                    zipOutput.write(geyserModel.animationFile);
                    zipOutput.closeEntry();
                }
            }
        }
    }
}
