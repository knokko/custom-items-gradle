package nl.knokko.customitems.plugin.set.item;

import nl.knokko.customitems.item.CustomGunValues;
import nl.knokko.customitems.item.gun.IndirectGunAmmoValues;
import nl.knokko.customitems.nms.GeneralItemNBT;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustomGunWrapper extends CustomItemWrapper {

    private static final String[] KEY_INDIRECT_AMMO = { "KnokkosCustomGun", "StoredAmmo" };

    private static String ammoPrefix() {
        return CustomItemsPlugin.getInstance().getLanguageFile().getIndirectStoredAmmo();
    }

    private final CustomGunValues gun;

    public CustomGunWrapper(CustomGunValues item) {
        super(item);
        this.gun = item;
    }

    @Override
    public boolean forbidDefaultUse(ItemStack item) {
        return true;
    }

    @Override
    public ItemStack create(int amount) {
        if (gun.getAmmo() instanceof IndirectGunAmmoValues) {
            return createWithAmmo(((IndirectGunAmmoValues) gun.getAmmo()).getStoredAmmo());
        } else {
            return super.create(amount);
        }
    }

    public ItemStack createWithAmmo(int remainingAmmo) {
        if (!(gun.getAmmo() instanceof IndirectGunAmmoValues)) {
            throw new UnsupportedOperationException("Only guns with indirect ammo store remaining ammo in NBT");
        }

        IndirectGunAmmoValues indirectAmmo = (IndirectGunAmmoValues) gun.getAmmo();

        ItemStack beforeNbt = super.create(1, createLore(indirectAmmo, remainingAmmo));
        GeneralItemNBT nbt = KciNms.instance.items.generalReadWriteNbt(beforeNbt);
        nbt.set(KEY_INDIRECT_AMMO, remainingAmmo);

        return nbt.backToBukkit();
    }

    private List<String> createLore(IndirectGunAmmoValues indirectAmmo, int remainingAmmo) {
        List<String> lore = super.createLore();
        lore.add("");
        lore.add(ammoPrefix() + " " + remainingAmmo + " / " + indirectAmmo.getStoredAmmo());
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
        if (gun.getAmmo() instanceof IndirectGunAmmoValues) {

            IndirectGunAmmoValues indirectAmmo = (IndirectGunAmmoValues) gun.getAmmo();
            GeneralItemNBT nbt = KciNms.instance.items.generalReadWriteNbt(original);

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
        if (gun.getAmmo() instanceof IndirectGunAmmoValues) {

            GeneralItemNBT nbt = KciNms.instance.items.generalReadOnlyNbt(gunStack);
            return nbt.getOrDefault(KEY_INDIRECT_AMMO, 0);
        } else {
            throw new UnsupportedOperationException("Only guns with indirect ammo can have internal ammo");
        }
    }

    public ItemStack reload(ItemStack gunStack) {
        if (gun.getAmmo() instanceof IndirectGunAmmoValues) {

            IndirectGunAmmoValues indirectAmmo = (IndirectGunAmmoValues) gun.getAmmo();

            GeneralItemNBT nbt = KciNms.instance.items.generalReadWriteNbt(gunStack);
            nbt.set(KEY_INDIRECT_AMMO, indirectAmmo.getStoredAmmo());
            ItemStack reloaded = nbt.backToBukkit();

            ItemMeta meta = reloaded.getItemMeta();
            meta.setLore(createLore(indirectAmmo, indirectAmmo.getStoredAmmo()));
            reloaded.setItemMeta(meta);

            return reloaded;
        } else {
            throw new UnsupportedOperationException("Only guns with indirect ammo can decrement internal ammo");
        }
    }
}
