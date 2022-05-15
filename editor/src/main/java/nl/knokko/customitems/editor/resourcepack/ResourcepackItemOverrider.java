package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.editor.util.VanillaModelProperties;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.durability.ItemDurabilityAssignments;
import nl.knokko.customitems.item.durability.ItemDurabilityClaim;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.BowTextureEntry;
import nl.knokko.customitems.util.ValidationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.knokko.customitems.MCVersions.VERSION1_13;
import static nl.knokko.customitems.editor.resourcepack.DefaultItemModels.getMinecraftModelTridentInHandBegin;
import static nl.knokko.customitems.editor.resourcepack.DefaultItemModels.getMinecraftModelTridentInHandEnd;

class ResourcepackItemOverrider {

    private final ItemSet itemSet;
    private final int mcVersion;
    private final ZipOutputStream zipOutput;

    ResourcepackItemOverrider(ItemSet itemSet, int mcVersion, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.mcVersion = mcVersion;
        this.zipOutput = zipOutput;
    }

    void overrideItems() throws IOException, ValidationException {

        Map<CustomItemType, ItemDurabilityAssignments> allDamageAssignments = itemSet.assignInternalItemDamages(this.mcVersion);
        for (Map.Entry<CustomItemType, ItemDurabilityAssignments> typeEntry : allDamageAssignments.entrySet()) {

            CustomItemType itemType = typeEntry.getKey();
            ItemDurabilityAssignments damageAssignments = typeEntry.getValue();

            if (!damageAssignments.claimList.isEmpty()) {

                if (itemType == CustomItemType.OTHER) {
                    overrideOtherItems(zipOutput, damageAssignments);
                } else {

                    String modelName;
                    String textureName;
                    if (mcVersion <= MCVersions.VERSION1_12) {
                        modelName = itemType.getModelName12();
                        textureName = itemType.getTextureName12();
                    } else {
                        modelName = itemType.getModelName14();
                        textureName = itemType.getTextureName14();
                    }

                    ZipEntry zipEntry = new ZipEntry("assets/minecraft/models/item/" + modelName + ".json");
                    zipOutput.putNextEntry(zipEntry);
                    final PrintWriter jsonWriter = new PrintWriter(zipOutput);

                    if (itemType == CustomItemType.BOW) {
                        overrideBow(jsonWriter, damageAssignments);
                    } else if (itemType == CustomItemType.CROSSBOW) {
                        overrideCrossBow(jsonWriter, damageAssignments);
                    } else if (itemType == CustomItemType.SHIELD) {
                        overrideShield(jsonWriter, damageAssignments);
                    } else {
                        overrideItem(jsonWriter, damageAssignments, itemType, modelName, textureName);
                    }
                    jsonWriter.flush();

                    // The trident base model is not special, but it does need a special in-hand model
                    if (itemType == CustomItemType.TRIDENT) {
                        overrideTridentInHand(jsonWriter, damageAssignments);
                    }

                    zipOutput.closeEntry();
                }
            }
        }
    }

    private void overrideOtherItems(
            ZipOutputStream zipOutput, ItemDurabilityAssignments dataAssignments
    ) throws IOException {
        Set<CIMaterial> usedOtherMaterials = EnumSet.noneOf(CIMaterial.class);

        for (CustomItemValues item : itemSet.getItems()) {
            if (item.getItemType() == CustomItemType.OTHER) {
                usedOtherMaterials.add(item.getOtherMaterial());
            }
        }

        for (CIMaterial currentOtherMaterial : usedOtherMaterials) {
            String modelName = currentOtherMaterial.name().toLowerCase(Locale.ROOT);
            String textureName;
            String parent;

            try {
                VanillaModelProperties vanillaModelProperties = VanillaModelProperties.valueOf(currentOtherMaterial.name());
                textureName = vanillaModelProperties.texture;
                parent = vanillaModelProperties.parent;
            } catch (IllegalArgumentException noModelInfo) {
                textureName = "item/" + modelName;
                parent = "item/handheld";
            }

            ZipEntry zipEntry = new ZipEntry("assets/minecraft/models/item/" + modelName + ".json");
            zipOutput.putNextEntry(zipEntry);
            final PrintWriter jsonWriter = new PrintWriter(zipOutput);

            // Begin of the json file
            jsonWriter.println("{");
            jsonWriter.println("    \"parent\": \"" + parent + "\",");
            if (textureName != null) {
                jsonWriter.println("    \"textures\": {");
                jsonWriter.println("        \"layer0\": \"" + textureName + "\"");
                jsonWriter.println("    },");
            }
            jsonWriter.println("    \"overrides\": [");

            // Some bookkeeping
            int maxItemDamage = 0;
            for (CustomItemValues item : itemSet.getItems()) {
                if (item.getItemType() == CustomItemType.OTHER && item.getOtherMaterial() == currentOtherMaterial && item.getItemDamage() > maxItemDamage) {
                    maxItemDamage = item.getItemDamage();
                }
            }

            // The interesting part...
            for (CustomItemValues item : itemSet.getItems().stream().sorted(Comparator.comparingInt(CustomItemValues::getItemDamage)).collect(Collectors.toList())) {
                if (item.getItemType() == CustomItemType.OTHER && item.getOtherMaterial() == currentOtherMaterial) {

                    // Find the corresponding claim
                    ItemDurabilityClaim claim = null;
                    for (ItemDurabilityClaim candidateClaim : dataAssignments.claimList) {
                        if (candidateClaim.itemDamage == item.getItemDamage()) {
                            claim = candidateClaim;
                        }
                    }

                    jsonWriter.print("        { \"predicate\": { \"custom_model_data\": " + claim.itemDamage + " }, \"model\": \"" + claim.resourcePath + "\" }");
                    if (item.getItemDamage() != maxItemDamage) {
                        jsonWriter.print(",");
                    }
                    jsonWriter.println();
                }
            }

            // End of the json file
            jsonWriter.println("    ]");
            jsonWriter.println("}");
            jsonWriter.flush();

            zipOutput.closeEntry();
        }
    }

    private void overrideBow(PrintWriter jsonWriter, ItemDurabilityAssignments damageAssignments) {

        // Begin of the json file
        jsonWriter.println("{");
        jsonWriter.println("    \"parent\": \"item/generated\",");
        jsonWriter.println("    \"textures\": {");
        jsonWriter.println("        \"layer0\": \"item/bow\"");
        jsonWriter.println("    },");

        // Display
        jsonWriter.println("    \"display\": {");
        jsonWriter.println("        \"thirdperson_righthand\": {");
        jsonWriter.println("            \"rotation\": [ -80, 260, -40 ],");
        jsonWriter.println("            \"translation\": [ -1, -2, 2.5 ],");
        jsonWriter.println("            \"scale\": [ 0.9, 0.9, 0.9 ]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"thirdperson_lefthand\": {");
        jsonWriter.println("            \"rotation\": [ -80, -280, 40 ],");
        jsonWriter.println("            \"translation\": [ -1, -2, 2.5 ],");
        jsonWriter.println("            \"scale\": [ 0.9, 0.9, 0.9 ]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"firstperson_righthand\": {");
        jsonWriter.println("            \"rotation\": [ 0, -90, 25 ],");
        jsonWriter.println("            \"translation\": [ 1.13, 3.2, 1.13 ],");
        jsonWriter.println("            \"scale\": [ 0.68, 0.68, 0.68 ]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"firstperson_lefthand\": {");
        jsonWriter.println("            \"rotation\": [ 0, 90, -25 ],");
        jsonWriter.println("            \"translation\": [ 1.13, 3.2, 1.13 ],");
        jsonWriter.println("            \"scale\": [ 0.68, 0.68, 0.68 ]");
        jsonWriter.println("        }");
        jsonWriter.println("    },");

        // The interesting part...
        jsonWriter.println("    \"overrides\": [");
        jsonWriter.println("        { \"predicate\": { \"pulling\": 1 }, \"model\": \"item/bow_pulling_0\"},");
        jsonWriter.println("        { \"predicate\": { \"pulling\": 1, \"pull\": 0.65 }, \"model\": \"item/bow_pulling_1\"},");
        jsonWriter.println("        { \"predicate\": { \"pulling\": 1, \"pull\": 0.9 }, \"model\": \"item/bow_pulling_2\"},");

        for (ItemDurabilityClaim claim : damageAssignments.claimList) {
            double damage = (double) claim.itemDamage / CustomItemType.BOW.getMaxDurability(this.mcVersion);
            jsonWriter.println("        { \"predicate\": {\"damaged\": 0, \"damage\": " + damage + "}, \"model\": \"" + claim.resourcePath + "\"},");
            List<BowTextureEntry> pullTextures = claim.pullTextures;

            int counter = 0;
            for (BowTextureEntry pullTexture : pullTextures) {
                jsonWriter.println(
                        "        { \"predicate\": {\"damaged\": 0, \"damage\": " + damage + ", \"pulling\": 1, \"pull\": "
                                + pullTexture.getPull() + "}, \"model\": \"" + claim.resourcePath + "_pulling_" + counter++ + "\"},"
                );
            }
        }
        // End of the json file
        jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/bow\"},");
        jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1 }, \"model\": \"item/bow_pulling_0\"},");
        jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.65 }, \"model\": \"item/bow_pulling_1\"},");
        jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.9 }, \"model\": \"item/bow_pulling_2\"}");
        jsonWriter.println("    ]");
        jsonWriter.println("}");
    }

    private void overrideCrossBow(PrintWriter jsonWriter, ItemDurabilityAssignments assignments) {

        // The crossbow model should always start with these lines:
        jsonWriter.println("{");
        jsonWriter.println("    \"parent\": \"item/generated\",");
        jsonWriter.println("    \"textures\": {");
        jsonWriter.println("        \"layer0\": \"item/crossbow_standby\"");
        jsonWriter.println("    },");
        jsonWriter.println("    \"display\": {");
        jsonWriter.println("        \"thirdperson_righthand\": {");
        jsonWriter.println("            \"rotation\": [-90, 0, -60],");
        jsonWriter.println("            \"translation\": [2, 0.1, -3],");
        jsonWriter.println("            \"scale\": [0.9, 0.9, 0.9]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"thirdperson_lefthand\": {");
        jsonWriter.println("            \"rotation\": [-90, 0, 30],");
        jsonWriter.println("            \"translation\": [2, 0.1, -3],");
        jsonWriter.println("            \"scale\": [0.9, 0.9, 0.9]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"firstperson_righthand\": {");
        jsonWriter.println("            \"rotation\": [-90, 0, -55],");
        jsonWriter.println("            \"translation\": [1.13, 3.2, 1.13],");
        jsonWriter.println("            \"scale\": [0.68, 0.68, 0.68]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"firstperson_lefthand\": {");
        jsonWriter.println("            \"rotation\": [-90, 0, 35],");
        jsonWriter.println("            \"translation\": [1.13, 3.2, 1.13],");
        jsonWriter.println("            \"scale\": [0.68, 0.68, 0.68]");
        jsonWriter.println("        }");
        jsonWriter.println("    },");
        jsonWriter.println("    \"overrides\": [");
        jsonWriter.println("        { \"predicate\": { \"pulling\": 1 }, \"model\": \"item/crossbow_pulling_0\" },");
        jsonWriter.println("        { \"predicate\": { \"pulling\": 1, \"pull\": 0.58 }, \"model\": \"item/crossbow_pulling_1\" },");
        jsonWriter.println("        { \"predicate\": { \"pulling\": 1, \"pull\": 1.0 }, \"model\": \"item/crossbow_pulling_2\" },");
        jsonWriter.println("        { \"predicate\": { \"charged\": 1 }, \"model\": \"item/crossbow_arrow\" },");
        jsonWriter.println("        { \"predicate\": { \"charged\": 1, \"firework\": 1 }, \"model\": \"item/crossbow_firework\" },");

        // This is where things get interesting...
        for (ItemDurabilityClaim claim : assignments.claimList) {

            double damageFraction = (double) claim.itemDamage / CustomItemType.CROSSBOW.getMaxDurability(this.mcVersion);
            jsonWriter.println("        { \"predicate\": { \"damaged\": 0, \"damage\": "
                    + damageFraction + " }, \"model\": \"" + claim.resourcePath + "\" },");

            List<BowTextureEntry> pullTextures = claim.pullTextures;
            int counter = 0;
            for (BowTextureEntry pullTexture : pullTextures) {
                jsonWriter.println("        { \"predicate\": { \"damaged\": 0, \"damage\": "
                        + damageFraction + ", \"pulling\": 1, \"pull\": " + pullTexture.getPull()
                        + " }, \"model\": \"" + claim.resourcePath + "_pulling_" + counter++ + "\" },");
            }

            jsonWriter.println("        { \"predicate\": { \"damaged\": 0, \"damage\": "
                    + damageFraction + ", \"charged\": 1 }, \"model\": \"" + claim.resourcePath
                    + "_arrow\" },");
            jsonWriter.println("        { \"predicate\": { \"damaged\": 0, \"damage\": "
                    + damageFraction + ", \"charged\": 1, \"firework\": 1 }, \"model\": \""
                    + claim.resourcePath + "_firework\" },");
        }

        // The crossbow model should always end with these lines:
        jsonWriter.println("        { \"predicate\": { \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/crossbow\" },");
        jsonWriter.println("        { \"predicate\": { \"pulling\": 1, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/crossbow_pulling_0\" },");
        jsonWriter.println("        { \"predicate\": { \"pulling\": 1, \"pull\": 0.58, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/crossbow_pulling_1\" },");
        jsonWriter.println("        { \"predicate\": { \"pulling\": 1, \"pull\": 1.0, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/crossbow_pulling_2\" },");
        jsonWriter.println("        { \"predicate\": { \"charged\": 1, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/crossbow_arrow\" },");
        jsonWriter.println("        { \"predicate\": { \"charged\": 1, \"firework\": 1, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/crossbow_firework\" }");
        jsonWriter.println("    ]");
        jsonWriter.println("}");
    }

    private void overrideShield(PrintWriter jsonWriter, ItemDurabilityAssignments damageAssignments) {
        // The beginning
        jsonWriter.println("{");
        jsonWriter.println("    \"parent\": \"builtin/entity\",");
        jsonWriter.println("    \"textures\": {");
        jsonWriter.println("        \"particle\": \"block/dark_oak_planks\"");
        jsonWriter.println("    },");
        jsonWriter.println("    \"display\": {");

        // All the display stuff, it's copied from minecrafts default shield model
        jsonWriter.println("        \"thirdperson_righthand\": {");
        jsonWriter.println("            \"rotation\": [0,90,0],");
        jsonWriter.println("            \"translation\": [10,6,-4],");
        jsonWriter.println("            \"scale\": [1,1,1]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"thirdperson_lefthand\": {");
        jsonWriter.println("            \"rotation\": [0,90,0],");
        jsonWriter.println("            \"translation\": [10,6,12],");
        jsonWriter.println("            \"scale\": [1,1,1]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"firstperson_righthand\": {");
        jsonWriter.println("            \"rotation\": [0,180,5],");
        jsonWriter.println("            \"translation\": [-10,2,-10],");
        jsonWriter.println("            \"scale\": [1.25,1.25,1.25]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"firstperson_lefthand\": {");
        jsonWriter.println("            \"rotation\": [0,180,5],");
        jsonWriter.println("            \"translation\": [10,0,-10],");
        jsonWriter.println("            \"scale\": [1.25,1.25,1.25]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"gui\": {");
        jsonWriter.println("            \"rotation\": [15,-25,-5],");
        jsonWriter.println("            \"translation\": [2,3,0],");
        jsonWriter.println("            \"scale\": [0.65,0.65,0.65]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"fixed\": {");
        jsonWriter.println("            \"rotation\": [0,180,0],");
        jsonWriter.println("            \"translation\": [-2,4,-5],");
        jsonWriter.println("            \"scale\": [0.5,0.5,0.5]");
        jsonWriter.println("        },");
        jsonWriter.println("        \"ground\": {");
        jsonWriter.println("            \"rotation\": [0,0,0],");
        jsonWriter.println("            \"translation\": [4,4,2],");
        jsonWriter.println("            \"scale\": [0.25,0.25,0.25]");
        jsonWriter.println("        }");
        jsonWriter.println("    }, \"overrides\": [");

        // The next entry is part of preserving vanilla shield blocking model
        jsonWriter.println("        { \"predicate\": { \"blocking\": 1 }, \"model\": \"item/shield_blocking\" },");

        // Now the part for the custom shield predicates...
        for (ItemDurabilityClaim claim : damageAssignments.claimList) {
            double damage = (double) claim.itemDamage / CustomItemType.SHIELD.getMaxDurability(this.mcVersion);
            jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 0, \"damage\": "
                    + damage + " }, \"model\": \"" + claim.resourcePath + "\" },");
            jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 0, \"damage\": "
                    + damage + " }, \"model\": \"" + claim.resourcePath + "_blocking\" },");
        }

        // The next ones are required to preserve the vanilla shield models
        jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield\" },");
        jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield_blocking\" }");

        // Now finish the json
        jsonWriter.println("    ]");
        jsonWriter.println("}");
    }

    private void overrideItem(
            PrintWriter jsonWriter, ItemDurabilityAssignments damageAssignments,
            CustomItemType itemType, String modelName, String textureName
    ) {

        // Begin of the json file
        jsonWriter.println("{");
        jsonWriter.println("    \"parent\": \"item/handheld\",");
        jsonWriter.println("    \"textures\": {");
        if (this.mcVersion >= VERSION1_13) {
            jsonWriter.print("        \"layer0\": \"item/" + textureName + "\"");
        } else {
            jsonWriter.print("        \"layer0\": \"items/" + textureName + "\"");
        }
        boolean isLeatherArmor = itemType.isLeatherArmor();
        if (isLeatherArmor) {
            jsonWriter.print(",");
        }
        jsonWriter.println();
        if (isLeatherArmor) {
            if (this.mcVersion >= VERSION1_13) {
                jsonWriter.print("        \"layer1\": \"item/" + textureName + "_overlay\"");
            } else {
                jsonWriter.print("        \"layer1\": \"items/" + textureName + "_overlay\"");
            }
        }
        jsonWriter.println("    },");
        jsonWriter.println("    \"overrides\": [");

        // Now the interesting part
        for (ItemDurabilityClaim claim : damageAssignments.claimList) {
            double damage = (double) claim.itemDamage / itemType.getMaxDurability(this.mcVersion);
            jsonWriter.println("        { \"predicate\": {\"damaged\": 0, \"damage\": " + damage + "}, \"model\": \"" + claim.resourcePath + "\"},");
        }

        // End of the json file
        jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/" + modelName + "\"}");
        jsonWriter.println("    ]");
        jsonWriter.println("}");
    }

    private void overrideTridentInHand(PrintWriter jsonWriter, ItemDurabilityAssignments damageAssignments) throws IOException {

        // The beginning:
        ZipEntry zipEntry = new ZipEntry("assets/minecraft/models/item/trident_in_hand.json");
        zipOutput.putNextEntry(zipEntry);
        String[] begin = getMinecraftModelTridentInHandBegin();

        String[] end = getMinecraftModelTridentInHandEnd();

        for (String line : begin) {
            jsonWriter.println(line);
        }

        for (ItemDurabilityClaim claim : damageAssignments.claimList) {
            double damage = (double) claim.itemDamage / CustomItemType.TRIDENT.getMaxDurability(this.mcVersion);
            jsonWriter.println("        { \"predicate\": { \"throwing\": 0, \"damaged\": 0, \"damage\": "
                    + damage + " }, \"model\": \"" + claim.resourcePath + "_in_hand\" },");
            jsonWriter.println("        { \"predicate\": { \"throwing\": 1, \"damaged\": 0, \"damage\": "
                    + damage + " }, \"model\": \"" + claim.resourcePath + "_throwing\" },");
        }

        for (String line : end) {
            jsonWriter.println(line);
        }

        jsonWriter.flush();
    }
}
