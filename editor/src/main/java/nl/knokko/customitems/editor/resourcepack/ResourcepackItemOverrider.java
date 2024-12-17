package nl.knokko.customitems.editor.resourcepack;

import com.github.cliftonlabs.json_simple.JsonObject;
import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.editor.util.VanillaModelProperties;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.item.KciItem;
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

import static nl.knokko.customitems.MCVersions.*;
import static nl.knokko.customitems.editor.resourcepack.DefaultItemModels.getMinecraftModelTridentInHandBegin;
import static nl.knokko.customitems.editor.resourcepack.ResourcepackModelWriter.ARMOR_TRIMS;

class ResourcepackItemOverrider {

    private final ItemSet itemSet;
    private final ZipOutputStream zipOutput;

    ResourcepackItemOverrider(ItemSet itemSet, ZipOutputStream zipOutput) {
        this.itemSet = itemSet;
        this.zipOutput = zipOutput;
    }

    void overrideItems() throws IOException, ValidationException {

        Map<KciItemType, ItemDurabilityAssignments> allDamageAssignments = itemSet.assignInternalItemDamages();
        for (Map.Entry<KciItemType, ItemDurabilityAssignments> typeEntry : allDamageAssignments.entrySet()) {

            KciItemType itemType = typeEntry.getKey();
            ItemDurabilityAssignments damageAssignments = typeEntry.getValue();

            if (!damageAssignments.claimList.isEmpty()) {

                if (itemType == KciItemType.OTHER) {
                    overrideOtherItems(zipOutput, damageAssignments);
                } else {

                    String modelName;
                    String textureName;
                    if (itemSet.getExportSettings().getMcVersion() <= MCVersions.VERSION1_12) {
                        modelName = itemType.getModelName12();
                        textureName = itemType.getTextureName12();
                    } else {
                        modelName = itemType.getModelName14();
                        textureName = itemType.getTextureName14();
                    }

                    boolean isArmor = itemType.canServe(KciItemType.Category.HELMET) ||
                            itemType.canServe(KciItemType.Category.CHESTPLATE) ||
                            itemType.canServe(KciItemType.Category.LEGGINGS) ||
                            itemType.canServe(KciItemType.Category.BOOTS);

                    if (itemSet.getExportSettings().getMcVersion() >= VERSION1_21) {
                        zipOutput.putNextEntry(new ZipEntry("assets/minecraft/items/" + modelName + ".json"));
                        if (itemType == KciItemType.BOW) {
                            overrideModernBow(new PrintWriter(zipOutput), damageAssignments);
                        } else if (itemType == KciItemType.CROSSBOW) {
                            overrideModernCrossbow(new PrintWriter(zipOutput), damageAssignments);
                        } else if (itemType == KciItemType.SHIELD) {
                            overrideModernShield(new PrintWriter(zipOutput), damageAssignments);
                        } else if (itemType == KciItemType.ELYTRA) {
                            overrideModernElytra(new PrintWriter(zipOutput), damageAssignments);
                        } else if (isArmor) {
                            overrideModernArmor(
                                    new PrintWriter(zipOutput), "item/" + textureName,
                                    itemType.isLeatherArmor(), damageAssignments
                            );
                        } else if (itemType == KciItemType.TRIDENT) {
                            overrideModernTrident(new PrintWriter(zipOutput), damageAssignments);
                        } else {
                            overrideModernItem(new PrintWriter(zipOutput), modelName, null, damageAssignments);
                        }
                        zipOutput.closeEntry();
                        continue;
                    }

                    ZipEntry zipEntry = new ZipEntry("assets/minecraft/models/item/" + modelName + ".json");
                    zipOutput.putNextEntry(zipEntry);
                    final PrintWriter jsonWriter = new PrintWriter(zipOutput);

                    if (itemType == KciItemType.BOW) {
                        overrideBow(jsonWriter, damageAssignments);
                    } else if (itemType == KciItemType.CROSSBOW) {
                        overrideCrossBow(jsonWriter, damageAssignments);
                    } else if (itemType == KciItemType.SHIELD) {
                        overrideShield(jsonWriter, damageAssignments);
                    } else if (itemType == KciItemType.ELYTRA) {
                        overrideElytra(jsonWriter, damageAssignments);
                    } else if (isArmor && itemSet.getExportSettings().getMcVersion() >= VERSION1_20) {
                        overrideArmor(jsonWriter, damageAssignments, itemType, textureName);
                    } else {
                        overrideItem(jsonWriter, damageAssignments, itemType, modelName, textureName);
                    }
                    jsonWriter.flush();

                    // The trident base model is not special, but it does need a special in-hand model
                    if (itemType == KciItemType.TRIDENT) {
                        overrideTridentInHand(jsonWriter, damageAssignments);
                    }

                    zipOutput.closeEntry();
                }
            }
        }
    }

    private void overrideModernBow(PrintWriter output, ItemDurabilityAssignments dataAssignments) throws IOException {
        List<JsonObject> entries = new ArrayList<>(dataAssignments.claimList.size());
        for (ItemDurabilityClaim claim : dataAssignments.claimList) {
            entries.add(createBowEntry(claim.resourcePath, claim.pullTextures, claim.itemDamage));
        }

        List<BowTextureEntry> vanillaPulls = new ArrayList<>();
        vanillaPulls.add(BowTextureEntry.createQuick(null, 0.0));
        vanillaPulls.add(BowTextureEntry.createQuick(null, 0.65));
        vanillaPulls.add(BowTextureEntry.createQuick(null, 0.9));

        overrideModernItemTemplate(output, createBowEntry("item/bow", vanillaPulls, -1), entries);
    }

    private void overrideModernCrossbow(PrintWriter output, ItemDurabilityAssignments dataAssignments) throws IOException {
        List<JsonObject> entries = new ArrayList<>(dataAssignments.claimList.size());
        for (ItemDurabilityClaim claim : dataAssignments.claimList) {
            entries.add(createCrossbowEntry(claim.resourcePath, claim.pullTextures, claim.itemDamage));
        }

        List<BowTextureEntry> vanillaPulls = new ArrayList<>();
        vanillaPulls.add(BowTextureEntry.createQuick(null, 0.0));
        vanillaPulls.add(BowTextureEntry.createQuick(null, 0.58));
        vanillaPulls.add(BowTextureEntry.createQuick(null, 1.0));

        overrideModernItemTemplate(output, createCrossbowEntry("item/crossbow", vanillaPulls, -1), entries);
    }

    private void overrideModernShield(PrintWriter output, ItemDurabilityAssignments dataAssignments) throws IOException {
        List<JsonObject> entries = new ArrayList<>(dataAssignments.claimList.size());
        for (ItemDurabilityClaim claim : dataAssignments.claimList) {
            entries.add(createShieldEntry(claim.resourcePath, claim.itemDamage));
        }

        overrideModernItemTemplate(output, createShieldEntry("item/shield", -1), entries);
    }

    private void overrideModernElytra(PrintWriter output, ItemDurabilityAssignments dataAssignments) throws IOException {
        List<JsonObject> entries = new ArrayList<>(dataAssignments.claimList.size());
        for (ItemDurabilityClaim claim : dataAssignments.claimList) {
            entries.add(createElytraEntry(claim.resourcePath, claim.itemDamage));
        }

        overrideModernItemTemplate(output, createElytraEntry("item/elytra", -1), entries);
    }

    private void overrideModernArmor(
            PrintWriter output, String vanillaTexturePath, boolean isLeather,
            ItemDurabilityAssignments dataAssignments
    ) throws IOException {
        List<JsonObject> entries = new ArrayList<>(dataAssignments.claimList.size());
        for (ItemDurabilityClaim claim : dataAssignments.claimList) {
            entries.add(createArmorEntry(claim.resourcePath, claim.itemDamage, false));
        }

        overrideModernItemTemplate(output, createArmorEntry(vanillaTexturePath, -1, isLeather), entries);
    }

    private void overrideModernTrident(PrintWriter output, ItemDurabilityAssignments dataAssignments) throws IOException {
        List<JsonObject> entries = new ArrayList<>(dataAssignments.claimList.size());
        for (ItemDurabilityClaim claim : dataAssignments.claimList) {
            entries.add(createTridentEntry(claim.resourcePath, claim.itemDamage));
        }

        overrideModernItemTemplate(output, createTridentEntry("item/trident", -1), entries);
    }

    private JsonObject createBowEntry(String texturePrefix, List<BowTextureEntry> pulls, int threshold) {
        JsonObject root = new JsonObject();
        JsonObject outerModel = new JsonObject();
        outerModel.put("type", "condition");
        outerModel.put("property", "using_item");
        outerModel.put("on_false", createModernLeaf(texturePrefix));

        JsonObject using = new JsonObject();
        using.put("type", "range_dispatch");
        using.put("property", "use_duration");
        using.put("scale", 0.05);
        using.put("fallback", createModernLeaf(texturePrefix + "_pulling_0"));
        List<JsonObject> pullEntries = new ArrayList<>(pulls.size() - 1);
        int pullIndex = 0;
        for (BowTextureEntry pull : pulls) {
            if (pullIndex != 0) {
                JsonObject pullEntry = new JsonObject();
                pullEntry.put("threshold", pull.getPull());
                pullEntry.put("model", createModernLeaf(texturePrefix + "_pulling_" + pullIndex));
                pullEntries.add(pullEntry);
            }
            pullIndex += 1;
        }
        using.put("entries", pullEntries);
        outerModel.put("on_true", using);

        if (threshold == -1) return outerModel;
        root.put("model", outerModel);
        root.put("threshold", threshold);
        return root;
    }

    private JsonObject createCrossbowEntry(String texturePrefix, List<BowTextureEntry> pulls, int threshold) {
        JsonObject root = new JsonObject();
        JsonObject outerModel = new JsonObject();
        outerModel.put("type", "condition");
        outerModel.put("property", "using_item");

        JsonObject notPulling = new JsonObject();
        notPulling.put("type", "select");
        notPulling.put("property", "charge_type");
        notPulling.put("fallback", createModernLeaf(texturePrefix));
        List<JsonObject> cases = new ArrayList<>(2);
        JsonObject arrowCase = new JsonObject();
        arrowCase.put("when", "arrow");
        arrowCase.put("model", createModernLeaf(texturePrefix + "_arrow"));
        JsonObject fireworkCase = new JsonObject();
        fireworkCase.put("when", "rocket");
        fireworkCase.put("model", createModernLeaf(texturePrefix + "_firework"));
        cases.add(arrowCase);
        cases.add(fireworkCase);
        notPulling.put("cases", cases);
        outerModel.put("on_false", notPulling);

        JsonObject pulling = new JsonObject();
        pulling.put("type", "range_dispatch");
        pulling.put("property", "crossbow/pull");
        pulling.put("fallback", createModernLeaf(texturePrefix + "_pulling_0"));
        List<JsonObject> entries = new ArrayList<>(pulls.size() - 1);
        int index = 0;
        for (BowTextureEntry pull : pulls) {
            if (index != 0) {
                JsonObject entry = new JsonObject();
                entry.put("threshold", pull.getPull());
                entry.put("model", createModernLeaf(texturePrefix + "_pulling_" + index));
                entries.add(entry);
            }
            index += 1;
        }
        pulling.put("entries", entries);
        outerModel.put("on_true", pulling);

        if (threshold == -1) return outerModel;
        root.put("model", outerModel);
        root.put("threshold", threshold);
        return root;
    }

    private JsonObject createShieldEntry(String texturePrefix, int threshold) {
        JsonObject root = new JsonObject();
        JsonObject outerModel = new JsonObject();

        outerModel.put("type", "condition");
        outerModel.put("property", "using_item");
        if (threshold == -1) {
            outerModel.put("on_false", createVanillaShieldSpecial(texturePrefix));
            outerModel.put("on_true", createVanillaShieldSpecial(texturePrefix + "_blocking"));
            return outerModel;
        }
        outerModel.put("on_false", createModernLeaf(texturePrefix));
        outerModel.put("on_true", createModernLeaf(texturePrefix + "_blocking"));

        root.put("model", outerModel);
        root.put("threshold", threshold);
        return root;
    }

    private JsonObject createElytraEntry(String texturePrefix, int threshold) {
        JsonObject root = new JsonObject();
        JsonObject outerModel = new JsonObject();

        outerModel.put("type", "condition");
        outerModel.put("property", "broken");
        outerModel.put("on_false", createModernLeaf(texturePrefix));
        // TODO Handle broken custom elytras
        outerModel.put("on_true", createModernLeaf("item/elytra_broken"));

        if (threshold == -1) return outerModel;
        root.put("model", outerModel);
        root.put("threshold", threshold);
        return root;
    }

    private JsonObject createArmorEntry(String texturePrefix, int threshold, boolean isLeather) {
        JsonObject root = new JsonObject();
        JsonObject outerModel = new JsonObject();

        outerModel.put("type", "select");
        outerModel.put("property", "trim_material");
        outerModel.put("fallback", createArmorLeaf(texturePrefix, isLeather));

        List<JsonObject> cases = new ArrayList<>(ARMOR_TRIMS.length);
        for (ResourcepackModelWriter.ArmorTrim trim : ARMOR_TRIMS) {
            JsonObject trimCase = new JsonObject();
            trimCase.put("when", trim.name);
            trimCase.put("model", createArmorLeaf(texturePrefix + "_" + trim.name + "_trim", isLeather));
            cases.add(trimCase);
        }
        outerModel.put("cases", cases);

        if (threshold == -1) return outerModel;
        root.put("model", outerModel);
        root.put("threshold", threshold);
        return root;
    }

    private JsonObject createTridentEntry(String texturePrefix, int threshold) {
        JsonObject root = new JsonObject();
        JsonObject outerModel = new JsonObject();

        outerModel.put("type", "select");
        outerModel.put("property", "display_context");
        JsonObject simpleCase = new JsonObject();
        simpleCase.put("model", createModernLeaf(texturePrefix));
        List<String> simpleWhen = new ArrayList<>(3);
        simpleWhen.add("gui");
        simpleWhen.add("ground");
        simpleWhen.add("fixed");
        simpleCase.put("when", simpleWhen);

        List<JsonObject> simpleCases = new ArrayList<>(1);
        simpleCases.add(simpleCase);
        outerModel.put("cases", simpleCases);

        JsonObject fallback = new JsonObject();
        fallback.put("type", "condition");
        fallback.put("property", "using_item");

        if (threshold == -1) {
            fallback.put("on_false", createVanillaTridentSpecial("item/trident_in_hand"));
            fallback.put("on_true", createVanillaTridentSpecial("item/trident_throwing"));
        } else {
            fallback.put("on_false", createModernLeaf(texturePrefix + "_in_hand"));
            fallback.put("on_true", createModernLeaf(texturePrefix + "_throwing"));
        }

        outerModel.put("fallback", fallback);
        if (threshold == -1) return outerModel;
        root.put("model", outerModel);
        root.put("threshold", threshold);
        return root;
    }

    private JsonObject createArmorLeaf(String texture, boolean isLeather) {
        JsonObject leaf = createModernLeaf(texture);
        if (isLeather) {
            JsonObject tints = new JsonObject();
            tints.put("type", "dye");
            tints.put("default", -6265536);

            List<JsonObject> tintList = new ArrayList<>(1);
            tintList.add(tints);
            leaf.put("tints", tintList);
        }
        return leaf;
    }

    private JsonObject createVanillaShieldSpecial(String texture) {
        JsonObject special = new JsonObject();
        special.put("type", "special");
        special.put("base", texture);

        JsonObject model = new JsonObject();
        model.put("type", "shield");
        special.put("model", model);

        return special;
    }

    private JsonObject createVanillaTridentSpecial(String texture) {
        JsonObject special = new JsonObject();
        special.put("type", "special");
        special.put("base", texture);

        JsonObject model = new JsonObject();
        model.put("type", "trident");
        special.put("model", model);

        return special;
    }

    private void overrideModernItemTemplate(
            PrintWriter output, JsonObject fallback, List<JsonObject> entries
    ) throws IOException {
        JsonObject root = new JsonObject();
        JsonObject model = new JsonObject();
        model.put("type", "range_dispatch");
        model.put("property", "custom_model_data");

        model.put("fallback", fallback);
        model.put("entries", entries);

        root.put("model", model);
        root.toJson(output);
        output.flush();
    }

    private void overrideModernItem(
            PrintWriter output, String fallbackItem,
            List<KciItem> items, ItemDurabilityAssignments dataAssignments
    ) throws IOException {
        List<JsonObject> entries = createSimpleEntries(items, dataAssignments);
        overrideModernItemTemplate(output, createModernLeaf("item/" + fallbackItem), entries);
    }

    private static JsonObject createClaimEntry(ItemDurabilityClaim claim) {
        JsonObject entry = new JsonObject();
        entry.put("threshold", claim.itemDamage);
        entry.put("model", createModernLeaf(claim.resourcePath));
        return entry;
    }

    private static JsonObject createModernLeaf(String texture) {
        JsonObject leaf = new JsonObject();
        leaf.put("type", "model");
        leaf.put("model", texture);
        return leaf;
    }

    private static List<JsonObject> createSimpleEntries(List<KciItem> items, ItemDurabilityAssignments dataAssignments) {
        if (items == null) {
            List<JsonObject> entries = new ArrayList<>(dataAssignments.claimList.size());

            for (ItemDurabilityClaim claim : dataAssignments.claimList) {
                entries.add(createClaimEntry(claim));
            }

            return entries;
        }

        List<JsonObject> entries = new ArrayList<>(items.size());

        for (KciItem item : items) {

            // Find the corresponding claim
            ItemDurabilityClaim claim = null;
            for (ItemDurabilityClaim candidateClaim : dataAssignments.claimList) {
                if (candidateClaim.itemDamage == item.getItemDamage()) {
                    claim = candidateClaim;
                }
            }
            assert claim != null;
            entries.add(createClaimEntry(claim));
        }
        return entries;
    }

    private void overrideOtherItems(
            ZipOutputStream zipOutput, ItemDurabilityAssignments dataAssignments
    ) throws IOException {
        Set<VMaterial> usedOtherMaterials = EnumSet.noneOf(VMaterial.class);

        for (KciItem item : itemSet.items) {
            if (item.getItemType() == KciItemType.OTHER) {
                usedOtherMaterials.add(item.getOtherMaterial());
            }
        }

        for (VMaterial currentOtherMaterial : usedOtherMaterials) {
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

            // The interesting part...
            List<KciItem> currentItems = itemSet.items.stream().sorted(
                    Comparator.comparingInt(KciItem::getItemDamage)
            ).filter(
                    item -> item.getItemType() == KciItemType.OTHER && item.getOtherMaterial() == currentOtherMaterial
            ).collect(Collectors.toList());

            if (itemSet.getExportSettings().getMcVersion() >= VERSION1_21) {
                zipOutput.putNextEntry(new ZipEntry("assets/minecraft/items/" + modelName + ".json"));
                overrideModernItem(new PrintWriter(zipOutput), modelName, currentItems, dataAssignments);
                zipOutput.closeEntry();
                continue;
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
            for (int index = 0; index < currentItems.size(); index++) {
                KciItem item = currentItems.get(index);

                // Find the corresponding claim
                ItemDurabilityClaim claim = null;
                for (ItemDurabilityClaim candidateClaim : dataAssignments.claimList) {
                    if (candidateClaim.itemDamage == item.getItemDamage()) {
                        claim = candidateClaim;
                    }
                }

                jsonWriter.print("        { \"predicate\": { \"custom_model_data\": " + claim.itemDamage + " }, \"model\": \"" + claim.resourcePath + "\" }");
                if (index != currentItems.size() - 1) {
                    jsonWriter.print(",");
                }
                jsonWriter.println();
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
        if (itemSet.getExportSettings().getMcVersion() >= VERSION1_13) {
            jsonWriter.println("        \"layer0\": \"item/bow\"");
        } else {
            jsonWriter.println("        \"layer0\": \"items/bow_standby\"");
        }
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

        if (itemSet.getExportSettings().getMcVersion() >= VERSION1_14) {
            int index = 0;
            for (ItemDurabilityClaim claim : damageAssignments.claimList) {
                index += 1;
                jsonWriter.print("        { \"predicate\": {\"custom_model_data\": " + claim.itemDamage + "}, \"model\": \"" + claim.resourcePath + "\"}");
                List<BowTextureEntry> pullTextures = claim.pullTextures;
                if (index != damageAssignments.claimList.size() || !pullTextures.isEmpty()) jsonWriter.print(',');
                jsonWriter.println();

                int counter = 0;
                for (BowTextureEntry pullTexture : pullTextures) {
                    jsonWriter.print(
                            "        { \"predicate\": {\"custom_model_data\": " + claim.itemDamage + ", \"pulling\": 1, \"pull\": "
                                    + pullTexture.getPull() + "}, \"model\": \"" + claim.resourcePath + "_pulling_" + counter++ + "\"}"
                    );
                    if (counter != pullTextures.size() || index != damageAssignments.claimList.size()) jsonWriter.print(",");
                    jsonWriter.println();
                }
            }
        } else {
            for (ItemDurabilityClaim claim : damageAssignments.claimList) {
                double damage = (double) claim.itemDamage / KciItemType.BOW.getMaxDurability(itemSet.getExportSettings().getMcVersion());
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

            jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/bow\"},");
            jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1 }, \"model\": \"item/bow_pulling_0\"},");
            jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.65 }, \"model\": \"item/bow_pulling_1\"},");
            jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0, \"pulling\": 1, \"pull\": 0.9 }, \"model\": \"item/bow_pulling_2\"}");
        }

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
        int index = 0;
        for (ItemDurabilityClaim claim : assignments.claimList) {
            index += 1;
            jsonWriter.println("        { \"predicate\": { \"custom_model_data\": "
                    + claim.itemDamage + " }, \"model\": \"" + claim.resourcePath + "\" },");

            List<BowTextureEntry> pullTextures = claim.pullTextures;
            int counter = 0;
            for (BowTextureEntry pullTexture : pullTextures) {
                jsonWriter.println("        { \"predicate\": { \"custom_model_data\": "
                        + claim.itemDamage + ", \"pulling\": 1, \"pull\": " + pullTexture.getPull()
                        + " }, \"model\": \"" + claim.resourcePath + "_pulling_" + counter++ + "\" },");
            }

            jsonWriter.println("        { \"predicate\": { \"custom_model_data\": "
                    + claim.itemDamage + ", \"charged\": 1 }, \"model\": \"" + claim.resourcePath
                    + "_arrow\" },");
            jsonWriter.print("        { \"predicate\": { \"custom_model_data\": "
                    + claim.itemDamage + ", \"charged\": 1, \"firework\": 1 }, \"model\": \""
                    + claim.resourcePath + "_firework\" }");
            if (index != assignments.claimList.size()) jsonWriter.print(",");
            jsonWriter.println();
        }

        jsonWriter.println("    ]");
        jsonWriter.println("}");
    }

    private void overrideShield(PrintWriter jsonWriter, ItemDurabilityAssignments damageAssignments) {
        // The beginning
        jsonWriter.println("{");
        jsonWriter.println("    \"parent\": \"builtin/entity\",");
        jsonWriter.println("    \"textures\": {");
        if (itemSet.getExportSettings().getMcVersion() >= VERSION1_13) {
            jsonWriter.println("        \"particle\": \"block/dark_oak_planks\"");
        }
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
        if (itemSet.getExportSettings().getMcVersion() >= VERSION1_14) {
            int index = 0;
            for (ItemDurabilityClaim claim : damageAssignments.claimList) {
                index += 1;
                jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"custom_model_data\": "
                        + claim.itemDamage + " }, \"model\": \"" + claim.resourcePath + "\" },");
                jsonWriter.print("        { \"predicate\": { \"blocking\": 1, \"custom_model_data\": "
                        + claim.itemDamage + " }, \"model\": \"" + claim.resourcePath + "_blocking\" }");
                if (index != damageAssignments.claimList.size()) jsonWriter.print(",");
                jsonWriter.println();
            }
        } else {
            for (ItemDurabilityClaim claim : damageAssignments.claimList) {
                double damage = (double) claim.itemDamage / KciItemType.SHIELD.getMaxDurability(itemSet.getExportSettings().getMcVersion());
                jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 0, \"damage\": "
                        + damage + " }, \"model\": \"" + claim.resourcePath + "\" },");
                jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 0, \"damage\": "
                        + damage + " }, \"model\": \"" + claim.resourcePath + "_blocking\" },");
            }

            // The next ones are required to preserve the vanilla shield models
            jsonWriter.println("        { \"predicate\": { \"blocking\": 0, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield\" },");
            jsonWriter.println("        { \"predicate\": { \"blocking\": 1, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/shield_blocking\" }");
        }

        // Now finish the json
        jsonWriter.println("    ]");
        jsonWriter.println("}");
    }

    private void overrideElytra(PrintWriter jsonWriter, ItemDurabilityAssignments damageAssignments) {
        // The beginning
        jsonWriter.println("{");
        jsonWriter.println("    \"parent\": \"item/generated\",");
        jsonWriter.println("    \"textures\": {");
        if (itemSet.getExportSettings().getMcVersion() >= VERSION1_13) {
            jsonWriter.println("        \"layer0\": \"item/elytra\"");
        } else {
            jsonWriter.println("        \"layer0\": \"items/elytra\"");
        }
        jsonWriter.println("    }, \"overrides\": [");

        // The next entry is part of preserving vanilla broken elytra model
        jsonWriter.println("        { \"predicate\": { \"broken\": 1 }, \"model\": \"item/broken_elytra\" },");

        // Now the part for the custom elytra predicates...
        if (itemSet.getExportSettings().getMcVersion() >= VERSION1_14) {
            int index = 0;
            for (ItemDurabilityClaim claim : damageAssignments.claimList) {
                index += 1;
                jsonWriter.print("        { \"predicate\": { \"broken\": 0, \"custom_model_data\": "
                        + claim.itemDamage + " }, \"model\": \"" + claim.resourcePath + "\" }");
                if (index != damageAssignments.claimList.size()) jsonWriter.print(",");
                jsonWriter.println();
            }
        } else {
            for (ItemDurabilityClaim claim : damageAssignments.claimList) {
                double damage = (double) claim.itemDamage / KciItemType.ELYTRA.getMaxDurability(itemSet.getExportSettings().getMcVersion());
                jsonWriter.println("        { \"predicate\": { \"broken\": 0, \"damaged\": 0, \"damage\": "
                        + damage + " }, \"model\": \"" + claim.resourcePath + "\" },");
            }

            // The next ones are required to preserve the vanilla elytra models
            jsonWriter.println("        { \"predicate\": { \"broken\": 0, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/elytra\" },");
            jsonWriter.println("        { \"predicate\": { \"broken\": 1, \"damaged\": 1, \"damage\": 0 }, \"model\": \"item/broken_elytra\" }");
        }

        // Now finish the json
        jsonWriter.println("    ]");
        jsonWriter.println("}");
    }

    private void overrideArmor(
            PrintWriter jsonWriter, ItemDurabilityAssignments damageAssignments,
            KciItemType itemType, String textureName
    ) {
        jsonWriter.println("{");
        jsonWriter.println("    \"parent\": \"item/generated\",");
        jsonWriter.println("    \"textures\": {");
        jsonWriter.print("        \"layer0\": \"item/" + textureName + "\"");

        boolean isLeatherArmor = itemType.isLeatherArmor();
        if (isLeatherArmor) jsonWriter.print(",");
        jsonWriter.println();
        if (isLeatherArmor) jsonWriter.print("        \"layer1\": \"item/" + textureName + "_overlay\"");

        jsonWriter.println("    },");
        jsonWriter.println("    \"overrides\": [");

        int claimCounter = 0;
        for (ItemDurabilityClaim claim : damageAssignments.claimList) {
            jsonWriter.print("        { \"predicate\": { \"custom_model_data\": " + claim.itemDamage +
                    " }, \"model\": \"" + claim.resourcePath + "\" }");
            if (claim.hasCustomModel || claimCounter != damageAssignments.claimList.size()) jsonWriter.print(',');
            jsonWriter.println();

            if (!claim.hasCustomModel) {
                for (ResourcepackModelWriter.ArmorTrim trim : ARMOR_TRIMS) {
                    jsonWriter.print("        { \"predicate\": { \"custom_model_data\": " + claim.itemDamage +
                            ", \"trim_type\": " + trim.type + " }, \"model\": \"" + claim.resourcePath +
                            "_" + trim.name + "_trim\" }");
                    if (claimCounter != damageAssignments.claimList.size() - 1 || trim != ARMOR_TRIMS[ARMOR_TRIMS.length - 1]) {
                        jsonWriter.print(",");
                    }

                    jsonWriter.println();
                }
            }

            claimCounter += 1;
        }

        // End of the json file
        jsonWriter.println("    ]");
        jsonWriter.println("}");
    }

    private void overrideItem(
            PrintWriter jsonWriter, ItemDurabilityAssignments damageAssignments,
            KciItemType itemType, String modelName, String textureName
    ) {

        String parentModelName = "item/handheld";
        if (itemType == KciItemType.CARROT_STICK) parentModelName = "item/handheld_rod";

        // Begin of the json file
        jsonWriter.println("{");
        jsonWriter.println("    \"parent\": \"" + parentModelName + "\",");
        jsonWriter.println("    \"textures\": {");
        if (itemSet.getExportSettings().getMcVersion() >= VERSION1_13) {
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
            if (itemSet.getExportSettings().getMcVersion() >= VERSION1_13) {
                jsonWriter.print("        \"layer1\": \"item/" + textureName + "_overlay\"");
            } else {
                jsonWriter.print("        \"layer1\": \"items/" + textureName + "_overlay\"");
            }
        }
        jsonWriter.println("    },");
        jsonWriter.println("    \"overrides\": [");

        // Now the interesting part
        if (itemSet.getExportSettings().getMcVersion() >= VERSION1_14) {
            int index = 0;
            for (ItemDurabilityClaim claim : damageAssignments.claimList) {
                jsonWriter.print("        { \"predicate\": { \"custom_model_data\": " + claim.itemDamage + " }, \"model\": \"" + claim.resourcePath + "\" }");
                if (index != damageAssignments.claimList.size() - 1) {
                    jsonWriter.print(",");
                }
                jsonWriter.println();
                index += 1;
            }
        } else {
            for (ItemDurabilityClaim claim : damageAssignments.claimList) {
                double damage = (double) claim.itemDamage / itemType.getMaxDurability(itemSet.getExportSettings().getMcVersion());
                jsonWriter.println("        { \"predicate\": {\"damaged\": 0, \"damage\": " + damage + "}, \"model\": \"" + claim.resourcePath + "\"},");
            }
            jsonWriter.println("        { \"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/" + modelName + "\"}");
        }

        // End of the json file
        jsonWriter.println("    ]");
        jsonWriter.println("}");
    }

    private void overrideTridentInHand(PrintWriter jsonWriter, ItemDurabilityAssignments damageAssignments) throws IOException {

        // The beginning:
        ZipEntry zipEntry = new ZipEntry("assets/minecraft/models/item/trident_in_hand.json");
        zipOutput.putNextEntry(zipEntry);
        String[] begin = getMinecraftModelTridentInHandBegin();

        for (String line : begin) {
            jsonWriter.println(line);
        }

        if (itemSet.getExportSettings().getMcVersion() >= VERSION1_14) {
            int index = 0;
            for (ItemDurabilityClaim claim : damageAssignments.claimList) {
                index += 1;
                jsonWriter.println("        { \"predicate\": { \"throwing\": 0, \"custom_model_data\": "
                        + claim.itemDamage + " }, \"model\": \"" + claim.resourcePath + "_in_hand\" },");
                jsonWriter.print("        { \"predicate\": { \"throwing\": 1, \"custom_model_data\": "
                        + claim.itemDamage + " }, \"model\": \"" + claim.resourcePath + "_throwing\" }");
                if (index != damageAssignments.claimList.size()) jsonWriter.print(",");
                jsonWriter.println();
            }
        } else {
            for (ItemDurabilityClaim claim : damageAssignments.claimList) {
                double damage = (double) claim.itemDamage / KciItemType.TRIDENT.getMaxDurability(itemSet.getExportSettings().getMcVersion());
                jsonWriter.println("        { \"predicate\": { \"throwing\": 0, \"damaged\": 0, \"damage\": "
                        + damage + " }, \"model\": \"" + claim.resourcePath + "_in_hand\" },");
                jsonWriter.println("        { \"predicate\": { \"throwing\": 1, \"damaged\": 0, \"damage\": "
                        + damage + " }, \"model\": \"" + claim.resourcePath + "_throwing\" },");
            }
            jsonWriter.println("        {\"predicate\": {\"damaged\": 1, \"damage\": 0}, \"model\": \"item/trident_in_hand\"},");
            jsonWriter.println("        {\"predicate\": {\"damaged\": 1, \"damage\": 0, \"throwing\": 1}, \"model\": \"item/trident_throwing\"}");
        }

        jsonWriter.println("    ]");
        jsonWriter.println("}");

        jsonWriter.flush();
    }
}
