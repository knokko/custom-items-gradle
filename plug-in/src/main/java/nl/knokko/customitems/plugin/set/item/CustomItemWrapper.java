package nl.knokko.customitems.plugin.set.item;

import com.google.common.collect.Lists;
import de.tr7zw.changeme.nbtapi.NBT;
import nl.knokko.customitems.effect.ChancePotionEffectValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.EnchantmentType;
import nl.knokko.customitems.item.enchantment.EnchantmentValues;
import nl.knokko.customitems.item.nbt.NbtValueType;
import nl.knokko.customitems.nms.*;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.tasks.updater.ItemUpgrader;
import nl.knokko.customitems.plugin.util.AttributeMerger;
import nl.knokko.customitems.plugin.util.ItemUtils;
import nl.knokko.customitems.plugin.util.NbtHelper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CustomItemWrapper {

    public static CIMaterial getMaterial(CustomItemType itemType, CIMaterial otherMaterial) {
        if (itemType == CustomItemType.OTHER) return otherMaterial;

        String materialName = itemType.name();

        // This method distinguishes minecraft 1.12 and before from minecraft 1.13 and later
        // That is what we need here, because Bukkit renamed all WOOD_* tools to WOODEN_* tools
        if (KciNms.instance.useNewCommands()) {
            materialName = materialName.replace("WOOD", "WOODEN").replace("GOLD", "GOLDEN");
        } else {
            materialName = materialName.replace("SHOVEL", "SPADE");
        }

        return CIMaterial.valueOf(materialName);
    }



    private static final Collection<Class<? extends CustomItemValues>> SIMPLE_WRAPPER_CLASSES = Lists.newArrayList(
            CustomBlockItemValues.class, CustomFoodValues.class, CustomGunValues.class, CustomArrowValues.class,
            CustomPocketContainerValues.class, CustomWandValues.class, SimpleCustomItemValues.class
    );

    public static CustomItemWrapper wrap(CustomItemValues item) {
        if (item instanceof CustomToolValues) return CustomToolWrapper.wrap((CustomToolValues) item);
        if (SIMPLE_WRAPPER_CLASSES.contains(item.getClass())) return new SimpleCustomItemWrapper(item);
        if (item.getClass() == CustomGunValues.class) return new CustomGunWrapper((CustomGunValues) item);
        if (item.getClass() == CustomMusicDiscValues.class) return new CustomMusicDiscWrapper(item);
        throw new IllegalArgumentException("Unknown item class " + item.getClass());
    }

    protected final CustomItemValues item;

    CustomItemWrapper(CustomItemValues item) {
        this.item = item;
    }

    public void onBlockBreak(
            Player player, ItemStack item, boolean wasSolid, boolean wasFakeMainHand, int numBrokenBlocks
    ) {}

    protected List<String> createLore(){
        return item.getLore();
    }

    public List<String> createLore(Long durability) {
        // This should be overridden by CustomTool
        return createLore();
    }

    protected ItemMeta createItemMeta(ItemStack item, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.item.getDisplayName());
        meta.setLore(lore);
        if (this.item.getItemType() != CustomItemType.OTHER) {
            meta.setUnbreakable(true);
        }

        ItemFlag[] allFlags = ItemFlag.values();
        List<Boolean> ownItemFlags = this.item.getItemFlags();
        for (int index = 0; index < allFlags.length && index < ownItemFlags.size(); index++) {
            if (ownItemFlags.get(index)) {
                meta.addItemFlags(allFlags[index]);
            }
        }
        return meta;
    }

    public ItemStack create(int amount, List<String> lore){
        RawAttribute[] attributeModifiers = AttributeMerger.merge(this.item, new ArrayList<>());
        ItemStack item = KciNms.instance.items.createWithAttributes(
                getMaterial(this.item.getItemType(), this.item.getOtherMaterial()).name(),
                amount, attributeModifiers
        );
        item.setItemMeta(createItemMeta(item, lore));
        if (this.item.getItemType() != CustomItemType.OTHER) {
            item.setDurability(this.item.getItemDamage());
        }
        Map<EnchantmentType, Integer> defaultEnchantmentMap = new HashMap<>();
        for (EnchantmentValues enchantment : this.item.getDefaultEnchantments()) {
            item = BukkitEnchantments.add(item, enchantment.getType(), enchantment.getLevel());
            defaultEnchantmentMap.put(enchantment.getType(), enchantment.getLevel());
        }

        ItemStack[] pResult = {null};
        KciNms.instance.items.customReadWriteNbt(item, nbt -> {
            long lastModified = CustomItemsPlugin.getInstance().getSet().get().getExportTime();
            nbt.set(this.item.getName(), lastModified, null, new BooleanRepresentation(this.item.getBooleanRepresentation()));
            initNBT(nbt);
        }, result -> pResult[0] = result);

        // Give it the extra nbt, if needed
        Collection<ExtraItemNbtValues.Entry> extraNbtPairs = this.item.getExtraNbt().getEntries();

        NBT.modify(pResult[0], nbt -> {
            for (ExtraItemNbtValues.Entry extraPair : extraNbtPairs) {
                ExtraItemNbtValues.Value value = extraPair.getValue();
                if (value.type == NbtValueType.INTEGER) {
                    NbtHelper.setNested(nbt, extraPair.getKey().toArray(new String[0]), value.getIntValue());
                } else if (value.type == NbtValueType.STRING) {
                    NbtHelper.setNested(nbt, extraPair.getKey().toArray(new String[0]), value.getStringValue());
                } else {
                    throw new Error("Unknown nbt value type: " + value.type);
                }
            }

            if (this.item.getItemType() == CustomItemType.OTHER) {
                String customModelDataKey = "CustomModelData";
                nbt.setInteger(customModelDataKey, (int) this.item.getItemDamage());
            }

            ItemUpgrader.setAttributeIDs(
                    nbt, Arrays.stream(attributeModifiers).map(attribute -> attribute.id).collect(Collectors.toList())
            );
            ItemUpgrader.setEnchantmentUpgrades(nbt, defaultEnchantmentMap);
        });

        return pResult[0];
    }

    protected void initNBT(CustomItemNBT nbt) {}

    public ItemStack create(int amount) {
        return create(amount, createLore());
    }

    public abstract boolean forbidDefaultUse(ItemStack item);

    public boolean needsStackingHelp() {
        if (item.getItemType() == CustomItemType.OTHER) {
            return item.getMaxStacksize() != KciNms.instance.items.createStack(item.getOtherMaterial().name(), 1).getMaxStackSize();
        } else {
            return item.canStack();
        }
    }

    public boolean is(ItemStack item){
        if (!ItemUtils.isEmpty(item)) {
            boolean[] pResult = {false};
            KciNms.instance.items.customReadOnlyNbt(item, nbt -> {
                if (nbt.hasOurNBT()) {
                    if (nbt.getName().equals(this.item.getName())) {
                        pResult[0] = true;
                    }
                }
            });

            return pResult[0];
        } else {
            return false;
        }
    }

    public void onEntityHit(LivingEntity attacker, ItemStack weapon, Entity target) {

        Collection<PotionEffect> pe = new ArrayList<>();
        Random rng = new Random();
        for (ChancePotionEffectValues effect : this.item.getOnHitPlayerEffects()) {
            if (effect.getChance().apply(rng)) {
                pe.add(new PotionEffect(
                        PotionEffectType.getByName(effect.getType().name()),
                        effect.getDuration() * 20,
                        effect.getLevel() - 1)
                );
            }
        }

        Collection<PotionEffect> te = new ArrayList<>();
        for (ChancePotionEffectValues effect : this.item.getOnHitTargetEffects()) {
            if (effect.getChance().apply(rng)) {
                te.add(new PotionEffect(
                        PotionEffectType.getByName(effect.getType().name()),
                        effect.getDuration() * 20,
                        effect.getLevel() - 1)
                );
            }
        }

        attacker.addPotionEffects(pe);
        if (target instanceof LivingEntity) {
            LivingEntity t = (LivingEntity) target;
            t.addPotionEffects(te);
        }
    }
}
