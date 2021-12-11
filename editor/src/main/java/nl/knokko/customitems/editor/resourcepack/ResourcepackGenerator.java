package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcepackGenerator {

    private final SItemSet itemSet;
    private final int mcVersion;

    public ResourcepackGenerator(SItemSet itemSet, int mcVersion) {
        this.itemSet = itemSet;
        this.mcVersion = mcVersion;
    }

    public void write(OutputStream rawOutputStream) throws IOException, ValidationException, ProgrammingValidationException {
        ZipOutputStream zipOutput = new ZipOutputStream(rawOutputStream);

        ResourcepackTextureWriter textureWriter = new ResourcepackTextureWriter(itemSet, mcVersion, zipOutput);
        textureWriter.writeBaseTextures();
        textureWriter.writeOptifineArmorTextures();

        ResourcepackModelWriter modelWriter = new ResourcepackModelWriter(itemSet, zipOutput);
        modelWriter.writeCustomItemModels();
        modelWriter.writeCustomBlockModels();
        modelWriter.writeProjectileCoverModels();

        ResourcepackBlockOverrider blockOverrider = new ResourcepackBlockOverrider(itemSet, zipOutput);
        blockOverrider.overrideMushroomBlocks();

        ResourcepackItemOverrider itemOverrider = new ResourcepackItemOverrider(itemSet, mcVersion, zipOutput);
        itemOverrider.overrideItems();

        writePackMcMeta(zipOutput);
        zipOutput.flush();
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
}
