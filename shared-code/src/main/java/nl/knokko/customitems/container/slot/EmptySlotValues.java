package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitOutput;

import java.util.Collection;

public class EmptySlotValues extends ContainerSlotValues {

    public EmptySlotValues() {
        super(false);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.EMPTY);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof EmptySlotValues;
    }

    @Override
    public EmptySlotValues copy(boolean mutable) {
        return new EmptySlotValues();
    }

    @Override
    public boolean canInsertItems() {
        return false;
    }

    @Override
    public boolean canTakeItems() {
        return false;
    }

    @Override
    public void validate(SItemSet itemSet, Collection<ContainerSlotValues> otherSlots) throws ValidationException, ProgrammingValidationException {
        // This slot type doesn't have anything to validate
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        // This slot is supported in all MC versions
    }
}
