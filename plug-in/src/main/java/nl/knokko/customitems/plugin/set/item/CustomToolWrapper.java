package nl.knokko.customitems.plugin.set.item;

import com.google.common.collect.Lists;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.multisupport.dualwield.DualWieldSupport;
import nl.knokko.customitems.plugin.tasks.updater.LoreUpdater;
import nl.knokko.customitems.plugin.util.SoundPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static nl.knokko.customitems.item.CustomItemValues.UNBREAKABLE_TOOL_DURABILITY;

public class CustomToolWrapper extends CustomItemWrapper {

    public static String prefix() {
        return CustomItemsPlugin.getInstance().getLanguageFile().getDurabilityPrefix();
    }

    protected static String createDurabilityLine(long current, long max) {
        return LoreUpdater.createDurabilityLine(prefix(), current, max);
    }

    private static final Collection<Class<?>> BASIC_TOOL_WRAPPERS = Lists.newArrayList(
            CustomBowValues.class, CustomCrossbowValues.class, CustomHoeValues.class,
            CustomShieldValues.class, CustomToolValues.class, CustomTridentValues.class,
            CustomElytraValues.class
    );

    public static CustomToolWrapper wrap(CustomToolValues tool) {
        if (tool.getClass() == CustomArmorValues.class) return new CustomArmorWrapper((CustomArmorValues) tool);
        if (tool.getClass() == CustomHelmet3dValues.class) return new CustomHelmet3dWrapper((CustomArmorValues) tool);
        if (tool.getClass() == CustomShearsValues.class) return new CustomShearsWrapper(tool);
        if (BASIC_TOOL_WRAPPERS.contains(tool.getClass())) return new CustomToolWrapper(tool);
        throw new IllegalArgumentException("Unknown custom tool class " + tool.getClass());
    }

    protected final CustomToolValues tool;

    CustomToolWrapper(CustomToolValues item) {
        super(item);
        this.tool = item;
    }

    @Override
    public boolean forbidDefaultUse(ItemStack item) {
        return false;
    }

    @Override
    protected List<String> createLore(){
        return createLore(this.tool.getMaxDurabilityNew());
    }

    @Override
    public List<String> createLore(Long currentDurability){
        List<String> rawLore = this.tool.getLore();
        List<String> itemLore = new ArrayList<>(rawLore.size() + 2);
        if (this.tool.getMaxDurabilityNew() != null) {
            if (currentDurability == null) {
                currentDurability = this.tool.getMaxDurabilityNew();
            }
            itemLore.add(createDurabilityLine(currentDurability, this.tool.getMaxDurabilityNew()));
        }
        itemLore.addAll(rawLore);

        return itemLore;
    }

    public ItemStack create(int amount, long durability) {
        if (amount != 1) throw new IllegalArgumentException("Amount must be 1, but is " + amount);
        ItemStack result = super.create(amount, createLore(durability));

        if (this.tool.getMaxDurabilityNew() != null) {
            NBT.modify(result, nbt -> {
                nbt.getOrCreateCompound(NBT_KEY).setLong("Durability", durability);
            });
        }

        return result;
    }

    @Override
    public void onBlockBreak(
            Player player, ItemStack tool, boolean wasSolid, boolean wasFakeMainHand, int numBrokenBlocks
    ) {
        if (wasSolid && this.tool.getBlockBreakDurabilityLoss() != 0) {

            int durabilityFactor = this.tool.getMultiBlockBreak().shouldStackDurabilityCost() ? numBrokenBlocks : 1;
            ItemStack decreased = decreaseDurability(tool, this.tool.getBlockBreakDurabilityLoss() * durabilityFactor);
            if (decreased == null) {
                for (ReplacementConditionValues cond : this.tool.getReplacementConditions()) {
                    if (cond.getCondition() == ReplacementConditionValues.ReplacementCondition.ISBROKEN) {
                        ItemStack replace = wrap(cond.getReplaceItem()).create(1);
                        player.getInventory().addItem(replace);
                    }
                }
                SoundPlayer.playBreakSound(player);
            }
            if (decreased != tool) {
                if (wasFakeMainHand) {
                    player.getInventory().setItemInOffHand(DualWieldSupport.purge(decreased));
                } else {
                    player.getInventory().setItemInMainHand(decreased);
                }
            }
        }
    }

    @Override
    public void onEntityHit(LivingEntity attacker, ItemStack tool, Entity target) {
        super.onEntityHit(attacker, tool, target);
        if (this.tool.getEntityHitDurabilityLoss() != 0) {
            ItemStack decreased = decreaseDurability(tool, this.tool.getEntityHitDurabilityLoss());
            if (decreased == null && attacker instanceof Player) {
                SoundPlayer.playBreakSound((Player) attacker);
            }
            if (decreased != tool) {
                // This method can only be called when the attacker has equipment
                Objects.requireNonNull(attacker.getEquipment()).setItemInMainHand(decreased);
            }
        }
    }

    /**
     * @param stack The (custom) item stack to decrease the durability of
     * @return The same item stack if nothing changed, or a new ItemStack that should
     * replace the old one (null if the stack should break).
     *
     * It is the task of the caller to ensure that the old one really gets replaced!
     */
    public ItemStack decreaseDurability(ItemStack stack, int damage) {
        if (this.tool.getMaxDurabilityNew() == null || !stack.hasItemMeta()) {
            return stack;
        }

        if (Math.random() <= 1.0 / (1 + stack.getEnchantmentLevel(Enchantment.DURABILITY))) {

            Long[] pOldDurability = {null};
            Long[] pNewDurability = {null};

            NBT.modify(stack, nbt -> {
                ReadWriteNBT customNbt = nbt.getOrCreateCompound(NBT_KEY);
                if (customNbt.hasTag("Durability", NBTType.NBTTagLong)) pOldDurability[0] = customNbt.getLong("Durability");
                if (pOldDurability[0] != null) {
                    if (pOldDurability[0] > damage) {
                        pNewDurability[0] = pOldDurability[0] - damage;
                        customNbt.setLong("Durability", pNewDurability[0]);
                    } else {

                        // If this block is reached, the item will break
                        pNewDurability[0] = 0L;
                    }
                } else {
                    /*
                     * If this happens, the item stack doesn't have durability
                     * stored in its lore, even though it should be breakable.
                     * This probably means that the custom item used to be
                     * unbreakable in the previous version of the item set, but
                     * became breakable in the current version of the item set.
                     * We have a repeating task to frequently check for these
                     * problems, so we will just do nothing and wait for the
                     * repeating task to fix this.
                     */
                }
            });

            if (pNewDurability[0] != null && !translateLore()) {
                long newDurability = pNewDurability[0];
                if (newDurability == 0) {
                    return null;
                }
                ItemMeta meta = stack.getItemMeta();
                assert meta != null;
                if (LoreUpdater.updateDurability(
                        meta, pOldDurability[0], newDurability, tool.getMaxDurabilityNew(), tool.getMaxDurabilityNew(), prefix())
                ) {
                    meta.setLore(createLore(newDurability));
                }
                stack.setItemMeta(meta);
            }
        }
        return stack;
    }

    public static class IncreaseDurabilityResult {

        public final ItemStack stack;
        public final long increasedAmount;

        IncreaseDurabilityResult(ItemStack stack, long increasedAmount) {
            this.stack = stack;
            this.increasedAmount = increasedAmount;
        }
    }

    public IncreaseDurabilityResult increaseDurability(ItemStack stack, long amount) {
        if (this.tool.getMaxDurabilityNew() == null || !stack.hasItemMeta()) {
            return new IncreaseDurabilityResult(stack, 0);
        }

        long[] pIncreasedAmount = {0L};
        Long[] pOldDurability = {null};
        long[] pNewDurability = {-1L};

        NBT.modify(stack, nbt -> {
            ReadWriteNBT customNbt = nbt.getOrCreateCompound(NBT_KEY);
            if (customNbt.hasTag("Durability", NBTType.NBTTagLong)) {
                pOldDurability[0] = customNbt.getLong("Durability");
            }
            if (pOldDurability[0] != null) {
                long newDurability;
                if (pOldDurability[0] + amount <= this.tool.getMaxDurabilityNew()) {
                    newDurability = pOldDurability[0] + amount;
                } else {
                    newDurability = this.tool.getMaxDurabilityNew();
                }
                pIncreasedAmount[0] = newDurability - pOldDurability[0];
                pNewDurability[0] = newDurability;
                customNbt.setLong("Durability", newDurability);
            } else {
                /*
                 * If this happens, the item stack doesn't have durability
                 * stored in its lore, even though it should be breakable.
                 * This probably means that the custom item used to be
                 * unbreakable in the previous version of the item set, but
                 * became breakable in the current version of the item set.
                 * We have a repeating task to frequently check for these
                 * problems, so we will just do nothing and wait for the
                 * repeating task to fix this.
                 */
            }
        });
        long increasedAmount = pIncreasedAmount[0];

        if (increasedAmount > 0 && !translateLore()) {
            long newDurability = pNewDurability[0];
            ItemMeta meta = stack.getItemMeta();
            assert meta != null;
            if (LoreUpdater.updateDurability(
                    meta, pOldDurability[0], newDurability, tool.getMaxDurabilityNew(), tool.getMaxDurabilityNew(), prefix()
            )) {
                meta.setLore(createLore(newDurability));
            }
            stack.setItemMeta(meta);
        }

        return new IncreaseDurabilityResult(stack, increasedAmount);
    }

    public long getDurability(ItemStack stack) {
        return NBT.get(stack, nbt -> {
            ReadableNBT customNbt = nbt.getCompound(NBT_KEY);
            if (customNbt == null || !customNbt.hasTag("Durability", NBTType.NBTTagLong)) return UNBREAKABLE_TOOL_DURABILITY;
            return customNbt.getLong("Durability");
        });
    }

    @Override
    protected void initNBT(ReadWriteNBT nbt) {
        if (this.tool.getMaxDurabilityNew() != null) {
            nbt.setLong("Durability", this.tool.getMaxDurabilityNew());
        }
    }
}
