package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.editor.resourcepack.PriorityZipOutputStream;
import nl.knokko.customitems.editor.resourcepack.ResourcepackCombiner;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

public class GeyserPackGenerator {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;
    private final boolean closeOutput;

    public GeyserPackGenerator(ItemSet itemSet, OutputStream rawOutputStream, boolean closeOutput) {
        this.itemSet = itemSet;
        this.zipOutput = new PriorityZipOutputStream(rawOutputStream);
        this.closeOutput = closeOutput;
    }

    public void write() throws IOException {
        ResourcepackCombiner combiner = new ResourcepackCombiner(itemSet, zipOutput, true);
        combiner.writeEarly();

        generateManifest();

        GeyserPackTextureGenerator textureGenerator = new GeyserPackTextureGenerator(itemSet, zipOutput);
        textureGenerator.writeTexturesJsons();
        textureGenerator.writeTextures();

        GeyserPackAnimationGenerator animationsGenerator = new GeyserPackAnimationGenerator(itemSet, zipOutput);
        animationsGenerator.copyAnimations();
        animationsGenerator.writeCustomModelAnimations();

        GeyserPackAttachableGenerator attachableGenerator = new GeyserPackAttachableGenerator(itemSet, zipOutput);
        attachableGenerator.generateBows();
        attachableGenerator.generateArmor();
        attachableGenerator.generateCustomModels();

        GeyserPackModelGenerator modelGenerator = new GeyserPackModelGenerator(itemSet, zipOutput);
        modelGenerator.generateCustomModels();
        modelGenerator.generateBlockModel();

        GeyserPackControllerGenerator controllerGenerator = new GeyserPackControllerGenerator(itemSet, zipOutput);
        controllerGenerator.generateBow();

        combiner.writeLate();

        zipOutput.flush();
        if (closeOutput) zipOutput.close();
        else zipOutput.finish();
    }

    private void generateManifest() throws IOException {
        IOHelper.propagate(
                "manifest.json", zipOutput, "manifest.json",
                line -> line.replace("%RANDOM_ID%", UUID.randomUUID().toString())
        );
    }
}
