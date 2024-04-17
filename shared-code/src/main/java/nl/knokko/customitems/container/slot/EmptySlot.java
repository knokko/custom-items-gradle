package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Collection;

public class EmptySlot extends ContainerSlot {

    public EmptySlot() {
        super(false);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(Encodings.EMPTY);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof EmptySlot;
    }

    @Override
    public EmptySlot copy(boolean mutable) {
        return new EmptySlot();
    }

    @Override
    public EmptySlot nonConflictingCopy(KciContainer container) {
        return new EmptySlot();
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
    public void validate(ItemSet itemSet, Collection<ContainerSlot> otherSlots) throws ValidationException, ProgrammingValidationException {
        // This slot type doesn't have anything to validate
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        // This slot is supported in all MC versions
    }
}
