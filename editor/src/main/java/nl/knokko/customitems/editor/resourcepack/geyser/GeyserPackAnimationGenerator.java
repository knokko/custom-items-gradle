package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.BowTexture;

import java.io.IOException;
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
                    "animations/kci_bow.animation.json", null
            );
        }
    }
}
