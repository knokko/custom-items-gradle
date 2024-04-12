package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.editor.resourcepack.PriorityZipOutputStream;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

public class GeyserPackGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    public GeyserPackGenerator(ItemSet itemSet, OutputStream rawOutputStream) {
        this.itemSet = itemSet;
        this.zipOutput = new PriorityZipOutputStream(rawOutputStream);
    }

    public void write() throws IOException {
        generateManifest();

        GeyserPackTextureGenerator textureGenerator = new GeyserPackTextureGenerator(itemSet, zipOutput);
        textureGenerator.writeTexturesJson();
        textureGenerator.writeTextures();

        GeyserPackAnimationGenerator animationsGenerator = new GeyserPackAnimationGenerator(itemSet, zipOutput);
        animationsGenerator.writeBowsJson();
        // TODO Crossbow animations and regular animations

        GeyserPackAttachableGenerator attachableGenerator = new GeyserPackAttachableGenerator(itemSet, zipOutput);
        attachableGenerator.generateBows();

        GeyserPackControllerGenerator controllerGenerator = new GeyserPackControllerGenerator(itemSet, zipOutput);
        controllerGenerator.generateBow();

        zipOutput.flush();
        zipOutput.close();
    }

    private void generateManifest() throws IOException {
        IOHelper.propagate(
                "manifest.json", zipOutput, "manifest.json",
                line -> line.replace("%RANDOM_ID%", UUID.randomUUID().toString())
        );
    }
}
