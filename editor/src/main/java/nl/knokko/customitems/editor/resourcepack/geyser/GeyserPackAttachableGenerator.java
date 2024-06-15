package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.item.KciBow;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.itemset.ItemSet;

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
        for (KciItem item : itemSet.items) {
            if (item instanceof KciBow) {
                IOHelper.propagate(
                        "bow_template.attachable.json", zipOutput,
                        "attachables/kci/bow/" + item.getName() + ".attachable.json",
                        line -> line.replace("%TEXTURE_NAME%", item.getTexture().getName())
                                .replace("%ITEM_NAME%", item.getName())
                );
            }
        }
    }

    void generateCustomModels() throws IOException {
        for (KciItem item : itemSet.items) {
            GeyserCustomModel model = item.getGeyserModel();
            if (model != null) {
                zipOutput.putNextEntry(new ZipEntry(
                        "attachables/kci/custom/" + item.getName() + ".attachable.json"
                ));
                zipOutput.write(model.attachableFile);
                zipOutput.closeEntry();
            }
        }
    }
}
