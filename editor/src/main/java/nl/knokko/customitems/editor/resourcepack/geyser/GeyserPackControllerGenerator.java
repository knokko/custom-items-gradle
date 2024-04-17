package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.BowTexture;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

class GeyserPackControllerGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    GeyserPackControllerGenerator(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void generateBow() throws IOException {
        if (itemSet.textures.stream().anyMatch(texture -> texture instanceof BowTexture)) {
            IOHelper.propagate(
                    "kci_bow.render_controllers.json", zipOutput,
                    "render_controllers/kci_bow.render_controllers.json", null
            );
        }
    }
}
