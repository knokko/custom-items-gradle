package nl.knokko.customitems.plugin.tasks.updater;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomToolValues;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.itemset.UpgradeReference;
import nl.knokko.customitems.nms.GeneralItemNBT;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.nms.RawAttribute;
import nl.knokko.customitems.plugin.recipe.RecipeHelper;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.plugin.set.item.CustomToolWrapper;
import nl.knokko.customitems.plugin.util.AttributeMerger;
import nl.knokko.customitems.recipe.result.UpgradeResultValues;
import nl.knokko.customitems.recipe.upgrade.UpgradeValues;
import nl.knokko.customitems.util.StringEncoder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static nl.knokko.customitems.plugin.set.item.CustomToolWrapper.wrap;
import static nl.knokko.customitems.util.Checks.isClose;

public class ItemUpgrader {

    static final String[] LAST_VANILLA_UPGRADE_KEY = {
            "KnokkosCustomUpgrades", "LastVanillaExportUpgradeTime"
    };

    private static final String[] UPGRADE_IDS_KEY = {
            "KnokkosCustomUpgrades", "UpgradeIDs"
    };

    private static final String[] ATTRIBUTE_IDS_KEY = {
            "KnokkosCustomUpgrades", "AttributeIDs"
    };

    private static final String[] ENCHANTMENTS_KEY = {
            "KnokkosCustomUpgrades", "Enchantments"
    };

    private static List<UUID> parseUUIDs(GeneralItemNBT nbt, String[] key) {
        String[] raw = nbt.getOrDefault(key, "").split(",");
        if (raw.length <= 1 && raw[0].isEmpty()) return new ArrayList<>(0);
        return Arrays.stream(raw).map(UUID::fromString).collect(Collectors.toList());
    }

    private static void saveUUIDs(GeneralItemNBT nbt, String[] key, Collection<UUID> ids) {
        List<UUID> idList = new ArrayList<>(ids);
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < idList.size(); index++) {
            builder.append(idList.get(index).toString());
            if (index != idList.size() - 1) {
                builder.append(",");
            }
        }
        nbt.set(key, builder.toString());
    }

    static List<UUID> getExistingUpgradeIDs(GeneralItemNBT nbt) {
        return parseUUIDs(nbt, UPGRADE_IDS_KEY);
    }

    static Collection<UUID> getExistingAttributeIDs(GeneralItemNBT nbt) {
        return parseUUIDs(nbt, ATTRIBUTE_IDS_KEY);
    }

    public static boolean hasStoredExistingAttributeIDs(GeneralItemNBT nbt) {
        return nbt.getOrDefault(ATTRIBUTE_IDS_KEY, null) != null;
    }

    static void setUpgradeIDs(GeneralItemNBT nbt, List<UUID> newIDs) {
        saveUUIDs(nbt, UPGRADE_IDS_KEY, newIDs);
    }

    public static void setAttributeIDs(GeneralItemNBT nbt, Collection<UUID> newIDs) {
        saveUUIDs(nbt, ATTRIBUTE_IDS_KEY, newIDs);
    }

    static Map<EnchantmentType, Integer> getEnchantmentUpgrades(GeneralItemNBT nbt) {
        String binaryString = nbt.getOrDefault(ENCHANTMENTS_KEY, null);
        if (binaryString == null) return new HashMap<>();

        BitInput input = new ByteArrayBitInput(StringEncoder.decodeTextyBytes(
                binaryString.getBytes(StandardCharsets.UTF_8)
        ));
        byte encoding = input.readByte();
        if (encoding != 1) throw new IllegalArgumentException("Unknown encoding for upgrade enchantments: " + encoding);
        int size = input.readInt();
        Map<EnchantmentType, Integer> enchantmentMap = new HashMap<>(size);
        for (int counter = 0; counter < size; counter++) {
            enchantmentMap.put(EnchantmentType.valueOf(input.readString()), input.readInt());
        }
        return enchantmentMap;
    }

    public static void setEnchantmentUpgrades(GeneralItemNBT nbt, Map<EnchantmentType, Integer> newEnchantmentUpgrades) {
        ByteArrayBitOutput output = new ByteArrayBitOutput();
        output.addByte((byte) 1);
        output.addInt(newEnchantmentUpgrades.size());
        newEnchantmentUpgrades.forEach((enchantment, level) -> {
            output.addString(enchantment.name());
            output.addInt(level);
        });
        nbt.set(ENCHANTMENTS_KEY, new String(
                StringEncoder.encodeTextyBytes(output.getBytes(), false),
                StandardCharsets.UTF_8
        ));
    }

    public static boolean hasStoredEnchantmentUpgrades(GeneralItemNBT nbt) {
        return nbt.getOrDefault(ENCHANTMENTS_KEY, null) != null;
    }

    public static List<UpgradeValues> getUpgrades(ItemStack stack, ItemSetWrapper itemSet) {
        GeneralItemNBT nbt = KciNms.instance.items.generalReadOnlyNbt(stack);
        List<UpgradeValues> upgrades = new ArrayList<>();
        for (UUID id : getExistingUpgradeIDs(nbt)) {
            Optional<UpgradeValues> upgrade = itemSet.get().getUpgrade(id);
            upgrade.ifPresent(upgrades::add);
        }
        return upgrades;
    }

    public static ItemStack addUpgrade(ItemStack original, ItemSetWrapper itemSet, UpgradeResultValues result) {
        CustomItemValues customItem = itemSet.getItem(original);

        ItemStack currentStack = original.clone();
        System.out.println("original is " + original);
        float originalDurabilityPercentage = RecipeHelper.getDurabilityPercentage(currentStack);

        RawAttribute[] originalAttributes = KciNms.instance.items.getAttributes(currentStack);
        GeneralItemNBT nbt = KciNms.instance.items.generalReadWriteNbt(currentStack);

        Collection<UUID> existingUpgradeIDs = getExistingUpgradeIDs(nbt);
        List<UUID> newUpgradeIDs = new ArrayList<>();
        if (result.shouldKeepOldUpgrades()) newUpgradeIDs.addAll(existingUpgradeIDs);
        for (UpgradeReference upgrade : result.getUpgrades()) newUpgradeIDs.add(upgrade.get().getId());

        Map<EnchantmentType, Integer> oldKciEnchantments = getEnchantmentUpgrades(nbt);
        Map<EnchantmentType, Integer> newKciEnchantments = new HashMap<>();
        if (customItem != null) ItemUpdater.addEnchantmentsToMap(newKciEnchantments, customItem.getDefaultEnchantments());
        for (UUID id : newUpgradeIDs) {
            ItemUpdater.addEnchantmentsToMap(newKciEnchantments, itemSet.get().getUpgrade(id).get().getEnchantments());
        }
        Map<EnchantmentType, Integer> enchantmentAdjustments = ItemUpdater.determineEnchantmentAdjustments(
                oldKciEnchantments, newKciEnchantments
        );

        Map<Enchantment, Integer> nonKciEnchantments = null;
        if (
                (result.shouldKeepOldEnchantments() && result.getNewType() != null)
                || (!result.shouldKeepOldEnchantments() && result.getNewType() == null)
        ) {
            nonKciEnchantments = new HashMap<>();
            System.out.println("currentStack is " + currentStack);
            for (Enchantment enchantment : Enchantment.values()) {
                int totalLevel = currentStack.getEnchantmentLevel(enchantment);
                if (enchantment.getName().equals("ASCEND")) {
                    System.out.println("ASCEND level is " + totalLevel + " on item stack " + currentStack);
                }
                int kciLevel;

                try {
                    // Warning: enchantment.getKey() is not supported in MC 1.12
                    @SuppressWarnings("deprecation")
                    EnchantmentType kciEnchantment = EnchantmentType.valueOf(enchantment.getName());
                    kciLevel = oldKciEnchantments.getOrDefault(kciEnchantment, 0);
                } catch (IllegalArgumentException cantBeKci) {
                    kciLevel = 0;
                }

                int nonKciLevel = totalLevel - kciLevel;
                if (nonKciLevel > 0) {
                    nonKciEnchantments.put(enchantment, nonKciLevel);
                }
            }
        }
        System.out.println("nonKciEnchantments are " + nonKciEnchantments);

        setEnchantmentUpgrades(nbt, newKciEnchantments);

        if (result.getNewType() != null) {
            ItemStack newStack = RecipeHelper.convertResultToItemStack(result.getNewType());

            if (result.shouldKeepOldEnchantments()) {
                assert nonKciEnchantments != null;
                //nonKciEnchantments.forEach((enchantment, level) -> BukkitEnchantments.add(newStack, enchantment, level));
                nonKciEnchantments.forEach(newStack::addUnsafeEnchantment);
            }

            UpgradeResultValues newResult = result.copy(true);
            newResult.setNewType(null);
            newResult.setRepairPercentage(result.getRepairPercentage() + originalDurabilityPercentage - 100f);
            newResult.setUpgrades(newUpgradeIDs.stream().map(
                    id -> itemSet.get().getUpgradeReference(id)).collect(Collectors.toList()
            ));

            return addUpgrade(newStack, itemSet, newResult);
        }


        Collection<UUID> existingAttributeIDs = new HashSet<>(getExistingAttributeIDs(nbt));

        List<RawAttribute> newAttributes = new ArrayList<>(originalAttributes.length);
        for (RawAttribute originalAttribute : originalAttributes) {
            if (!existingAttributeIDs.contains(originalAttribute.id)) newAttributes.add(originalAttribute);
        }
        Collections.addAll(newAttributes, AttributeMerger.merge(itemSet, null, newUpgradeIDs));
        Collection<UUID> newAttributeIDs = newAttributes.stream().map(attribute -> attribute.id).collect(Collectors.toList());

        setUpgradeIDs(nbt, newUpgradeIDs);
        setAttributeIDs(nbt, newAttributeIDs);

        if (customItem == null) {
            nbt.set(LAST_VANILLA_UPGRADE_KEY, Long.toString(itemSet.get().getExportTime()));
        }

        currentStack = KciNms.instance.items.replaceAttributes(
                nbt.backToBukkit(),
                newAttributes.toArray(new RawAttribute[0])
        );

        ItemUpdater.applyEnchantmentAdjustments(currentStack, enchantmentAdjustments);

        if (!result.shouldKeepOldEnchantments()) {
            assert nonKciEnchantments != null;
            for (Map.Entry<Enchantment, Integer> entry : nonKciEnchantments.entrySet()) {
                int newLevel = currentStack.getEnchantmentLevel(entry.getKey()) - entry.getValue();
                if (newLevel > 0) currentStack.addUnsafeEnchantment(entry.getKey(), newLevel);
                else currentStack.removeEnchantment(entry.getKey());
            }
        }

        if (!isClose(result.getRepairPercentage(), 0f)) {
            float newDurabilityPercentage = originalDurabilityPercentage + result.getRepairPercentage();
            if (newDurabilityPercentage > 100f) newDurabilityPercentage = 100f;

            if (!isClose(originalDurabilityPercentage, newDurabilityPercentage)) {
                float durabilityFractionToIncrease = 0.01f * (newDurabilityPercentage - originalDurabilityPercentage);

                if (customItem == null) {
                    ItemMeta rawMeta = currentStack.getItemMeta();
                    if (rawMeta instanceof Damageable && !rawMeta.isUnbreakable()) {
                        Damageable meta = (Damageable) rawMeta;
                        int newDamage = meta.getDamage() - Math.round(
                                currentStack.getType().getMaxDurability() * durabilityFractionToIncrease
                        );
                        if (newDamage >= currentStack.getType().getMaxDurability()) return null;
                        meta.setDamage(Math.max(newDamage, 0));
                        currentStack.setItemMeta(rawMeta);
                    }
                } else if (customItem instanceof CustomToolValues) {
                    CustomToolValues customTool = (CustomToolValues) customItem;
                    if (customTool.getMaxDurabilityNew() != null) {
                        CustomToolWrapper wrapper = wrap(customTool);
                        if (durabilityFractionToIncrease >= 0f) {
                            currentStack = wrapper.increaseDurability(
                                    currentStack,
                                    Math.round(durabilityFractionToIncrease * customTool.getMaxDurabilityNew())
                            ).stack;
                        } else {
                            currentStack = wrapper.decreaseDurability(
                                    currentStack,
                                    Math.round(-durabilityFractionToIncrease * customTool.getMaxDurabilityNew())
                            );
                        }
                    }
                }
            }
        }

        return currentStack;
    }
}
