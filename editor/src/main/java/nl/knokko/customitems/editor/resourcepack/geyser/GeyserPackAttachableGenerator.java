package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.block.model.CustomBlockModel;
import nl.knokko.customitems.block.model.SidedBlockModel;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.animated.AnimatedTexture;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Scanner;
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
            if (item instanceof KciCrossbow) {
                IOHelper.propagate(
                        "crossbow_template.attachable.json", zipOutput,
                        "attachables/kci/crossbow/" + item.getName() + ".attachable.json",
                        line -> line.replace("%TEXTURE_NAME%", item.getTexture().getName())
                                .replace("%ITEM_NAME%", item.getName())
                );
            }
        }
    }

    void generateShields() throws IOException {
        for (KciItem item : itemSet.items) {
            if (item instanceof KciShield && item.getGeyserModel() == null) {
                IOHelper.propagate(
                        "shield_template.attachable.json", zipOutput,
                        "attachables/kci/shield/" + item.getName() + ".attachable.json",
                        line -> line.replace("%ITEM_NAME%", item.getName())
                                .replace("%TEXTURE_NAME%", item.getTexture().getName())
                );
            }
        }
    }

    void generateArmor() throws IOException {
        for (KciItem item : itemSet.items) {
            if (item.getGeyserModel() != null) continue;

            if (item instanceof KciArmor) {
                KciArmor armor = (KciArmor) item;
                String armorName = null;
                if (armor.getFancyPantsTexture() != null) armorName = "fp_" + armor.getFancyPantsTexture().getName();
                if (armor.getArmorTexture() != null) armorName = "op_" + armor.getArmorTexture().getName();

                if (armorName != null) {
                    if (armor.getItemType().getMainCategory() == KciItemType.Category.LEGGINGS) armorName += "2";
                    else armorName += "1";

                    String finalArmorName = armorName;
                    String armorType = armor.getItemType().getMainCategory().name().toLowerCase(Locale.ROOT);
                    IOHelper.propagate(
                            "armor_template.attachable.json", zipOutput,
                            "attachables/kci/" + armor.getName() + ".attachable.json",
                            line -> line.replace("%ITEM_NAME%", armor.getName())
                                    .replace("%ARMOR_NAME%", finalArmorName)
                                    .replace("%ARMOR_TYPE%", armorType)
                    );
                }
            }
        }
    }

    void generateAnimations() throws IOException {
        for (KciItem item : itemSet.items) {
            if (item.getGeyserModel() == null && item.getTexture() instanceof AnimatedTexture) {
                AnimatedTexture texture = (AnimatedTexture) item.getTexture();

                zipOutput.putNextEntry(new ZipEntry("attachables/kci/animated/" + item.getName() + ".attachable.json"));
                PrintWriter attachableWriter = new PrintWriter(zipOutput);

                Scanner startScanner = new Scanner(getClass().getClassLoader().getResourceAsStream(
                        "nl/knokko/customitems/editor/geyser/animated_template_start.attachable.txt"
                ));
                while (startScanner.hasNextLine()) {
                    attachableWriter.println(startScanner.nextLine().replace("%ITEM_NAME%", item.getName()));
                }

                for (int frame = 1; frame <= texture.getImageReferences().size(); frame++) {
                    attachableWriter.println("\t\t\t\t\"frame" + frame + "\": \"textures/kci/" + texture.getName() + "/frame" + frame + "\",");
                }

                Scanner endScanner = new Scanner(getClass().getClassLoader().getResourceAsStream(
                        "nl/knokko/customitems/editor/geyser/animated_template_end.attachable.txt"
                ));
                while (endScanner.hasNextLine()) {
                    attachableWriter.println(endScanner.nextLine().replace("%TEXTURE_NAME%", texture.getName()));
                }

                attachableWriter.flush();
                zipOutput.closeEntry();
            }
        }
    }

    void generateCustomModels() throws IOException {
        for (KciItem item : itemSet.items) {
            GeyserCustomModel model = item.getGeyserModel();
            if (item instanceof KciBlockItem) {
                KciBlockItem blockItem = (KciBlockItem) item;
                if (blockItem.getBlock().getModel() instanceof CustomBlockModel) {
                    model = ((CustomBlockModel) blockItem.getBlock().getModel()).getGeyserModel();
                }
            }

            if (model != null) {
                zipOutput.putNextEntry(new ZipEntry(
                        "attachables/kci/custom/" + item.getName() + ".attachable.json"
                ));
                zipOutput.write(model.attachableFile);
                zipOutput.closeEntry();
            }

            if (item instanceof KciBlockItem && model == null) {
                KciBlockItem blockItem = (KciBlockItem) item;

                String blockModelType;
                String texturePath;
                if (blockItem.getBlock().getModel() instanceof SidedBlockModel) {
                    blockModelType = "sided";
                    texturePath = "sided/" + blockItem.getBlock().getName();
                } else {
                    blockModelType = "same";
                    texturePath = item.getTexture().getName();
                }

                IOHelper.propagate(
                        "block_template.attachable.json", zipOutput,
                        "attachables/kci/block/" + item.getName() + ".attachable.json",
                        line -> line.replace("%ITEM_NAME%", item.getName())
                                .replace("%TEXTURE_PATH%", texturePath)
                                .replace("%BLOCK_MODEL_TYPE%", blockModelType)
                );
            }
        }
    }
}
