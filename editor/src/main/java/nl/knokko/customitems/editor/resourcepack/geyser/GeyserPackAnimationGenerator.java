package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.BowTexture;

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

    void writeBowsJson() throws IOException {
        if (itemSet.textures.stream().anyMatch(texture -> texture instanceof BowTexture)) {
            IOHelper.propagate(
                    "kci_bow.animation.json", zipOutput,
                    "animations/kci/bow.animation.json", null
            );
        }
    }

    void writeCustomModelAnimations() throws IOException {
        if (itemSet.items.stream().anyMatch(item -> item.getGeyserModel() != null)) {
            IOHelper.propagate(
                    "animation.geyser_custom.disable.json", zipOutput,
                    "animations/kci/disable.animation.json", null
            );
        }

        for (KciItem item : itemSet.items) {
            GeyserCustomModel model = item.getGeyserModel();
            if (model != null) {
                zipOutput.putNextEntry(new ZipEntry("animations/kci/custom/" + item.getName() + ".animation.json"));
                zipOutput.write(model.animationFile);
                zipOutput.closeEntry();
            }
        }
    }
}
