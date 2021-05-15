package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.block.CustomBlockView;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.plugin.set.ItemSet;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class CustomBlockItem extends CustomItem {

    private CustomBlockView block;
    private final int blockID;

    private final int maxStacksize;

    public CustomBlockItem(
            CustomItemType itemType, short itemDamage, String name, String alias, String displayName,
            String[] lore, AttributeModifier[] attributes, Enchantment[] defaultEnchantments,
            boolean[] itemFlags, List<PotionEffect> playerEffects, List<PotionEffect> targetEffects,
            Collection<EquippedPotionEffect> equippedEffects, String[] commands, ReplaceCondition[] conditions,
            ReplaceCondition.ConditionOperation op, ExtraItemNbt extraNbt, float attackRange,
            int blockID, int maxStacksize
    ) {
        super(
                itemType, itemDamage, name, alias, displayName, lore, attributes, defaultEnchantments,
                itemFlags, playerEffects, targetEffects, equippedEffects, commands, conditions, op,
                extraNbt, attackRange
        );
        this.blockID = blockID;
        this.maxStacksize = maxStacksize;
    }

    public CustomBlockView getBlock() {
        return block;
    }

    @Override
    public boolean forbidDefaultUse(ItemStack item) {
        return true;
    }

    @Override
    public int getMaxStacksize() {
        return maxStacksize;
    }

    @Override
    public void afterBlocksAreLoaded(ItemSet set) {
        for (CustomBlockView block : set.getBlocks()) {
            if (block.getInternalID() == this.blockID) {
                this.block = block;
            }
        }
    }
}
