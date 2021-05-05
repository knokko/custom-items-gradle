package nl.knokko.customitems.block.drop;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

public class RequiredItems {

    private boolean enabled;
    private Collection<VanillaEntry> vanillaItems;
    private Collection<CustomItem> customItems;
    private boolean invert;

    private final boolean mutable;

    public RequiredItems(boolean mutable) {
        this.mutable = mutable;

        this.enabled = false;
        this.vanillaItems = new ArrayList<>(0);
        this.customItems = new ArrayList<>(0);
        this.invert = false;
    }

    public RequiredItems(RequiredItems toCopy, boolean mutable) {
        this.mutable = mutable;

        this.enabled = toCopy.isEnabled();
        this.vanillaItems = toCopy.getVanillaItems();
        this.customItems = toCopy.getCustomItems();
        this.invert = toCopy.isInverted();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Collection<VanillaEntry> getVanillaItems() {
        return new ArrayList<>(vanillaItems);
    }

    public Collection<CustomItem> getCustomItems() {
        return new ArrayList<>(customItems);
    }

    public boolean isInverted() {
        return invert;
    }

    private void assertMutable() {
        if (!mutable) {
            throw new UnsupportedOperationException("This RequiredItems instance is immutable");
        }
    }

    public void setEnabled(boolean newEnabled) {
        assertMutable();
        this.enabled = newEnabled;
    }

    public void setVanillaItems(Collection<VanillaEntry> newVanillaItems) {
        assertMutable();
        this.vanillaItems = new ArrayList<>(newVanillaItems);
    }

    public void setCustomItems(Collection<CustomItem> newCustomItems) {
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
        for (CustomItem customItem : customItems) {
            if (customItem == null) throw new ProgrammingValidationException("A custom item is null");
        }
    }

    public void validateComplete(
            Iterable<? extends CustomItem> customItems
    ) throws ProgrammingValidationException {
        validateIndependent();
        for (CustomItem ownItem : this.customItems) {

            boolean containsIt = false;
            for (CustomItem existingItem : customItems) {
                if (ownItem == existingItem) {
                    containsIt = true;
                    break;
                }
            }

            if (!containsIt) throw new ProgrammingValidationException("A custom item is not registered");
        }
    }

    public static class VanillaEntry {

        public final CIMaterial material;
        public final boolean allowCustom;

        public VanillaEntry(CIMaterial material, boolean allowCustom) {
            this.material = material;
            this.allowCustom = allowCustom;
        }
    }
}
