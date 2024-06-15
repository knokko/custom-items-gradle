package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.itemset.ItemSet;

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
    }
}
