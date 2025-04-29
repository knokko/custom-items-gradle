package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.editor.resourcepack.geyser.GeyserMappingsGenerator;
import nl.knokko.customitems.editor.resourcepack.geyser.GeyserPackGenerator;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.MCVersions.*;

public class ResourcepackGenerator {

    private final ItemSet itemSet;

    public ResourcepackGenerator(ItemSet itemSet) {
        this.itemSet = itemSet;
    }

    public void write(
            OutputStream rawOutputStream, byte[] cisTxtFileContent, Runnable notifyStartGeyserPack, boolean closeOutput
    ) throws IOException, ValidationException, ProgrammingValidationException {
        ZipOutputStream zipOutput = new PriorityZipOutputStream(rawOutputStream);

        ResourcepackCombiner combiner = new ResourcepackCombiner(itemSet, zipOutput, false);
        if (!itemSet.getExportSettings().shouldSkipResourcepack()) {
            combiner.writeEarly();

            ResourcepackTextureWriter textureWriter = new ResourcepackTextureWriter(itemSet, zipOutput);
            textureWriter.writeBaseTextures();
            textureWriter.writeArmorTextures();
            textureWriter.writeOptifineArmorTextures();
            textureWriter.writeOptifineElytraTextures();
            textureWriter.writeContainerOverlayTextures();

            ResourcepackFancyPants fancyPants = new ResourcepackFancyPants(itemSet, zipOutput);
            fancyPants.copyShaderAndLicense();
            fancyPants.generateEmptyTextures();
            fancyPants.generateFullTextures();

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

            ResourcepackItemOverrider itemOverrider = new ResourcepackItemOverrider(itemSet, zipOutput);
            itemOverrider.overrideItems();

            ResourcepackLanguageWriter languageWriter = new ResourcepackLanguageWriter(itemSet, zipOutput);
            languageWriter.writeLanguageFiles();

            writeAtlases(zipOutput);
        }

        writePackMcMeta(zipOutput);

        if (cisTxtFileContent != null) {
            ZipEntry cisTxtEntry = new ZipEntry("items.cis.txt");
            zipOutput.putNextEntry(cisTxtEntry);
            zipOutput.write(cisTxtFileContent);
            zipOutput.flush();
            zipOutput.closeEntry();
        }

        if (notifyStartGeyserPack != null) {
            notifyStartGeyserPack.run();
            zipOutput.putNextEntry(new ZipEntry("geyser.mcpack"));
            new GeyserPackGenerator(itemSet, zipOutput, false).write();
            zipOutput.closeEntry();

            zipOutput.putNextEntry(new ZipEntry("geyser_mappings.json"));
            GeyserMappingsGenerator geyserMapper = new GeyserMappingsGenerator(itemSet, zipOutput);
            geyserMapper.writeMappings();
            zipOutput.closeEntry();
        }

        if (!itemSet.getExportSettings().shouldSkipResourcepack()) combiner.writeLate();

        zipOutput.flush();
        if (closeOutput) zipOutput.close();
        else zipOutput.finish();
    }

    private void writePackMcMeta(ZipOutputStream zipOutput) throws IOException, ProgrammingValidationException {

        int mcVersion = itemSet.getExportSettings().getMcVersion();
        int packFormat = getPackFormat(mcVersion);

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

    private static int getPackFormat(int mcVersion) throws ProgrammingValidationException {
        int packFormat;
        if (mcVersion == VERSION1_12) {
            packFormat = 3;
        } else if (mcVersion == VERSION1_13 || mcVersion == VERSION1_14) {
            packFormat = 4;
        } else if (mcVersion == VERSION1_15) {
            packFormat = 5;
        } else if (mcVersion == VERSION1_16) {
            packFormat = 6;
        } else if (mcVersion == VERSION1_17) {
            packFormat = 7;
        } else if (mcVersion == VERSION1_18) {
            packFormat = 8;
        } else if (mcVersion == VERSION1_19) {
            packFormat = 13;
        } else if (mcVersion == VERSION1_20) {
            packFormat = 32;
        } else if (mcVersion == VERSION1_21) {
            packFormat = 55;
        } else {
            throw new ProgrammingValidationException("Unknown pack format for mc version " + mcVersion);
        }
        return packFormat;
    }

    private void writeAtlases(ZipOutputStream zipOutput) throws IOException {
        if (itemSet.getExportSettings().getMcVersion() >= VERSION1_19) {
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
