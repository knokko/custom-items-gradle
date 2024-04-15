package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.texture.BowTextureValues;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

class GeyserPackAttachableGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    GeyserPackAttachableGenerator(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void generateBows() throws IOException {
        for (BaseTextureValues texture : itemSet.textures) {
            if (texture instanceof BowTextureValues) {
                IOHelper.propagate(
                        "bow_template.attachable.json", zipOutput,
                        "attachables/kci_" + texture.getName() + ".attachable.json",
                        line -> line.replace("%TEXTURE_NAME%", texture.getName())
                );
            }
        }
    }
}
