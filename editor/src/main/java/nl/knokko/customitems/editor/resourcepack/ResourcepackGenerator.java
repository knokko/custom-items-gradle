package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.MCVersions.VERSION1_19;

public class ResourcepackGenerator {

    private final ItemSet itemSet;
    private final int mcVersion;

    public ResourcepackGenerator(ItemSet itemSet, int mcVersion) {
        this.itemSet = itemSet;
        this.mcVersion = mcVersion;
    }

    public void write(OutputStream rawOutputStream) throws IOException, ValidationException, ProgrammingValidationException {
        ZipOutputStream zipOutput = new ZipOutputStream(rawOutputStream);

        ResourcepackTextureWriter textureWriter = new ResourcepackTextureWriter(itemSet, mcVersion, zipOutput);
        textureWriter.writeBaseTextures();
        textureWriter.writeOptifineArmorTextures();
        textureWriter.writeOptifineElytraTextures();
        textureWriter.writeContainerOverlayTextures();

        ResourcepackFontOverrider fontWriter = new ResourcepackFontOverrider(itemSet, zipOutput);
        fontWriter.overrideContainerOverlayChars();

        ResourcepackSoundWriter soundWriter = new ResourcepackSoundWriter(itemSet, zipOutput);
        soundWriter.writeSoundsJson();
        soundWriter.writeSoundFiles();

        ResourcepackModelWriter modelWriter = new ResourcepackModelWriter(itemSet, zipOutput);
        modelWriter.writeCustomItemModels();
        modelWriter.writeCustomBlockModels();
        modelWriter.writeProjectileCoverModels();

        ResourcepackBlockOverrider blockOverrider = new ResourcepackBlockOverrider(itemSet, zipOutput);
        blockOverrider.overrideMushroomBlocks();

        ResourcepackItemOverrider itemOverrider = new ResourcepackItemOverrider(itemSet, mcVersion, zipOutput);
        itemOverrider.overrideItems();

        writePackMcMeta(zipOutput);
        writeAtlases(zipOutput);
        zipOutput.flush();
        zipOutput.close();
    }

    private void writePackMcMeta(ZipOutputStream zipOutput) throws IOException, ProgrammingValidationException {

        int packFormat;
        if (mcVersion == MCVersions.VERSION1_12) {
            packFormat = 3;
        } else if (mcVersion == MCVersions.VERSION1_13 || mcVersion == MCVersions.VERSION1_14) {
            packFormat = 4;
        } else if (mcVersion == MCVersions.VERSION1_15) {
            packFormat = 5;
        } else if (mcVersion == MCVersions.VERSION1_16) {
            packFormat = 6;
        } else if (mcVersion == MCVersions.VERSION1_17) {
            packFormat = 7;
        } else if (mcVersion == MCVersions.VERSION1_18) {
            packFormat = 8;
        } else if (mcVersion == VERSION1_19) {
            packFormat = 12;
        } else {
            throw new ProgrammingValidationException("Unknown pack format for mc version " + mcVersion);
        }

        ZipEntry mcMeta = new ZipEntry("pack.mcmeta");
        zipOutput.putNextEntry(mcMeta);
        PrintWriter jsonWriter = new PrintWriter(zipOutput);
        jsonWriter.println("{");
        jsonWriter.println("    \"pack\": {");
        jsonWriter.println("        \"pack_format\": " + packFormat + ",");
        jsonWriter.println("        \"description\": \"KnokkosCustomItems generated resourcepack\"");
        jsonWriter.println("    }");
        jsonWriter.println("}");
        jsonWriter.flush();
        zipOutput.closeEntry();
    }

    private void writeAtlases(ZipOutputStream zipOutput) throws IOException {
        if (mcVersion >= VERSION1_19) {
            ZipEntry customAtlas = new ZipEntry("assets/minecraft/atlases/blocks.json");
            zipOutput.putNextEntry(customAtlas);

            PrintWriter jsonWriter = new PrintWriter(zipOutput);
            jsonWriter.println("{");
            jsonWriter.println("    \"sources\": [");
            jsonWriter.println("        {");
            jsonWriter.println("            \"type\": \"directory\",");
            jsonWriter.println("            \"source\": \"customitems\",");
            jsonWriter.println("            \"prefix\": \"customitems/\"");
            jsonWriter.println("        }");
            jsonWriter.println("    ]");
            jsonWriter.println("}");
            jsonWriter.flush();
            zipOutput.closeEntry();
        }
    }
}
