package nl.knokko.customitems.nms20;

import net.minecraft.nbt.NBTTagCompound;
import nl.knokko.customitems.nms.GeneralItemNBT;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class GeneralItemNBT20 implements GeneralItemNBT {

    private final net.minecraft.world.item.ItemStack nmsStack;
    private NBTTagCompound tag;
    private final boolean canWrite;

    GeneralItemNBT20(net.minecraft.world.item.ItemStack nmsStack, boolean canWrite) {
        this.nmsStack = nmsStack;
        this.tag = nmsStack != null ? nmsStack.v() : null;
        this.canWrite = canWrite;
        if (nmsStack == null && canWrite) {
            throw new IllegalArgumentException("Can't write to nbt of a null item stack");
        }
    }

    public ItemStack backToBukkit() {
        nmsStack.c(tag);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public String getOrDefault(String[] key, String defaultValue) {
        if (tag == null) return defaultValue;

        NBTTagCompound nestedTag = tag;
        for (int index = 0; index < key.length - 1; index++) {
            nestedTag = nestedTag.p(key[index]);
            if (nestedTag == null || nestedTag.g()) return defaultValue;
        }

        String lastKey = key[key.length - 1];
        if (nestedTag.e(lastKey)) {
            return nestedTag.l(lastKey);
        } else {
            return defaultValue;
        }
    }

    public int getOrDefault(String[] key, int defaultValue) {
        if (tag == null) return defaultValue;

        NBTTagCompound nestedTag = tag;
        for (int index = 0; index < key.length - 1; index++) {
            nestedTag = nestedTag.p(key[index]);
            if (nestedTag == null || nestedTag.g()) return defaultValue;
        }

        String lastKey = key[key.length - 1];
        if (nestedTag.e(lastKey)) {
            return nestedTag.h(lastKey);
        } else {
            return defaultValue;
        }
    }

    private void checkWrite() {
        if (!canWrite) {
            throw new UnsupportedOperationException("Attempted to write on a read-only nbt view");
        }
    }

    private NBTTagCompound initCompoundTags(String[] key) {
        if (tag == null) {
            tag = new NBTTagCompound();
        }

        NBTTagCompound nestedTag = tag;
        for (int index = 0; index < key.length - 1; index++) {
            String currentKey = key[index];
            if (nestedTag.e(currentKey)) {
                nestedTag = nestedTag.p(currentKey);
            } else {
                NBTTagCompound child = new NBTTagCompound();
                nestedTag.a(currentKey, child);
                nestedTag = child;
            }
        }

        return nestedTag;
    }

    public void set(String[] key, String value) {
        checkWrite();
        initCompoundTags(key).a(key[key.length - 1], value);
    }

    public void set(String[] key, int value) {
        checkWrite();
        initCompoundTags(key).a(key[key.length - 1], value);
    }

    public void remove(String[] key) {
        checkWrite();
        initCompoundTags(key).r(key[key.length - 1]);
    }
}
