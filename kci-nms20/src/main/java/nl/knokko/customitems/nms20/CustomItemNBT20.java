package nl.knokko.customitems.nms20;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.ItemStack;
import nl.knokko.customitems.nms.BooleanRepresentation;
import nl.knokko.customitems.nms.CustomItemNBT;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;

class CustomItemNBT20 implements CustomItemNBT {

    private final ItemStack nmsStack;
    private NBTTagCompound nbt;

    private final boolean allowWrite;

    CustomItemNBT20(org.bukkit.inventory.ItemStack bukkitStack, boolean allowWrite) {
        this.nmsStack = CraftItemStack.asNMSCopy(bukkitStack);
        this.nbt = nmsStack.v();
        this.allowWrite = allowWrite;
    }

    org.bukkit.inventory.ItemStack getBukkitStack() {
        nmsStack.c(nbt);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    private NBTTagCompound getOurTag() {
        return nbt.p(KEY);
    }

    public boolean hasOurNBT() {
        return nbt != null && nbt.e(KEY);
    }

    private void assertOurNBT() throws UnsupportedOperationException {
        if (!hasOurNBT())
            throw new UnsupportedOperationException("This item stack doesn't have our nbt tag");
    }

    private NBTTagCompound getOrCreateOurNBT() {
        if (hasOurNBT()) {
            return getOurTag();
        } else {
            assertWrite();
            NBTTagCompound ourNBT = new NBTTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
            }
            nbt.a(KEY, ourNBT);
            return ourNBT;
        }
    }

    private void assertWrite() {
        if (!allowWrite)
            throw new UnsupportedOperationException("This CustomItemNBT is read-only");
    }

    public String getName() throws UnsupportedOperationException {
        assertOurNBT();

        return getOurTag().l(NAME);
    }

    public Long getLastExportTime() throws UnsupportedOperationException {
        assertOurNBT();

        if (getOurTag().e(LAST_EXPORT_TIME)) {
            return getOurTag().i(LAST_EXPORT_TIME);
        } else {
            return null;
        }
    }

    public void setLastExportTime(long newLastExportTime) throws UnsupportedOperationException {
        assertWrite();
        assertOurNBT();
        getOurTag().a(LAST_EXPORT_TIME, newLastExportTime);
    }

    public BooleanRepresentation getBooleanRepresentation() throws UnsupportedOperationException {
        assertOurNBT();

        if (getOurTag().e(BOOL_REPRESENTATION)) {
            byte[] byteRepresentation = getOurTag().m(BOOL_REPRESENTATION);
            return new BooleanRepresentation(byteRepresentation);
        } else {
            return null;
        }
    }

    public void setBooleanRepresentation(BooleanRepresentation newBoolRepresentation) throws UnsupportedOperationException {
        assertWrite();
        assertOurNBT();

        getOurTag().a(BOOL_REPRESENTATION, newBoolRepresentation.getAsBytes());
    }

    public Long getDurability() throws UnsupportedOperationException {
        assertOurNBT();

        NBTTagCompound ourTag = getOurTag();
        if (!ourTag.e(DURABILITY))
            return null;

        return getOurTag().i(DURABILITY);
    }

    public void setDurability(long newDurability) throws UnsupportedOperationException {
        assertWrite();
        assertOurNBT();
        getOurTag().a(DURABILITY, newDurability);
    }

    public void removeDurability() throws UnsupportedOperationException {
        assertWrite();
        assertOurNBT();
        getOurTag().r(DURABILITY);
    }

    public void set(String name, long lastExportTime, Long maxDurability,
                    BooleanRepresentation boolRepresentation) throws UnsupportedOperationException {
        assertWrite();
        NBTTagCompound nbt = getOrCreateOurNBT();
        nbt.a(NAME, name);
        nbt.a(LAST_EXPORT_TIME, lastExportTime);
        if (maxDurability != null) {
            nbt.a(DURABILITY, maxDurability);
        }
        nbt.a(BOOL_REPRESENTATION, boolRepresentation.getAsBytes());
    }
}
