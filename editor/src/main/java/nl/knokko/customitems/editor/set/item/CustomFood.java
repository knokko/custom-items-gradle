package nl.knokko.customitems.editor.set.item;

import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.util.bits.BitOutput;

import java.util.Collection;
import java.util.List;

public class CustomFood extends CustomItem {

    public int foodValue;
    public Collection<PotionEffect> eatEffects;
    public int eatTime;

    public CISound eatSound;
    public float soundVolume;
    public float soundPitch;
    public int soundPeriod;

    public int maxStacksize;

    public CustomFood(
            CustomItemType itemType, String name, String alias, String displayName, String[] lore,
            AttributeModifier[] attributes, Enchantment[] defaultEnchantments, NamedImage texture,
            boolean[] itemFlags, byte[] customModel, List<PotionEffect> playerEffects,
            List<PotionEffect> targetEffects, Collection<EquippedPotionEffect> equippedEffects,
            String[] commands, ReplaceCondition[] conditions, ReplaceCondition.ConditionOperation op,
            ExtraItemNbt extraNbt, float attackRange, int foodValue, Collection<PotionEffect> eatEffects,
            int eatTime, CISound eatSound, float soundVolume, float soundPitch, int soundPeriod,
            int maxStacksize
    ) {
        super(
                itemType, name, alias, displayName, lore, attributes, defaultEnchantments, texture, itemFlags,
                customModel, playerEffects, targetEffects, equippedEffects, commands, conditions, op, extraNbt,
                attackRange
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
    public void export(BitOutput output) {
        output.addByte(ItemEncoding.ENCODING_FOOD_9);
        output.addJavaString(itemType.name());
        output.addShort(itemDamage);
        output.addJavaString(name);
        output.addString(alias);
        output.addJavaString(displayName);
        output.addByte((byte) lore.length);
        for(String line : lore)
            output.addJavaString(line);
        output.addByte((byte) attributes.length);
        for (AttributeModifier attribute : attributes) {
            output.addJavaString(attribute.getAttribute().name());
            output.addJavaString(attribute.getSlot().name());
            output.addNumber(attribute.getOperation().ordinal(), (byte) 2, false);
            output.addDouble(attribute.getValue());
        }
        output.addByte((byte) defaultEnchantments.length);
        for (Enchantment enchantment : defaultEnchantments) {
            output.addString(enchantment.getType().name());
            output.addInt(enchantment.getLevel());
        }
        output.addBooleans(itemFlags);
        output.addByte((byte) playerEffects.size());
        for (PotionEffect effect : playerEffects) {
            output.addJavaString(effect.getEffect().name());
            output.addInt(effect.getDuration());
            output.addInt(effect.getLevel());
        }
        output.addByte((byte) targetEffects.size());
        for (PotionEffect effect : targetEffects) {
            output.addJavaString(effect.getEffect().name());
            output.addInt(effect.getDuration());
            output.addInt(effect.getLevel());
        }
        writeEquippedEffects(output);
        output.addByte((byte) commands.length);
        for (String command : commands) {
            output.addJavaString(command);
        }
        output.addByte((byte) conditions.length);
        for (ReplaceCondition condition : conditions) {
            output.addJavaString(condition.getCondition().name());
            output.addJavaString(condition.getItemName());
            output.addJavaString(condition.getOp().name());
            output.addInt(condition.getValue());
            output.addJavaString(condition.getReplacingItemName());
        }
        output.addJavaString(op.name());
        extraNbt.save(output);
        output.addFloat(attackRange);
        output.addInt(foodValue);
        output.addInt(eatEffects.size());
        for (PotionEffect eatEffect : eatEffects) {
            eatEffect.save1(output);
        }
        output.addInt(eatTime);
        output.addString(eatSound.name());
        output.addFloat(soundVolume);
        output.addFloat(soundPitch);
        output.addInt(soundPeriod);
        output.addInt(maxStacksize);
    }
}
