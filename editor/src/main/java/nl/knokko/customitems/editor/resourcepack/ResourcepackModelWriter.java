package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.cover.ProjectileCover;
import nl.knokko.customitems.texture.BowTextureEntry;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.MCVersions.VERSION1_20;
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
        boolean supportsArmorTrims = itemSet.getExportSettings().getMcVersion() >= VERSION1_20;
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
            } else if (item instanceof KciArmor && supportsArmorTrims && item.getModel() instanceof DefaultItemModel) {
                writeExtraArmorModels(item);
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

    private void writeExtraArmorModels(KciItem item) throws IOException {
        for (ArmorTrim trim : ARMOR_TRIMS) {
            String armorType = item.getItemType().getMainCategory().name().toLowerCase(Locale.ROOT);
            zipOutput.putNextEntry(new ZipEntry("assets/minecraft/models/customitems/" + item.getName() + "_" + trim.name + "_trim.json"));
            PrintWriter jsonWriter = new PrintWriter(zipOutput);
            jsonWriter.println("{");
            jsonWriter.println("    \"parent\": \"item/generated\",");
            jsonWriter.println("    \"textures\": {");
            jsonWriter.println("        \"layer0\": \"customitems/" + item.getTexture().getName() + "\",");
            if (item.getItemType().isLeatherArmor()) {
                jsonWriter.println("        \"layer1\": \"customitems/" + item.getTexture().getName() + "\",");
                jsonWriter.println("        \"layer2\": \"trims/items/" + armorType + "_trim_" + trim.name + "\"");
            } else {
                jsonWriter.println("        \"layer1\": \"trims/items/" + armorType + "_trim_" + trim.name + "\"");
            }
            jsonWriter.println("    }");
            jsonWriter.println("}");
            jsonWriter.flush();
            zipOutput.closeEntry();
        }
    }

    static class ArmorTrim {
        final String name;
        final String type;

        ArmorTrim(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    static final ArmorTrim[] ARMOR_TRIMS = {
            new ArmorTrim("quartz", "0.1"),
            new ArmorTrim("iron", "0.2"),
            new ArmorTrim("netherite", "0.3"),
            new ArmorTrim("redstone", "0.4"),
            new ArmorTrim("copper", "0.5"),
            new ArmorTrim("gold", "0.6"),
            new ArmorTrim("emerald", "0.7"),
            new ArmorTrim("diamond", "0.8"),
            new ArmorTrim("lapis", "0.9"),
            new ArmorTrim("amethyst", "1.0")
    };
}
