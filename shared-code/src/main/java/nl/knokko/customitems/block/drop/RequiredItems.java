package nl.knokko.customitems.block.drop;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

public class RequiredItems extends ModelValues {

    private static final byte ENCODING_1 = 1;

    public static RequiredItems load(
            BitInput input, SItemSet itemSet, boolean mutable
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        RequiredItems result = new RequiredItems(mutable);
        if (encoding == ENCODING_1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("RequiredItems", encoding);
        }

        return result;
    }

    private boolean enabled;
    private Collection<VanillaEntry> vanillaItems;
    private Collection<ItemReference> customItems;
    private boolean invert;

    public RequiredItems(boolean mutable) {
        super(mutable);

        this.enabled = false;
        this.vanillaItems = new ArrayList<>(0);
        this.customItems = new ArrayList<>(0);
        this.invert = false;
    }

    public RequiredItems(RequiredItems toCopy, boolean mutable) {
        super(mutable);

        this.enabled = toCopy.isEnabled();
        this.vanillaItems = toCopy.getVanillaItems();
        this.customItems = toCopy.getCustomItems();
        this.invert = toCopy.isInverted();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RequiredItems) {
            RequiredItems otherItems = (RequiredItems) other;
            return otherItems.enabled == this.enabled && otherItems.vanillaItems.equals(this.vanillaItems) &&
                    otherItems.customItems.equals(this.customItems) && otherItems.invert == this.invert;
        } else {
            return false;
        }
    }

    private void loadVanillaItems1(BitInput input) {
        int numItems = input.readInt();
        this.vanillaItems = new ArrayList<>(numItems);
        for (int counter = 0; counter < numItems; counter++) {
            this.vanillaItems.add(VanillaEntry.createQuick(
                    CIMaterial.valueOf(input.readString()),
                    input.readBoolean()
            ).copy(false));
        }
    }

    private void loadCustomItems1(BitInput input, SItemSet itemSet) {
        int numItems = input.readInt();
        this.customItems = new ArrayList<>(numItems);
        for (int counter = 0; counter < numItems; counter++) {
            this.customItems.add(itemSet.getItemReference(input.readString()));
        }
    }

    private void load1(BitInput input, SItemSet itemSet) {
        this.enabled = input.readBoolean();
        this.loadVanillaItems1(input);
        this.loadCustomItems1(input, itemSet);
        this.invert = input.readBoolean();
    }

    public void save(BitOutput output) {
        output.addByte(ENCODING_1);
        save1(output);
    }

    private void saveVanillaItems1(BitOutput output) {
        output.addInt(vanillaItems.size());
        for (VanillaEntry entry : vanillaItems) {
            output.addString(entry.material.name());
            output.addBoolean(entry.allowCustom);
        }
    }

    private void saveCustomItems1(BitOutput output) {
        output.addInt(customItems.size());
        for (ItemReference item : customItems) {
            output.addString(item.get().getName());
        }
    }

    private void save1(BitOutput output) {
        output.addBoolean(enabled);
        saveVanillaItems1(output);
        saveCustomItems1(output);
        output.addBoolean(invert);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Collection<VanillaEntry> getVanillaItems() {
        return new ArrayList<>(vanillaItems);
    }

    public Collection<ItemReference> getCustomItems() {
        return new ArrayList<>(customItems);
    }

    public boolean isInverted() {
        return invert;
    }

    @Override
    public RequiredItems copy(boolean mutable) {
        return new RequiredItems(this, mutable);
    }

    public void setEnabled(boolean newEnabled) {
        assertMutable();
        this.enabled = newEnabled;
    }

    public void setVanillaItems(Collection<VanillaEntry> newVanillaItems) {
        assertMutable();
        this.vanillaItems = Mutability.createDeepCopy(newVanillaItems, false);
    }

    public void setCustomItems(Collection<ItemReference> newCustomItems) {
        assertMutable();
        this.customItems = new ArrayList<>(newCustomItems);
    }

    public void setInverted(boolean newInvert) {
        assertMutable();
        this.invert = newInvert;
    }

    public void validateIndependent() throws ProgrammingValidationException {
        if (this.vanillaItems == null) throw new ProgrammingValidationException("The vanilla items are null");
        for (VanillaEntry vanilla : vanillaItems) {
            if (vanilla == null) throw new ProgrammingValidationException("A vanilla item entry is null");
            if (vanilla.material == null) throw new ProgrammingValidationException("A vanilla item is null");
        }

        if (this.customItems == null) throw new ProgrammingValidationException("The custom items are null");
        for (ItemReference customItem : customItems) {
            if (customItem == null) throw new ProgrammingValidationException("A custom item is null");
        }
    }

    public void validateComplete(
            SItemSet itemSet
    ) throws ProgrammingValidationException {
        validateIndependent();
        for (ItemReference ownItem : this.customItems) {
            if (!itemSet.isReferenceValid(ownItem)) {
                throw new ProgrammingValidationException("A custom item is not (or no longer) valid");
            }
        }
    }

    public static class VanillaEntry extends ModelValues {

        public static VanillaEntry createQuick(CIMaterial material, boolean allowCustom) {
            VanillaEntry result = new VanillaEntry(true);
            result.setMaterial(material);
            result.setAllowCustomItems(allowCustom);
            return result;
        }

        private CIMaterial material;
        private boolean allowCustom;

        public VanillaEntry(boolean mutable) {
            super(mutable);

            this.material = CIMaterial.STONE;
            this.allowCustom = true;
        }

        public VanillaEntry(VanillaEntry toCopy, boolean mutable) {
            super(mutable);

            this.material = toCopy.getMaterial();
            this.allowCustom = toCopy.shouldAllowCustomItems();
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof VanillaEntry) {
                VanillaEntry otherEntry = (VanillaEntry) other;
                return otherEntry.material == this.material && otherEntry.allowCustom == this.allowCustom;
            } else {
                return false;
            }
        }

        @Override
        public VanillaEntry copy(boolean mutable) {
            return new VanillaEntry(this, mutable);
        }

        public CIMaterial getMaterial() {
            return material;
        }

        public boolean shouldAllowCustomItems() {
            return allowCustom;
        }

        public void setMaterial(CIMaterial newMaterial) {
            assertMutable();
            Checks.notNull(newMaterial);
            this.material = newMaterial;
        }

        public void setAllowCustomItems(boolean allowCustomItems) {
            assertMutable();
            this.allowCustom = allowCustomItems;
        }
    }
}
