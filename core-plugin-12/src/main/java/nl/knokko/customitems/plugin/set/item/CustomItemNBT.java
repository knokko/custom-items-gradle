package nl.knokko.customitems.plugin.set.item;

import java.util.function.Consumer;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;

import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public class CustomItemNBT {
	
	private static final String KEY = "KnokkosCustomItems";
	
	private static final String NAME = "Name";
	private static final String LAST_EXPORT_TIME = "LastExportTime";
	private static final String DURABILITY = "Durability";
	private static final String BOOL_REPRESENTATION = "BooleanRepresentation";
	
	/**
	 * This method grants the opportunity to both read from and write to the custom
	 * item nbt of the given Bukkit ItemStack.
	 * 
	 * This method requires that the original item stack is replaced with a new item 
	 * stack because the original item stack will NOT be modified. The third 
	 * parameter is to remind users of that.
	 * 
	 * Both lambda expression parameters will be called before this method returns.
	 * 
	 * @param original The original Bukkit ItemStack 'to modify'
	 * @param useNBT A lambda expression taking the CustomItemNBT as parameter.
	 * Reading from and writing to the custom item nbt should be done in this 
	 * lambda expression.
	 * @param getNewStack A lambda expression taking the new item stack as parameter.
	 * This lambda should be used to replace the old item stack with the new item
	 * modified item stack.
	 */
	public static void readWrite(org.bukkit.inventory.ItemStack original, 
			Consumer<CustomItemNBT> useNBT, 
			Consumer<org.bukkit.inventory.ItemStack> getNewStack) {
		
		CustomItemNBT nbt = new CustomItemNBT(original, true);
		useNBT.accept(nbt);
		getNewStack.accept(nbt.getBukkitStack());
	}
	
	/**
	 * This method grants the opportunity to read the custom item nbt of the given
	 * Bukkit ItemStack.
	 * 
	 * @param bukkitStack The item stack whose custom item nbt is to be read
	 * @param useNBT A lambda expression taking the custom item nbt of the given item
	 * stack as parameter. It will be called before this method returns.
	 */
	public static void readOnly(org.bukkit.inventory.ItemStack bukkitStack, 
			Consumer<CustomItemNBT> useNBT) {
		useNBT.accept(new CustomItemNBT(bukkitStack, false));
	}
	
	private final ItemStack nmsStack;
	private NBTTagCompound nbt;
	
	private boolean allowWrite;
	
	private CustomItemNBT(org.bukkit.inventory.ItemStack bukkitStack, boolean allowWrite) {
		this.nmsStack = CraftItemStack.asNMSCopy(bukkitStack);
		this.nbt = nmsStack.getTag();
		this.allowWrite = allowWrite;
	}
	
	private org.bukkit.inventory.ItemStack getBukkitStack() {
		nmsStack.setTag(nbt);
		return CraftItemStack.asBukkitCopy(nmsStack);
	}
	
	private NBTTagCompound getOurTag() {
		return nbt.getCompound(KEY);
	}

	/**
	 * Checks if this item has the custom nbt tag for this plug-in. This method will
	 * return true if and only if the item has the tag.
	 */
	public boolean hasOurNBT() {
		return nbt != null && nbt.hasKey(KEY);
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
			nbt.set(KEY, ourNBT);
			return ourNBT;
		}
	}
	
	private void assertWrite() {
		if (!allowWrite)
			throw new UnsupportedOperationException("This CustomItemNBT is read-only");
	}
	
	/**
	 * @return The name of the custom item represented by this item
	 * @throws UnsupportedOperationException If this item doesn't have custom item nbt
	 */
	public String getName() throws UnsupportedOperationException {
		assertOurNBT();
		
		return getOurTag().getString(NAME);
	}
	
	/**
	 * <p>
	 * Gets the <b>export time</b> of the .cis file that was active at the time this 
	 * item stack was last <b>upgraded</b> (or created if it hasn't been upgraded
	 * yet). If this is not equal to the export time of the current .cis file, this 
	 * indicates that this item stack was created from an older version of the 
	 * server item set. If the LastExportTime property is missing, this method will 
	 * return null.
	 * </p>
	 * 
	 * <p>
	 * The <b>export time</b> of a .cis file is the result of 
	 * System.currentTimeMillis() at the time that .cis file was exported by the
	 * editor.
	 * </p>
	 * 
	 * <p>
	 * Modern versions of this plug-in will <b>upgrade</b> item stacks it finds in
	 * inventories. Upgrading an item stack means checking if its NBT indicates that
	 * it is a custom item, and doing something if that is the case. For instance,
	 * it checks if the attribute modifiers of such item stacks still match the
	 * attribute modifiers defined by the corresponding custom item, and adjusts them
	 * if necessary.
	 * </p>
	 * 
	 * @throws UnsupportedOperationException If this item stack doesn't have custom
	 * item nbt
	 */
	public Long getLastExportTime() throws UnsupportedOperationException {
		assertOurNBT();
		
		if (getOurTag().hasKey(LAST_EXPORT_TIME)) {
			return getOurTag().getLong(LAST_EXPORT_TIME);
		} else {
			return null;
		}
	}
	
	/**
	 * Sets the LastExportTime property of this item to the given value. This value
	 * should normally be the exportTime of the .cis file, but other values can
	 * be supplied as well.
	 * 
	 * @throws UnsupportedOperationException If this item stack doesn't have custom
	 * item nbt
	 */
	public void setLastExportTime(long newLastExportTime) throws UnsupportedOperationException {
		assertWrite();
		assertOurNBT();
		getOurTag().setLong(LAST_EXPORT_TIME, newLastExportTime);
	}
	
	/**
	 * Gets the {@link BooleanRepresentation} that is stored in the nbt of this item
	 * stack. If it is missing, this method will return null.
	 * 
	 * @throws UnsupportedOperationException If this item stack doesn't have custom
	 * item nbt
	 */
	public BooleanRepresentation getBooleanRepresentation() throws UnsupportedOperationException {
		assertOurNBT();
		
		if (getOurTag().hasKey(BOOL_REPRESENTATION)) {
			byte[] byteRepresentation = getOurTag().getByteArray(BOOL_REPRESENTATION);
			return new BooleanRepresentation(byteRepresentation);
		} else {
			return null;
		}
	}
	
	/**
	 * Stores the given {@link BooleanRepresentation} in the custom item nbt of this
	 * item stack.
	 * @param newBoolRepresentation The new boolean representation to store
	 * @throws UnsupportedOperationException If this item stack doesn't have custom
	 * item nbt
	 */
	public void setBooleanRepresentation(BooleanRepresentation newBoolRepresentation) throws UnsupportedOperationException {
		assertWrite();
		assertOurNBT();
		
		getOurTag().setByteArray(BOOL_REPRESENTATION, newBoolRepresentation.getAsBytes());
	}
	
	/**
	 * Gets the remaining custom durability of this item stack. If no custom
	 * durability was stored in the custom item nbt, this method returns null.
	 * 
	 * @throws UnsupportedOperationException If this item doesn't have custom item nbt
	 */
	public Long getDurability() throws UnsupportedOperationException {
		assertOurNBT();
		
		NBTTagCompound ourTag = getOurTag();
		if (!ourTag.hasKey(DURABILITY))
			return null;
		
		return getOurTag().getLong(DURABILITY);
	}
	
	/**
	 * Changes the remaining custom durability of this item to newDurability.
	 * @param newDurability The new custom durability
	 * 
	 * @throws UnsupportedOperationException If this custom nbt is read-only or of
	 * this item doesn't have custom item nbt
	 */
	public void setDurability(long newDurability) throws UnsupportedOperationException {
		assertWrite();
		assertOurNBT();
		getOurTag().setLong(DURABILITY, newDurability);
	}
	
	/**
	 * Removes the custom durability of this item, making it practically unbreakable.
	 * This method should only be used when its corresponding custom item turned into
	 * a custom item without durability or if it became unbreakable.
	 * 
	 * @throws UnsupportedOperationException If this custom item nbt is read-only or
	 * if this item doesn't have custom item nbt
	 */
	public void removeDurability() throws UnsupportedOperationException {
		assertWrite();
		assertOurNBT();
		getOurTag().remove(DURABILITY);
	}
	
	/**
	 * Changes the entire custom item nbt or initializes it (if the item didn't have
	 * a custom nbt yet).
	 * 
	 * @param name The name of the custom item that is to be represented by this item
	 * @param lastExportTime The time at which the current .cis file was exported/generated
	 * @param maxDurability The maximum durability of the custom item, or null if it's
	 * an unbreakable tool or not a tool at all
	 * @param boolRepresentation The boolean representation of the custom item
	 * 
	 * @throws UnsupportedOperationException If this custom item nbt is read-only
	 */
	public void set(String name, long lastExportTime, Long maxDurability, 
			BooleanRepresentation boolRepresentation) throws UnsupportedOperationException {
		assertWrite();
		NBTTagCompound nbt = getOrCreateOurNBT();
		nbt.setString(NAME, name);
		nbt.setLong(LAST_EXPORT_TIME, lastExportTime);
		if (maxDurability != null) {
			nbt.setLong(DURABILITY, maxDurability);
		}
		nbt.setByteArray(BOOL_REPRESENTATION, boolRepresentation.getAsBytes());
	}
}
