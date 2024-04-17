package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.cover.ProjectileCover;
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
        for (KciItem item : itemSet.items) {

            // Core item model
            ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + ".json");
            zipOutput.putNextEntry(entry);

            if (item instanceof KciBlockItem) {
                writeLines(zipOutput, createModelBlockItem(((KciBlockItem) item).getBlock()));
            } else if (item instanceof KciBow) {
                writeLines(zipOutput, getDefaultModelBow(item.getTexture().getName()));
            } else if (item instanceof KciCrossbow) {
                writeLines(zipOutput, getDefaultModelCrossbow(item.getTexture().getName()));
            } else {
                ItemModel model = item.getModel();
                model.write(zipOutput, item.getName(), item.getTexture().getName(), item.getDefaultModelType(), item.getItemType().isLeatherArmor());
            }

            zipOutput.closeEntry();

            if (item instanceof KciBow) {
                writeExtraBowModels(item, ((KciBow) item).getTexture().getPullTextures(), false);
            } else if (item instanceof KciCrossbow) {
                writeExtraBowModels(item, ((KciCrossbow) item).getTexture().getPullTextures(), true);
                writeExtraCrossbowModels((KciCrossbow) item);
            } else if (item instanceof KciShield) {
                writeExtraShieldModels((KciShield) item);
            } else if (item instanceof KciTrident) {
                writeExtraTridentModels((KciTrident) item);
            }
        }
    }

    void writeCustomBlockModels() throws IOException {
        // Write the models of all custom blocks
        for (KciBlock block : itemSet.blocks) {
            ZipEntry blockModelEntry = new ZipEntry("assets/minecraft/models/customblocks/" + block.getName() + ".json");
            zipOutput.putNextEntry(blockModelEntry);

            block.getModel().write(zipOutput, block.getName());

            zipOutput.closeEntry();
        }
    }

    private void writeExtraBowModels(
            KciItem bow, List<BowTextureEntry> pullTextures, boolean isForCrossbow
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

    private void writeExtraCrossbowModels(KciCrossbow item) throws IOException {
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

    private void writeExtraShieldModels(KciShield shield) throws IOException {
        ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + shield.getName() + "_blocking.json");
        zipOutput.putNextEntry(entry);
        shield.getBlockingModel().write(zipOutput, shield.getName(), shield.getTexture().getName(), DefaultModelType.SHIELD_BLOCKING, false);
        zipOutput.closeEntry();
    }

    private void writeExtraTridentModels(KciTrident trident) throws IOException {
        ZipEntry entry = new ZipEntry("assets/minecraft/models/customitems/" + trident.getName() + "_in_hand.json");
        zipOutput.putNextEntry(entry);
        trident.getInHandModel().write(zipOutput, trident.getName(), trident.getTexture().getName(), DefaultModelType.TRIDENT_IN_HAND, false);
        zipOutput.closeEntry();

        entry = new ZipEntry("assets/minecraft/models/customitems/" + trident.getName() + "_throwing.json");
        zipOutput.putNextEntry(entry);
        trident.getThrowingModel().write(zipOutput, trident.getName(), trident.getTexture().getName(), DefaultModelType.TRIDENT_THROWING, false);
        zipOutput.closeEntry();
    }

    void writeProjectileCoverModels() throws IOException {
        for (ProjectileCover cover : itemSet.projectileCovers) {
            ZipEntry entry = new ZipEntry("assets/minecraft/models/customprojectiles/" + cover.getName() + ".json");
            zipOutput.putNextEntry(entry);
            cover.writeModel(zipOutput);
            zipOutput.flush();
        }
    }
}
