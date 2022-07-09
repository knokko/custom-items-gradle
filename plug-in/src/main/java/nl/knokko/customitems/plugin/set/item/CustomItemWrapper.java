package nl.knokko.customitems.plugin.set.item;

import com.google.common.collect.Lists;
import nl.knokko.core.plugin.CorePlugin;
import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.core.plugin.item.ItemHelper;
import nl.knokko.core.plugin.item.attributes.ItemAttributes;
import nl.knokko.customitems.effect.ChancePotionEffectValues;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.EnchantmentValues;
import nl.knokko.customitems.item.nbt.NbtValueType;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public abstract class CustomItemWrapper {

    public static CIMaterial getMaterial(CustomItemType itemType, CIMaterial otherMaterial) {
        if (itemType == CustomItemType.OTHER) return otherMaterial;

        String materialName = itemType.name();

        // This method distinguishes minecraft 1.12 and before from minecraft 1.13 and later
        // That is what we need here, because Bukkit renamed all WOOD_* tools to WOODEN_* tools
        if (CorePlugin.useNewCommands()) {
            materialName = materialName.replace("WOOD", "WOODEN").replace("GOLD", "GOLDEN");
        } else {
            materialName = materialName.replace("SHOVEL", "SPADE");
        }

        return CIMaterial.valueOf(materialName);
    }

    public static ItemAttributes.Single convertAttributeModifier(AttributeModifierValues modifier) {
        return new ItemAttributes.Single(
                modifier.getAttribute().getName(),
                modifier.getSlot().getSlot(),
                modifier.getOperation().getOperation(),
                modifier.getValue()
        );
    }

    public static ItemAttributes.Single[] convertAttributeModifiers(Collection<AttributeModifierValues> attributeModifiers) {
        ItemAttributes.Single[] result = new ItemAttributes.Single[attributeModifiers.size()];
        int index = 0;
        for (AttributeModifierValues modifier : attributeModifiers) {
            result[index] = convertAttributeModifier(modifier);
            index++;
        }
        return result;
    }

    private static final Collection<Class<? extends CustomItemValues>> SIMPLE_WRAPPER_CLASSES = Lists.newArrayList(
            CustomBlockItemValues.class, CustomFoodValues.class, CustomGunValues.class,
            CustomPocketContainerValues.class, CustomWandValues.class, SimpleCustomItemValues.class
    );

    public static CustomItemWrapper wrap(CustomItemValues item) {
        if (item instanceof CustomToolValues) return CustomToolWrapper.wrap((CustomToolValues) item);
        if (SIMPLE_WRAPPER_CLASSES.contains(item.getClass())) return new SimpleCustomItemWrapper(item);
        if (item.getClass() == CustomGunValues.class) return new CustomGunWrapper((CustomGunValues) item);
        throw new IllegalArgumentException("Unknown item class " + item.getClass());
    }

    private final CustomItemValues item;

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
        ItemStack item = ItemAttributes.createWithAttributes(
                getMaterial(this.item.getItemType(), this.item.getOtherMaterial()).name(),
                amount, convertAttributeModifiers(this.item.getAttributeModifiers())
        );
        item.setItemMeta(createItemMeta(item, lore));
        if (this.item.getItemType() != CustomItemType.OTHER) {
            item.setDurability(this.item.getItemDamage());
        }
        for (EnchantmentValues enchantment : this.item.getDefaultEnchantments()) {
            BukkitEnchantments.add(item, enchantment.getType(), enchantment.getLevel());
        }

        ItemStack[] pResult = {null};
        CustomItemNBT.readWrite(item, nbt -> {
            long lastModified = CustomItemsPlugin.getInstance().getSet().get().getExportTime();
            nbt.set(this.item.getName(), lastModified, null, new BooleanRepresentation(this.item.getBooleanRepresentation()));
            initNBT(nbt);
        }, result -> pResult[0] = result);

        // Give it the extra nbt, if needed
        Collection<ExtraItemNbtValues.Entry> extraNbtPairs = this.item.getExtraNbt().getEntries();
        if (!extraNbtPairs.isEmpty() || this.item.getItemType() == CustomItemType.OTHER) {
            GeneralItemNBT nbt = GeneralItemNBT.readWriteInstance(pResult[0]);
            for (ExtraItemNbtValues.Entry extraPair : extraNbtPairs) {
                ExtraItemNbtValues.Value value = extraPair.getValue();
                if (value.type == NbtValueType.INTEGER) {
                    nbt.set(extraPair.getKey().toArray(new String[0]), value.getIntValue());
                } else if (value.type == NbtValueType.STRING) {
                    nbt.set(extraPair.getKey().toArray(new String[0]), value.getStringValue());
                } else {
                    throw new Error("Unknown nbt value type: " + value.type);
                }
            }

            if (this.item.getItemType() == CustomItemType.OTHER) {
                String[] customModelDataKey = { "CustomModelData" };
                nbt.set(customModelDataKey, this.item.getItemDamage());
            }

            pResult[0] = nbt.backToBukkit();
        }

        return pResult[0];
    }

    protected void initNBT(CustomItemNBT nbt) {}

    public ItemStack create(int amount) {
        return create(amount, createLore());
    }

    public abstract boolean forbidDefaultUse(ItemStack item);

    public boolean needsStackingHelp() {
        if (item.getItemType() == CustomItemType.OTHER) {
            return item.getMaxStacksize() != ItemHelper.createStack(item.getOtherMaterial().name(), 1).getMaxStackSize();
        } else {
            return item.canStack();
        }
    }

    public boolean is(ItemStack item){
        if (!ItemUtils.isEmpty(item)) {
            boolean[] pResult = {false};
            CustomItemNBT.readOnly(item, nbt -> {
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
