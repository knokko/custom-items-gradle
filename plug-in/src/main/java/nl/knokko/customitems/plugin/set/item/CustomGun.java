package nl.knokko.customitems.plugin.set.item;

import nl.knokko.core.plugin.item.GeneralItemNBT;
import nl.knokko.customitems.effect.EquippedPotionEffect;
import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.Enchantment;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.gun.GunAmmo;
import nl.knokko.customitems.item.gun.IndirectGunAmmo;
import nl.knokko.customitems.item.nbt.ExtraItemNbt;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.projectile.CIProjectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;

public class CustomGun extends CustomItem {

    private static final String[] KEY_INDIRECT_AMMO = { "KnokkosCustomGun", "StoredAmmo" };

    private static String ammoPrefix() {
        return CustomItemsPlugin.getInstance().getLanguageFile().getIndirectStoredAmmo();
    }

    public final CIProjectile projectile;
    public final GunAmmo ammo;
    public final int amountPerShot;

    public CustomGun(
            CustomItemType itemType, short itemDamage, String name, String alias, String displayName,
            String[] lore, AttributeModifier[] attributes, Enchantment[] defaultEnchantments,
            boolean[] itemFlags, List<PotionEffect> playerEffects, List<PotionEffect> targetEffects,
            Collection<EquippedPotionEffect> equippedEffects, String[] commands, ReplaceCondition[] conditions,
            ReplaceCondition.ConditionOperation op, ExtraItemNbt extraNbt, float attackRange,
            CIProjectile projectile, GunAmmo ammo, int amountPerShot
    ) {
        super(
                itemType, itemDamage, name, alias, displayName, lore, attributes, defaultEnchantments, itemFlags,
                playerEffects, targetEffects, equippedEffects, commands, conditions, op, extraNbt, attackRange
        );

        this.projectile = projectile;
        this.ammo = ammo;
        this.amountPerShot = amountPerShot;
    }

    @Override
    public boolean forbidDefaultUse(ItemStack item) {
        return true;
    }

    @Override
    public int getMaxStacksize() {
        return 1;
    }

    @Override
    public ItemStack create(int amount) {
        if (ammo instanceof IndirectGunAmmo) {
            return createWithAmmo(((IndirectGunAmmo) ammo).storedAmmo);
        } else {
            return super.create(amount);
        }
    }

    public ItemStack createWithAmmo(int remainingAmmo) {
        if (!(ammo instanceof IndirectGunAmmo)) {
            throw new UnsupportedOperationException("Only guns with indirect ammo store remaining ammo in NBT");
        }

        IndirectGunAmmo indirectAmmo = (IndirectGunAmmo) ammo;

        ItemStack beforeNbt = super.create(1, createLore(indirectAmmo, remainingAmmo));
        GeneralItemNBT nbt = GeneralItemNBT.readWriteInstance(beforeNbt);
        nbt.set(KEY_INDIRECT_AMMO, remainingAmmo);

        return nbt.backToBukkit();
    }

    private List<String> createLore(IndirectGunAmmo indirectAmmo, int remainingAmmo) {
        List<String> lore = super.createLore();
        lore.add("");
        lore.add(ammoPrefix() + " " + remainingAmmo + " / " + indirectAmmo.storedAmmo);
        return lore;
    }

    /**
     * Attempts to decrement the internal ammo of the given gun item stack. If it has at least 1 stored
     * ammo left, a new item stack will be returned with 1 less stored ammo than the original item stack.
     * If the given gun item stack is out of ammo, this method returns null.
     * @param original The original gun item stack
     * @return A gun item stack with 1 less ammo, or null if original is out of ammo
     */
    public ItemStack decrementAmmo(ItemStack original) {
        if (ammo instanceof IndirectGunAmmo) {

            IndirectGunAmmo indirectAmmo = (IndirectGunAmmo) ammo;
            GeneralItemNBT nbt = GeneralItemNBT.readWriteInstance(original);

            int currentAmmo = nbt.getOrDefault(KEY_INDIRECT_AMMO, 0);
            if (currentAmmo <= 0) {

                // If this happens, the gun is either out of ammo or corrupted
                return null;
            }

            nbt.set(KEY_INDIRECT_AMMO, currentAmmo - 1);
            ItemStack decremented = nbt.backToBukkit();

            ItemMeta meta = decremented.getItemMeta();
            meta.setLore(createLore(indirectAmmo, currentAmmo - 1));
            decremented.setItemMeta(meta);

            return decremented;
        } else {
            throw new UnsupportedOperationException("Only guns with indirect ammo can decrement internal ammo");
        }
    }

    public int getCurrentAmmo(ItemStack gunStack) {
        if (ammo instanceof IndirectGunAmmo) {

            GeneralItemNBT nbt = GeneralItemNBT.readOnlyInstance(gunStack);
            return nbt.getOrDefault(KEY_INDIRECT_AMMO, 0);
        } else {
            throw new UnsupportedOperationException("Only guns with indirect ammo can have internal ammo");
        }
    }

    public ItemStack reload(ItemStack gunStack) {
        if (ammo instanceof IndirectGunAmmo) {

            IndirectGunAmmo indirectAmmo = (IndirectGunAmmo) ammo;

            GeneralItemNBT nbt = GeneralItemNBT.readWriteInstance(gunStack);
            nbt.set(KEY_INDIRECT_AMMO, indirectAmmo.storedAmmo);
            ItemStack reloaded = nbt.backToBukkit();

            ItemMeta meta = reloaded.getItemMeta();
            meta.setLore(createLore(indirectAmmo, indirectAmmo.storedAmmo));
            reloaded.setItemMeta(meta);

            return reloaded;
        } else {
            throw new UnsupportedOperationException("Only guns with indirect ammo can decrement internal ammo");
        }
    }
}
