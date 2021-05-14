package nl.knokko.customitems.editor.set.item;

import nl.knokko.customitems.block.CustomBlockView;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.util.bits.BitOutput;

import java.util.Collection;
import java.util.List;

import static nl.knokko.customitems.encoding.ItemEncoding.ENCODING_BLOCK_ITEM_9;

public class CustomBlockItem extends CustomItem {

    private CustomBlockView block;
    /** Needed for deserialization because items are loaded before blocks */
    private int blockID;

    private int stackSize;

    public CustomBlockItem(
            CustomItemType itemType, String name, String alias, String displayName, String[] lore,
            AttributeModifier[] attributes, Enchantment[] defaultEnchantments, NamedImage texture,
            boolean[] itemFlags, byte[] customModel, List<PotionEffect> playerEffects,
            List<PotionEffect> targetEffects, Collection<EquippedPotionEffect> equippedEffects,
            String[] commands, ReplaceCondition[] conditions, ReplaceCondition.ConditionOperation op,
            ExtraItemNbt extraNbt, float attackRange, CustomBlockView block, int blockID, int stackSize
    ) {
        super(
                itemType, name, alias, displayName, lore, attributes, defaultEnchantments, texture, itemFlags,
                customModel, playerEffects, targetEffects, equippedEffects, commands, conditions, op,
                extraNbt, attackRange
        );
        this.block = block;
        this.blockID = blockID;
        this.stackSize = stackSize;
    }

    @Override
    public void afterBlocksAreLoaded(ItemSet set) {
        this.block = set.getBlockByID(this.blockID);
    }

    public CustomBlockView getBlock() {
        return block;
    }

    public int getStackSize() {
        return stackSize;
    }

    public void setBlock(CustomBlockView newBlock) {
        block = newBlock;
        blockID = newBlock.getInternalID();
    }

    public void setStackSize(int newStackSize) {
        stackSize = newStackSize;
    }

    @Override
    public void export(BitOutput output) {
        output.addByte(ENCODING_BLOCK_ITEM_9);
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
        output.addInt(blockID);
        output.addInt(stackSize);
    }
}
