package nl.knokko.customitems.container.slot.display;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.List;

public class SlotDisplay extends ModelValues {

    static class Encodings {
        static final byte ENCODING1 = 1;
    }

    public static SlotDisplay load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        SlotDisplay result = new SlotDisplay(false);
        byte encoding = input.readByte();

        if (encoding == Encodings.ENCODING1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("SlotDisplay", encoding);
        }

        return result;
    }

    public static SlotDisplay createQuick(
            SlotDisplayItem displayItem, String displayName, List<String> lore, int amount
    ) {
        SlotDisplay result = new SlotDisplay(true);
        result.setDisplayItem(displayItem);
        result.setDisplayName(displayName);
        result.setLore(lore);
        result.setAmount(amount);
        return result;
    }

    private SlotDisplayItem displayItem;

    private String displayName;
    private List<String> lore;
    private int amount;

    public SlotDisplay(boolean mutable) {
        super(mutable);
        this.displayItem = new SimpleVanillaDisplayItem(false);
        this.displayName = "";
        this.lore = new ArrayList<>(0);
        this.amount = 1;
    }

    public SlotDisplay(SlotDisplay toCopy, boolean mutable) {
        super(mutable);
        this.displayItem = toCopy.getDisplayItem();
        this.displayName = toCopy.getDisplayName();
        this.lore = toCopy.getLore();
        this.amount = toCopy.getAmount();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.displayName = input.readString();
        int numLoreLines = input.readInt();
        this.lore = new ArrayList<>(numLoreLines);
        for (int counter = 0; counter < numLoreLines; counter++) {
            this.lore.add(input.readString());
        }
        this.amount = input.readInt();
        this.displayItem = SlotDisplayItem.load(input, itemSet);
    }

    public void save(BitOutput output) {
        output.addByte(Encodings.ENCODING1);
        output.addString(displayName);
        output.addInt(lore.size());
        for (String loreLine : lore) {
            output.addString(loreLine);
        }
        output.addInt(amount);
        displayItem.save(output);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SlotDisplay) {
            SlotDisplay otherDisplay = (SlotDisplay) other;
            return this.displayName.equals(otherDisplay.displayName) && this.lore.equals(otherDisplay.lore)
                    && this.amount == otherDisplay.amount && this.displayItem.equals(otherDisplay.displayItem);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (this.displayName.isEmpty()) {
            return this.displayItem + " x " + amount;
        } else {
            return this.displayName + " x " + amount;
        }
    }

    @Override
    public SlotDisplay copy(boolean mutable) {
        return new SlotDisplay(this, mutable);
    }

    public SlotDisplayItem getDisplayItem() {
        return displayItem;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return new ArrayList<>(lore);
    }

    public int getAmount() {
        return amount;
    }

    public void setDisplayItem(SlotDisplayItem displayItem) {
        assertMutable();
        Checks.notNull(displayItem);
        this.displayItem = displayItem.copy(false);
    }

    public void setDisplayName(String displayName) {
        assertMutable();
        this.displayName = displayName;
    }

    public void setLore(List<String> lore) {
        assertMutable();
        Checks.nonNull(lore);
        this.lore = new ArrayList<>(lore);
    }

    public void setAmount(int amount) {
        assertMutable();
        this.amount = amount;
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (displayItem == null) throw new ProgrammingValidationException("No display item");
        Validation.scope("Display item", () -> displayItem.validate(itemSet));
        if (displayName == null) throw new ProgrammingValidationException("No display name");
        if (lore == null) throw new ProgrammingValidationException("No lore");
        for (String loreLine : lore) {
            if (loreLine == null) throw new ProgrammingValidationException("Missing a lore line");
        }
        if (amount < 1) throw new ValidationException("Amount must be positive");
        if (amount > 64) throw new ValidationException("Amount can be at most 64");

        boolean isAir = false;
        if (displayItem instanceof SimpleVanillaDisplayItem && ((SimpleVanillaDisplayItem) displayItem).getMaterial() == VMaterial.AIR) {
            isAir = true;
        }
        if (displayItem instanceof DataVanillaDisplayItem && ((DataVanillaDisplayItem) displayItem).getMaterial() == VMaterial.AIR) {
            isAir = true;
        }

        if (isAir) {
            if (!displayName.isEmpty()) throw new ValidationException("Display name must be empty when you choose Air");
            if (!lore.isEmpty()) throw new ValidationException("Lore must be empty when you choose Air");
        }
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        displayItem.validateExportVersion(version);
    }
}
