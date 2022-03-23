package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Collection;

public class ManualOutputSlotValues extends ContainerSlotValues {

    static ManualOutputSlotValues loadManual(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ManualOutputSlot", encoding);

        ManualOutputSlotValues result = new ManualOutputSlotValues(false);
        result.name = input.readString();
        if (input.readBoolean()) {
            result.placeholder = SlotDisplayValues.load(input, itemSet);
        } else {
            result.placeholder = null;
        }
        return result;
    }

    public static ManualOutputSlotValues createQuick(String name, SlotDisplayValues placeholder) {
        ManualOutputSlotValues result = new ManualOutputSlotValues(true);
        result.setName(name);
        result.setPlaceholder(placeholder);
        return result;
    }

    private String name;
    private SlotDisplayValues placeholder;

    public ManualOutputSlotValues(boolean mutable) {
        super(mutable);
        this.name = "";
        this.placeholder = null;
    }

    public ManualOutputSlotValues(ManualOutputSlotValues toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.placeholder = toCopy.getPlaceholder();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.MANUAL_OUTPUT);
        output.addByte((byte) 1);

        output.addString(this.name);
        output.addBoolean(this.placeholder != null);
        if (this.placeholder != null) {
            this.placeholder.save(output);
        }
    }

    @Override
    public ManualOutputSlotValues copy(boolean mutable) {
        return new ManualOutputSlotValues(this, mutable);
    }

    @Override
    public ManualOutputSlotValues nonConflictingCopy(CustomContainerValues container) {
        int suffixInt = 0;
        String[] pSuffix = {""};
        while (container.createSlotList().stream().anyMatch(
                slot -> slot instanceof ManualOutputSlotValues && ((ManualOutputSlotValues) slot).getName().equals(name + pSuffix[0])
        )) {
            suffixInt += 1;
            pSuffix[0] = Integer.toString(suffixInt);
        }
        return createQuick(name + pSuffix[0], placeholder);
    }

    @Override
    public boolean canInsertItems() {
        return false;
    }

    @Override
    public boolean canTakeItems() {
        // Taking items is rather complex and needs to be handled manually
        return false;
    }

    public String getName() {
        return name;
    }

    public SlotDisplayValues getPlaceholder() {
        return placeholder;
    }

    public void setName(String name) {
        assertMutable();
        Checks.notNull(name);
        this.name = name;
    }

    public void setPlaceholder(SlotDisplayValues placeholder) {
        assertMutable();
        this.placeholder = placeholder != null ? placeholder.copy(false) : null;
    }

    @Override
    public void validate(ItemSet itemSet, Collection<ContainerSlotValues> otherSlots) throws ValidationException, ProgrammingValidationException {
        if (this.name == null) {
            throw new ProgrammingValidationException("No name");
        }
        if (this.placeholder != null) {
            Validation.scope("Placeholder", this.placeholder::validate, itemSet);
        }
        if (otherSlots.stream().anyMatch(candidate -> candidate instanceof ManualOutputSlotValues && ((ManualOutputSlotValues) candidate).getName().equals(this.name))) {
            throw new ValidationException("Multiple output slots have name " + this.name);
        }
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (this.placeholder != null) {
            Validation.scope("Placeholder", () -> this.placeholder.validateExportVersion(version));
        }
    }
}
