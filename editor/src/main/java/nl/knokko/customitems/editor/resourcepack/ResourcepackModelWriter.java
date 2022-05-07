package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.texture.BowTextureEntry;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.editor.resourcepack.DefaultItemModels.*;

class ResourcepackModelWriter {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    ResourcepackModelWriter(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    private void writeLines(OutputStream output, String[] lines) {
        PrintWriter jsonWriter = new PrintWriter(output);
        for (String line : lines) {
            jsonWriter.println(line);
        }
        jsonWriter.flush();
    }

    void writeCustomItemModels() throws IOException {
        for (CustomItemValues item : itemSet.getItems()) {

            // Core item model
            ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + ".json");
            zipOutput.putNextEntry(entry);

            if (item instanceof CustomBlockItemValues) {
                writeLines(zipOutput, createModelBlockItem(((CustomBlockItemValues) item).getBlock()));
            } else if (item instanceof CustomBowValues) {
                writeLines(zipOutput, getDefaultModelBow(item.getTexture().getName()));
            } else if (item instanceof CustomCrossbowValues) {
                writeLines(zipOutput, getDefaultModelCrossbow(item.getTexture().getName()));
            } else {
                DefaultModelType defaultModelType = DefaultModelType.BASIC;
                if (item instanceof CustomShieldValues) defaultModelType = DefaultModelType.SHIELD;
                if (item instanceof CustomTridentValues) defaultModelType = DefaultModelType.TRIDENT;

                ItemModel model = item.getModel();
                model.write(zipOutput, item, defaultModelType);
            }

            zipOutput.closeEntry();

            if (item instanceof CustomBowValues) {
                writeExtraBowModels(item, ((CustomBowValues) item).getTexture().getPullTextures(), false);
            } else if (item instanceof CustomCrossbowValues) {
                writeExtraBowModels(item, ((CustomCrossbowValues) item).getTexture().getPullTextures(), true);
                writeExtraCrossbowModels((CustomCrossbowValues) item);
            } else if (item instanceof CustomShieldValues) {
                writeExtraShieldModels((CustomShieldValues) item);
            } else if (item instanceof CustomTridentValues) {
                writeExtraTridentModels((CustomTridentValues) item);
            }
        }
    }

    void writeCustomBlockModels() throws IOException {
        // Write the models of all custom blocks
        for (CustomBlockValues block : itemSet.getBlocks()) {
            ZipEntry blockModelEntry = new ZipEntry("assets/minecraft/models/customblocks/" + block.getName() + ".json");
            zipOutput.putNextEntry(blockModelEntry);

            PrintWriter modelWriter = new PrintWriter(zipOutput);
            modelWriter.println("{");
            modelWriter.println("    \"parent\": \"block/cube_all\",");
            modelWriter.println("    \"textures\": {");
            modelWriter.println("        \"all\": \"customitems/" + block.getTexture().getName() + "\"");
            modelWriter.println("    }");
            modelWriter.println("}");
            modelWriter.flush();
        }
    }

    private void writeExtraBowModels(
            CustomItemValues bow, List<BowTextureEntry> pullTextures, boolean isForCrossbow
    ) throws IOException {
        String textureName = bow.getTexture().getName() + "_pulling_";
        for (int index = 0; index < pullTextures.size(); index++) {
            ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + bow.getName() + "_pulling_"
                    + index + ".json");
            zipOutput.putNextEntry(entry);
            PrintWriter jsonWriter = new PrintWriter(zipOutput);
            jsonWriter.println("{");
            if (isForCrossbow) {
                jsonWriter.println("    \"parent\": \"item/crossbow\",");
            } else {
                jsonWriter.println("    \"parent\": \"item/bow\",");
            }
            jsonWriter.println("    \"textures\": {");
            jsonWriter.println("        \"layer0\": \"customitems/" + textureName + index + "\"");
            jsonWriter.println("    }");
            jsonWriter.println("}");
            jsonWriter.flush();
            zipOutput.closeEntry();
        }
    }

    private void writeExtraCrossbowModels(CustomCrossbowValues item) throws IOException {
        // Add the models for the arrow texture and firework texture
        ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_arrow.json");
        zipOutput.putNextEntry(entry);

        PrintWriter jsonWriter = new PrintWriter(zipOutput);
        jsonWriter.println("{");
        jsonWriter.println("    \"parent\": \"item/crossbow\",");
        jsonWriter.println("    \"textures\": {");
        jsonWriter.println("        \"layer0\": \"customitems/" + item.getTexture().getName() + "_arrow\"");
        jsonWriter.println("    }");
        jsonWriter.println("}");
        jsonWriter.flush();
        zipOutput.closeEntry();

        entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_firework.json");
        zipOutput.putNextEntry(entry);
        jsonWriter = new PrintWriter(zipOutput);
        jsonWriter.println("{");
        jsonWriter.println("    \"parent\": \"item/crossbow\",");
        jsonWriter.println("    \"textures\": {");
        jsonWriter.println("        \"layer0\": \"customitems/" + item.getTexture().getName() + "_firework\"");
        jsonWriter.println("    }");
        jsonWriter.println("}");
        jsonWriter.flush();
        zipOutput.closeEntry();
    }

    private void writeExtraShieldModels(CustomShieldValues shield) throws IOException {
        byte[] blockingModel = shield.getCustomBlockingModel();
        ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + shield.getName() + "_blocking.json");
        zipOutput.putNextEntry(entry);
        if (blockingModel != null) {
            zipOutput.write(blockingModel);
            zipOutput.flush();
        } else {
            String[] modelContent = getDefaultModelBlockingShield(shield.getTexture().getName());
            PrintWriter jsonWriter = new PrintWriter(zipOutput);
            for (String line : modelContent) {
                jsonWriter.println(line);
            }
            jsonWriter.flush();
        }
    }

    private void writeExtraTridentModels(CustomTridentValues trident) throws IOException {
        byte[] inHandModel = trident.getCustomInHandModel();
        ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + trident.getName() + "_in_hand.json");
        zipOutput.putNextEntry(entry);
        if (inHandModel != null) {
            zipOutput.write(inHandModel);
            zipOutput.flush();
        } else {
            String[] modelContent = getDefaultModelTridentInHand(trident.getTexture().getName());
            PrintWriter jsonWriter = new PrintWriter(zipOutput);
            for (String line : modelContent) {
                jsonWriter.println(line);
            }
            jsonWriter.flush();
        }
        byte[] throwingModel = trident.getCustomThrowingModel();
        entry = new ZipEntry("assets/minecraft/models/customitems/" + trident.getName() + "_throwing.json");
        zipOutput.putNextEntry(entry);
        if (throwingModel != null) {
            zipOutput.write(throwingModel);
            zipOutput.flush();
        } else {
            String[] modelContent = getDefaultModelTridentThrowing(trident.getTexture().getName());
            PrintWriter jsonWriter = new PrintWriter(zipOutput);
            for (String line : modelContent) {
                jsonWriter.println(line);
            }
            jsonWriter.flush();
        }
    }

    void writeProjectileCoverModels() throws IOException {
        for (ProjectileCoverValues cover : itemSet.getProjectileCovers()) {
            ZipEntry entry = new ZipEntry("assets/minecraft/models/customprojectiles/" + cover.getName() + ".json");
            zipOutput.putNextEntry(entry);
            cover.writeModel(zipOutput);
            zipOutput.flush();
        }
    }
}
