package nl.knokko.customitems.editor.resourcepack.geyser;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.block.MushroomBlockMapping;
import nl.knokko.customitems.block.model.BlockModel;
import nl.knokko.customitems.block.model.CustomBlockModel;
import nl.knokko.customitems.block.model.SidedBlockModel;
import nl.knokko.customitems.item.KciBlockItem;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.cover.ProjectileCover;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class GeyserMappingsGenerator {

    private final ItemSet itemSet;
    private final OutputStream output;

    public GeyserMappingsGenerator(ItemSet itemSet, OutputStream output) {
        this.itemSet = itemSet;
        this.output = output;
    }

    private String getVanillaName(ProjectileCover cover) {
        return "minecraft:" + cover.getItemType().getModelName14();
    }

    private String getVanillaName(KciItem item) {
        if (item.getItemType() == KciItemType.OTHER) {
            return "minecraft:" + item.getOtherMaterial().name().toLowerCase(Locale.ROOT);
        } else return "minecraft:" + item.getItemType().getModelName14();
    }

    public void writeMappings() {
        Set<String> vanillaItems = new HashSet<>();
        for (KciItem item : itemSet.items) {
            vanillaItems.add(getVanillaName(item));
        }
        for (ProjectileCover cover : itemSet.projectileCovers) {
            vanillaItems.add(getVanillaName(cover));
        }

        PrintWriter jsonWriter = new PrintWriter(output);
        jsonWriter.println("{");
        jsonWriter.println("    \"format_version\": \"1\",");
        jsonWriter.println("    \"items\": {");

        int vanillaCounter = 0;
        for (String vanillaItem : vanillaItems) {
            vanillaCounter += 1;
            jsonWriter.println("        \"" + vanillaItem + "\": [");

            boolean isFirst = true;
            for (KciItem item : itemSet.items) {
                if (getVanillaName(item).equals(vanillaItem)) {
                    if (!isFirst) jsonWriter.println(',');
                    isFirst = false;
                    jsonWriter.println("            {");

                    GeyserCustomModel geyserModel = item.getGeyserModel();
                    if (item instanceof KciBlockItem) {
                        BlockModel blockModel = ((KciBlockItem) item).getBlock().getModel();
                        if (blockModel instanceof CustomBlockModel) {
                            geyserModel = ((CustomBlockModel) blockModel).getGeyserModel();
                        }
                    }

                    String prefix = "                ";
                    String attachableId = "kci_item_" + item.getName();
                    if (geyserModel != null) attachableId = geyserModel.attachableId;
                    jsonWriter.println(prefix + "\"name\": \"" + attachableId + "\",");
                    jsonWriter.println(prefix + "\"allow_offhand\": " + !item.isTwoHanded() + ",");
                    jsonWriter.println(prefix + "\"icon\": \"kci_" + item.getTexture().getName() + "\",");
                    jsonWriter.println(prefix + "\"custom_model_data\": " + item.getItemDamage());

                    jsonWriter.print("            }");
                }
            }

            for (ProjectileCover cover : itemSet.projectileCovers) {
                if (getVanillaName(cover).equals(vanillaItem)) {

                    if (!isFirst) jsonWriter.println(',');
                    isFirst = false;
                    jsonWriter.println("            {");

                    String icon = cover.getGeyserTexture() != null ? "kci_" + cover.getGeyserTexture().getName() : "fireball";

                    String prefix = "                ";
                    jsonWriter.println(prefix + "\"name\": \"kci_cover_" + cover.getName() + "\",");
                    jsonWriter.println(prefix + "\"allow_offhand\": true,");
                    jsonWriter.println(prefix + "\"icon\": \"" + icon + "\",");
                    jsonWriter.println(prefix + "\"custom_model_data\": " + cover.getItemDamage());

                    jsonWriter.print("            }");
                }
            }

            jsonWriter.println();

            jsonWriter.print("        ]");
            if (vanillaCounter != vanillaItems.size()) jsonWriter.print(',');
            jsonWriter.println();
        }

        if (itemSet.blocks.isEmpty()) jsonWriter.println("    }");
        else {
            jsonWriter.println("    },");
            jsonWriter.println("    \"blocks\": {");
        }

        List<MushroomBlockMapping.Type> vanillaTypes = new ArrayList<>(3);
        for (MushroomBlockMapping.Type type : MushroomBlockMapping.Type.values()) {
            if (itemSet.blocks.stream().anyMatch(block -> MushroomBlockMapping.getType(block.getInternalID()) == type)) {
                vanillaTypes.add(type);
            }
        }

        for (int index = 0; index < vanillaTypes.size(); index++) {
            MushroomBlockMapping.Type vanillaType = vanillaTypes.get(index);
            jsonWriter.println("        \"minecraft:" + vanillaType.getResourceName() + "\": {");
            jsonWriter.println("            \"name\": \"kci_" + vanillaType.getResourceName() + "\",");
            jsonWriter.println("            \"only_override_states\": true,");
            jsonWriter.println("            \"state_overrides\": {");

            List<KciBlock> blocks = itemSet.blocks.stream().filter(
                    block -> MushroomBlockMapping.getType(block.getInternalID()) == vanillaType
            ).collect(Collectors.toList());

            for (int blockIndex = 0; blockIndex < blocks.size(); blockIndex++) {
                KciBlock block = blocks.get(blockIndex);
                boolean[] directions = MushroomBlockMapping.getDirections(block.getInternalID());
                jsonWriter.println("                \"down=" + directions[0] + ",east=" + directions[1] +",north=" +
                        directions[2] + ",south=" + directions[3] + ",up=" + directions[4] + ",west=" + directions[5] + "\": {");
                jsonWriter.println("                    \"name\": \"kci_" + block.getName() + "\",");

                GeyserCustomModel geyserModel = null;
                if (block.getModel() instanceof CustomBlockModel) {
                    geyserModel = ((CustomBlockModel) block.getModel()).getGeyserModel();
                }

                if (geyserModel == null) {
                    jsonWriter.println("                    \"geometry\": \"minecraft:geometry.full_block\",");
                } else {
                    jsonWriter.println("                    \"geometry\": \"" + geyserModel.geometryId + "\",");
                }

                jsonWriter.println("                    \"material_instances\": {");

                if (geyserModel == null) {
                    jsonWriter.print("                        \"*\": { \"texture\": \"kci_" +
                            block.getModel().getPrimaryTexture().get().getName() + "\" }");
                } else {
                    jsonWriter.print("                        \"*\": { \"texture\": \"custom_kci_" + block.getName() + "\" }");
                }
                if (block.getModel() instanceof SidedBlockModel) {
                    jsonWriter.println(",");
                    SidedBlockModel model = (SidedBlockModel) block.getModel();
                    SidedBlockModel.TexturePair[] textures = model.getTexturePairs();

                    for (int textureIndex = 0; textureIndex < textures.length; textureIndex++) {
                        SidedBlockModel.TexturePair texture = textures[textureIndex];;
                        jsonWriter.print("                        \"" + texture.direction + "\": { \"texture\": \"kci_" +
                                texture.texture.get().getName() + "\" }");
                        if (textureIndex != textures.length - 1) jsonWriter.print(',');
                        jsonWriter.println();
                    }
                } else jsonWriter.println();
                jsonWriter.println("                    }");
                jsonWriter.print("                }");
                if (blockIndex != blocks.size() - 1) jsonWriter.print(',');
                jsonWriter.println();
            }

            jsonWriter.println("            }");
            jsonWriter.println("        }");
            jsonWriter.println("    }");
        }

        jsonWriter.println("}");
        jsonWriter.flush();
    }
}
