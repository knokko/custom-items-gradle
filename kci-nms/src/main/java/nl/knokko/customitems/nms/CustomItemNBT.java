package nl.knokko.customitems.nms;

public interface CustomItemNBT {

    String KEY = "KnokkosCustomItems";

    String NAME = "Name";
    String LAST_EXPORT_TIME = "LastExportTime";
    String DURABILITY = "Durability";
    String BOOL_REPRESENTATION = "BooleanRepresentation";

    /**
     * Checks if this item has the custom nbt tag for this plug-in. This method will
     * return true if and only if the item has the tag.
     */
    boolean hasOurNBT();

    /**
     * @return The name of the custom item represented by this item
     * @throws UnsupportedOperationException If this item doesn't have custom item nbt
     */
    String getName() throws UnsupportedOperationException;

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
    Long getLastExportTime() throws UnsupportedOperationException;

    /**
     * Sets the LastExportTime property of this item to the given value. This value
     * should normally be the exportTime of the .cis file, but other values can
     * be supplied as well.
     *
     * @throws UnsupportedOperationException If this item stack doesn't have custom
     * item nbt
     */
    void setLastExportTime(long newLastExportTime) throws UnsupportedOperationException;

    /**
     * Gets the {@link BooleanRepresentation} that is stored in the nbt of this item
     * stack. If it is missing, this method will return null.
     *
     * @throws UnsupportedOperationException If this item stack doesn't have custom
     * item nbt
     */
    BooleanRepresentation getBooleanRepresentation() throws UnsupportedOperationException;

    /**
     * Stores the given {@link BooleanRepresentation} in the custom item nbt of this
     * item stack.
     * @param newBoolRepresentation The new boolean representation to store
     * @throws UnsupportedOperationException If this item stack doesn't have custom
     * item nbt
     */
    void setBooleanRepresentation(BooleanRepresentation newBoolRepresentation) throws UnsupportedOperationException;

    /**
     * Gets the remaining custom durability of this item stack. If no custom
     * durability was stored in the custom item nbt, this method returns null.
     *
     * @throws UnsupportedOperationException If this item doesn't have custom item nbt
     */
    Long getDurability() throws UnsupportedOperationException;

    /**
     * Changes the remaining custom durability of this item to newDurability.
     * @param newDurability The new custom durability
     *
     * @throws UnsupportedOperationException If this custom nbt is read-only or of
     * this item doesn't have custom item nbt
     */
    void setDurability(long newDurability) throws UnsupportedOperationException;

    /**
     * Removes the custom durability of this item, making it practically unbreakable.
     * This method should only be used when its corresponding custom item turned into
     * a custom item without durability or if it became unbreakable.
     *
     * @throws UnsupportedOperationException If this custom item nbt is read-only or
     * if this item doesn't have custom item nbt
     */
    void removeDurability() throws UnsupportedOperationException;

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
    void set(
            String name, long lastExportTime, Long maxDurability, BooleanRepresentation boolRepresentation
    ) throws UnsupportedOperationException;
}
