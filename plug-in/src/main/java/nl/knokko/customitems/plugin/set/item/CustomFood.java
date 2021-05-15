package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.sound.CISound;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class CustomFood extends CustomItem {

    public final int foodValue;
    public final Collection<PotionEffect> eatEffects;
    public final int eatTime;

    public final CISound eatSound;
    public final float soundVolume;
    public final float soundPitch;
    public final int soundPeriod;

    public final int maxStacksize;

    public CustomFood(
            CustomItemType itemType, short itemDamage, String name, String alias, String displayName,
            String[] lore, AttributeModifier[] attributes, Enchantment[] defaultEnchantments,
            boolean[] itemFlags, List<PotionEffect> playerEffects, List<PotionEffect> targetEffects,
            Collection<EquippedPotionEffect> equippedEffects, String[] commands, ReplaceCondition[] conditions,
            ReplaceCondition.ConditionOperation op, ExtraItemNbt extraNbt, float attackRange,
            int foodValue, Collection<PotionEffect> eatEffects, int eatTime, CISound eatSound,
            float soundVolume, float soundPitch, int soundPeriod, int maxStacksize
    ) {
        super(
                itemType, itemDamage, name, alias, displayName, lore, attributes, defaultEnchantments,
                itemFlags, playerEffects, targetEffects, equippedEffects, commands, conditions, op,
                extraNbt, attackRange
        );

        this.foodValue = foodValue;
        this.eatEffects = eatEffects;
        this.eatTime = eatTime;
        this.eatSound = eatSound;
        this.soundVolume = soundVolume;
        this.soundPitch = soundPitch;
        this.soundPeriod = soundPeriod;
        this.maxStacksize = maxStacksize;
    }

    @Override
    public boolean forbidDefaultUse(ItemStack item) {
        return true;
    }

    @Override
    public int getMaxStacksize() {
        return maxStacksize;
    }
}
