package nl.knokko.customitems.nms17;

import net.minecraft.nbt.NBTTagCompound;
import nl.knokko.customitems.nms.GeneralItemNBT;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

class GeneralItemNBT17 implements GeneralItemNBT {

    private final net.minecraft.world.item.ItemStack nmsStack;
    private NBTTagCompound tag;
    private final boolean canWrite;

    GeneralItemNBT17(net.minecraft.world.item.ItemStack nmsStack, boolean canWrite) {
        this.nmsStack = nmsStack;
        this.tag = nmsStack != null ? nmsStack.getTag() : null;
        this.canWrite = canWrite;
        if (nmsStack == null && canWrite) {
            throw new IllegalArgumentException("Can't write to nbt of a null item stack");
        }
    }

    public ItemStack backToBukkit() {
        nmsStack.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public String getOrDefault(String[] key, String defaultValue) {
        if (tag == null) return defaultValue;

        NBTTagCompound nestedTag = tag;
        for (int index = 0; index < key.length - 1; index++) {
            nestedTag = nestedTag.getCompound(key[index]);
            if (nestedTag == null || nestedTag.isEmpty()) return defaultValue;
        }

        String lastKey = key[key.length - 1];
        if (nestedTag.hasKey(lastKey)) {
            return nestedTag.getString(lastKey);
        } else {
            return defaultValue;
        }
    }

    public int getOrDefault(String[] key, int defaultValue) {
        if (tag == null) return defaultValue;

        NBTTagCompound nestedTag = tag;
        for (int index = 0; index < key.length - 1; index++) {
            nestedTag = nestedTag.getCompound(key[index]);
            if (nestedTag == null || nestedTag.isEmpty()) return defaultValue;
        }

        String lastKey = key[key.length - 1];
        if (nestedTag.hasKey(lastKey)) {
            return nestedTag.getInt(lastKey);
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
            if (nestedTag.hasKey(currentKey)) {
                nestedTag = nestedTag.getCompound(currentKey);
            } else {
                NBTTagCompound child = new NBTTagCompound();
                nestedTag.set(currentKey, child);
                nestedTag = child;
            }
        }

        return nestedTag;
    }

    public void set(String[] key, String value) {
        checkWrite();
        initCompoundTags(key).setString(key[key.length - 1], value);
    }

    public void set(String[] key, int value) {
        checkWrite();
        initCompoundTags(key).setInt(key[key.length - 1], value);
    }

    public void remove(String[] key) {
        checkWrite();
        initCompoundTags(key).remove(key[key.length - 1]);
    }
}
