package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.texture.BowTexture;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class GeyserPackAttachableGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    GeyserPackAttachableGenerator(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void generateBows() throws IOException {
        for (KciTexture texture : itemSet.textures) {
            if (texture instanceof BowTexture) {
                IOHelper.propagate(
                        "bow_template.attachable.json", zipOutput,
                        "attachables/kci_" + texture.getName() + ".attachable.json",
                        line -> line.replace("%TEXTURE_NAME%", texture.getName())
                );
            }
        }
    }

    void generateCustomModels() throws IOException {
        for (KciItem item : itemSet.items) {
            GeyserCustomModel model = item.getGeyserModel();
            if (model != null) {
                String fileName = item.getName() + "." + model.attachableId + ".attachable.json";
                zipOutput.putNextEntry(new ZipEntry("attachables/minecraft/customitems/" + fileName));
                zipOutput.write(model.attachableFile);
                zipOutput.closeEntry();
            }
        }
    }
}
