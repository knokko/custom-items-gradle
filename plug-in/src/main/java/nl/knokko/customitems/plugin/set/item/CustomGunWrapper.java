package nl.knokko.customitems.plugin.set.item;

import de.tr7zw.changeme.nbtapi.NBT;
import nl.knokko.customitems.item.KciGun;
import nl.knokko.customitems.item.gun.IndirectGunAmmo;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.util.NbtHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustomGunWrapper extends CustomItemWrapper {

    private static final String[] KEY_INDIRECT_AMMO = { "KnokkosCustomGun", "StoredAmmo" };

    private static String ammoPrefix() {
        return CustomItemsPlugin.getInstance().getLanguageFile().getIndirectStoredAmmo();
    }

    private final KciGun gun;

    public CustomGunWrapper(KciGun item) {
        super(item);
        this.gun = item;
    }

    @Override
    public boolean forbidDefaultUse(ItemStack item) {
        return true;
    }

    @Override
    public ItemStack create(int amount) {
        if (gun.getAmmo() instanceof IndirectGunAmmo) {
            return createWithAmmo(((IndirectGunAmmo) gun.getAmmo()).getStoredAmmo());
        } else {
            return super.create(amount);
        }
    }

    public ItemStack createWithAmmo(int remainingAmmo) {
        if (!(gun.getAmmo() instanceof IndirectGunAmmo)) {
            throw new UnsupportedOperationException("Only guns with indirect ammo store remaining ammo in NBT");
        }

        IndirectGunAmmo indirectAmmo = (IndirectGunAmmo) gun.getAmmo();

        ItemStack result = super.create(1, createLore(indirectAmmo, remainingAmmo));
        NBT.modify(result, nbt -> {
            NbtHelper.setNested(nbt, KEY_INDIRECT_AMMO, remainingAmmo);
        });

        return result;
    }

    private List<String> createLore(IndirectGunAmmo indirectAmmo, int remainingAmmo) {
        List<String> lore = super.createLore();
        lore.add(ammoPrefix() + " " + remainingAmmo + " / " + indirectAmmo.getStoredAmmo());
        return lore;
    }

    /**
     * Attempts to decrement the internal ammo of the given gun item stack. If it has at least 1 stored
     * ammo left, it will be decremented, and the same item stack will be returned
     * If the given gun item stack is out of ammo, this method returns null.
     * @return The item stack with 1 less ammo, or null if original is out of ammo
     */
    public ItemStack decrementAmmo(ItemStack gunStack) {
        if (gun.getAmmo() instanceof IndirectGunAmmo) {

            IndirectGunAmmo indirectAmmo = (IndirectGunAmmo) gun.getAmmo();
            int newAmmo = NBT.modify(gunStack, nbt -> {
                int currentAmmo = NbtHelper.getNested(nbt, KEY_INDIRECT_AMMO, 0);
                if (currentAmmo <= 0) {

                    // If this happens, the gun is either out of ammo or corrupted
                    return -1;
                }

                NbtHelper.setNested(nbt, KEY_INDIRECT_AMMO, currentAmmo - 1);
                return currentAmmo - 1;
            });
            if (newAmmo == -1) return null;

            if (!translateLore()) {
                ItemMeta meta = gunStack.getItemMeta();
                meta.setLore(createLore(indirectAmmo, newAmmo));
                gunStack.setItemMeta(meta);
            }

            return gunStack;
        } else {
            throw new UnsupportedOperationException("Only guns with indirect ammo can decrement internal ammo");
        }
    }

    public int getCurrentAmmo(ItemStack gunStack) {
        if (gun.getAmmo() instanceof IndirectGunAmmo) {
            return NBT.get(gunStack, nbt -> { return NbtHelper.getNested(nbt, KEY_INDIRECT_AMMO, 0); } );
        } else {
            throw new UnsupportedOperationException("Only guns with indirect ammo can have internal ammo");
        }
    }

    public void reload(ItemStack gunStack) {
        if (gun.getAmmo() instanceof IndirectGunAmmo) {

            IndirectGunAmmo indirectAmmo = (IndirectGunAmmo) gun.getAmmo();

            NBT.modify(gunStack, nbt -> {
               NbtHelper.setNested(nbt, KEY_INDIRECT_AMMO, indirectAmmo.getStoredAmmo());
            });

            if (!translateLore()) {
                ItemMeta meta = gunStack.getItemMeta();
                meta.setLore(createLore(indirectAmmo, indirectAmmo.getStoredAmmo()));
                gunStack.setItemMeta(meta);
            }
        } else {
            throw new UnsupportedOperationException("Only guns with indirect ammo can decrement internal ammo");
        }
    }
}
