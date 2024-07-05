package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.block.model.CustomBlockModel;
import nl.knokko.customitems.block.model.SidedBlockModel;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.itemset.ItemSet;

import java.io.IOException;
import java.util.Locale;
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
