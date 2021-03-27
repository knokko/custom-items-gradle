package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.container.CustomContainer;
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

public class CustomPocketContainer extends CustomItem {

    private final String[] containerNames;
    private CustomContainer[] containers;

    public CustomPocketContainer(
            CustomItemType itemType, short itemDamage, String name, String alias, String displayName,
            String[] lore, AttributeModifier[] attributes, Enchantment[] defaultEnchantments, boolean[] itemFlags,
            List<PotionEffect> playerEffects, List<PotionEffect> targetEffects,
            Collection<EquippedPotionEffect> equippedEffects, String[] commands,
            ReplaceCondition[] conditions, ReplaceCondition.ConditionOperation op,
            ExtraItemNbt extraNbt, float attackRange, String[] containerNames) {
        super(
                itemType, itemDamage, name, alias, displayName, lore, attributes, defaultEnchantments, itemFlags,
                playerEffects, targetEffects, equippedEffects, commands, conditions, op, extraNbt, attackRange
        );
        this.containerNames = containerNames;
    }

    @Override
    public boolean forbidDefaultUse(ItemStack item) {
        return true;
    }

    @Override
    public int getMaxStacksize() {
        return 1;
    }

    public void findContainers(ItemSet set) throws IllegalArgumentException {
        containers = new CustomContainer[containerNames.length];
        for (CustomContainer setContainer : set.getContainers()) {
            for (int index = 0; index < containerNames.length; index++) {
                if (containerNames[index].equals(setContainer.getName())) {
                    containers[index] = setContainer;
                }
            }
        }

        for (int index = 0; index < containers.length; index++) {
            if (containers[index] == null) {
                throw new IllegalArgumentException("Couldn't find a container with name " + containerNames[index]);
            }
        }
    }

    public CustomContainer[] getContainers() {
        return containers;
    }
}
