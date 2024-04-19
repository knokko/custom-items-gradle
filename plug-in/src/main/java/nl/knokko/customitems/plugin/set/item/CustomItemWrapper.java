package nl.knokko.customitems.plugin.set.item;

import com.google.common.collect.Lists;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBTList;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import nl.knokko.customitems.effect.ChancePotionEffect;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.enchantment.VEnchantmentType;
import nl.knokko.customitems.item.enchantment.LeveledEnchantment;
import nl.knokko.customitems.nms.*;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.tasks.updater.ItemUpgrader;
import nl.knokko.customitems.plugin.util.AttributeMerger;
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
import java.util.stream.Collectors;

import static nl.knokko.customitems.MCVersions.VERSION1_14;

public abstract class CustomItemWrapper {

    public static final String NBT_KEY = "KnokkosCustomItems";

    private static final Collection<Class<? extends KciItem>> SIMPLE_WRAPPER_CLASSES = Lists.newArrayList(
            KciBlockItem.class, KciFood.class, KciGun.class, KciArrow.class,
            KciPocketContainer.class, KciWand.class, KciThrowable.class, KciSimpleItem.class
    );

    public static CustomItemWrapper wrap(KciItem item) {
        if (item instanceof KciTool) return CustomToolWrapper.wrap((KciTool) item);
        if (SIMPLE_WRAPPER_CLASSES.contains(item.getClass())) return new SimpleCustomItemWrapper(item);
        if (item.getClass() == KciGun.class) return new CustomGunWrapper((KciGun) item);
        if (item.getClass() == KciMusicDisc.class) return new CustomMusicDiscWrapper(item);
        throw new IllegalArgumentException("Unknown item class " + item.getClass());
    }

    protected final KciItem item;

    CustomItemWrapper(KciItem item) {
        this.item = item;
    }

    public void onBlockBreak(
            Player player, ItemStack item, boolean wasSolid, boolean wasFakeMainHand, int numBrokenBlocks
    ) {}

    protected boolean translateLore() {
        return !item.getTranslations().isEmpty() && !item.getTranslations().iterator().next().getLore().isEmpty();
    }

    protected List<String> createLore(){
        return item.getLore();
    }

    public List<String> createLore(Long durability) {
        // This should be overridden by CustomTool
        return createLore();
    }

    public boolean showDurabilityBar() {
        return false;
    }

    protected ItemMeta createItemMeta(ItemStack item, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.item.getDisplayName());
        meta.setLore(lore);
        if (this.item.getItemType() != KciItemType.OTHER && !showDurabilityBar()) meta.setUnbreakable(true);

        ItemFlag[] allFlags = ItemFlag.values();
        List<Boolean> ownItemFlags = this.item.getItemFlags();
        for (int index = 0; index < allFlags.length && index < ownItemFlags.size(); index++) {
            if (ownItemFlags.get(index)) {
                meta.addItemFlags(allFlags[index]);
            }
        }
        return meta;
    }

    public void translateLore(ReadWriteNBT nbt) {
        ReadWriteNBT display = nbt.getOrCreateCompound("display");
        int loreSize = this.item.getTranslations().iterator().next().getLore().size();
        if (loreSize > 0) {
            ReadWriteNBTList<String> nbtLore = display.getStringList("Lore");
            nbtLore.clear();
            for (int index = 0; index < loreSize; index++) {
                nbtLore.add("[{\"translate\": \"kci." + this.item.getName() + ".lore." + index + "\"}]");
            }
        }
    }

    public void translateName(ReadWriteNBT nbt) {
        ReadWriteNBT display = nbt.getOrCreateCompound("display");
        display.setString("Name", "{\"translate\": \"kci." + this.item.getName() + ".name\"}");
    }

    public ItemStack create(int amount, List<String> lore){
        RawAttribute[] attributeModifiers = AttributeMerger.merge(this.item, new ArrayList<>());
        ItemStack item = KciNms.instance.items.createWithAttributes(
                this.item.getVMaterial(KciNms.mcVersion).name(), amount, attributeModifiers
        );
        item.setItemMeta(createItemMeta(item, lore));
        if (KciNms.mcVersion < VERSION1_14) {
            item.setDurability(this.item.getItemDamage());
        }
        Map<VEnchantmentType, Integer> defaultEnchantmentMap = new HashMap<>();
        for (LeveledEnchantment enchantment : this.item.getDefaultEnchantments()) {
            item = BukkitEnchantments.add(item, enchantment.getType(), enchantment.getLevel());
            defaultEnchantmentMap.put(enchantment.getType(), enchantment.getLevel());
        }

        NBT.modify(item, nbt -> {

            long lastModified = CustomItemsPlugin.getInstance().getSet().get().getExportTime();

            ReadWriteNBT customNbt = nbt.getOrCreateCompound(NBT_KEY);
            customNbt.setString("Name", this.item.getName());
            customNbt.setLong("LastExportTime", lastModified);
            customNbt.setByteArray("BooleanRepresentation", this.item.getBooleanRepresentation());
            initNBT(customNbt);

            // Give it the extra nbt, if needed
            for (String extraNbt : this.item.getExtraNbt()) {
                nbt.mergeCompound(NBT.parseNBT(extraNbt));
            }

            if (!this.item.getTranslations().isEmpty()) {
                translateName(nbt);
                translateLore(nbt);
            }

            if (KciNms.mcVersion >= VERSION1_14) {
                String customModelDataKey = "CustomModelData";
                nbt.setInteger(customModelDataKey, (int) this.item.getItemDamage());
            }

            ItemUpgrader.setAttributeIDs(
                    nbt, Arrays.stream(attributeModifiers).map(attribute -> attribute.id).collect(Collectors.toList())
            );
            ItemUpgrader.setEnchantmentUpgrades(nbt, defaultEnchantmentMap);
        });

        return item;
    }

    protected void initNBT(ReadWriteNBT nbt) {}

    public ItemStack create(int amount) {
        return create(amount, createLore());
    }

    public abstract boolean forbidDefaultUse(ItemStack item);

    public boolean needsStackingHelp() {
        if (item.getItemType() == KciItemType.OTHER) {
            return item.getMaxStacksize() != KciNms.instance.items.createStack(item.getOtherMaterial().name(), 1).getMaxStackSize();
        } else {
            return item.canStack();
        }
    }

    public boolean is(ItemStack item){
        if (!ItemUtils.isEmpty(item)) {
            return NBT.get(item, nbt -> {
                ReadableNBT customNbt = nbt.getCompound(NBT_KEY);
                if (customNbt == null) return false;
                return this.item.getName().equals(customNbt.getString("Name"));
            });
        } else {
            return false;
        }
    }

    public void onEntityHit(LivingEntity attacker, ItemStack weapon, Entity target) {

        Collection<PotionEffect> pe = new ArrayList<>();
        Random rng = new Random();
        for (ChancePotionEffect effect : this.item.getOnHitPlayerEffects()) {
            if (effect.getChance().apply(rng)) {
                pe.add(new PotionEffect(
                        PotionEffectType.getByName(effect.getType().name()),
                        effect.getDuration() * 20,
                        effect.getLevel() - 1)
                );
            }
        }

        Collection<PotionEffect> te = new ArrayList<>();
        for (ChancePotionEffect effect : this.item.getOnHitTargetEffects()) {
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
