package nl.knokko.customitems.container.slot;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Collection;

public abstract class ContainerSlotValues extends ModelValues {

    static class Encodings {

        public static final byte DECORATION1 = 0;
        public static final byte EMPTY = 1;
        public static final byte FUEL1 = 2;
        public static final byte FUEL_INDICATOR1 = 3;
        public static final byte INPUT1 = 4;
        public static final byte OUTPUT1 = 5;
        public static final byte PROGRESS_INDICATOR1 = 6;

        /** Added fuel slot placeholders */
        public static final byte FUEL2 = 7;

        /** Added input slot placeholders */
        public static final byte INPUT2 = 8;

        /** Added output slot placeholders */
        public static final byte OUTPUT2 = 9;

        /** The first general storage slot encoding */
        public static final byte STORAGE1 = 10;

        public static final byte MANUAL_OUTPUT = 11;

        public static final byte ENERGY_INDICATOR = 12;

        public static final byte LINK = 13;

        public static final byte ACTION = 14;
    }

    public static ContainerSlotValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == Encodings.DECORATION1) return DecorationSlotValues.load(input, encoding, itemSet);
        else if (encoding == Encodings.EMPTY) return new EmptySlotValues();
        else if (encoding == Encodings.FUEL_INDICATOR1) return FuelIndicatorSlotValues.load(input, encoding, itemSet);
        else if (encoding == Encodings.FUEL1 || encoding == Encodings.FUEL2) return FuelSlotValues.load(input, encoding, itemSet);
        else if (encoding == Encodings.INPUT1 || encoding == Encodings.INPUT2) return InputSlotValues.load(input, encoding, itemSet);
        else if (encoding == Encodings.OUTPUT1 || encoding == Encodings.OUTPUT2) return OutputSlotValues.load(input, encoding, itemSet);
        else if (encoding == Encodings.PROGRESS_INDICATOR1) return ProgressIndicatorSlotValues.load(input, encoding, itemSet);
        else if (encoding == Encodings.STORAGE1) return StorageSlotValues.load(input, encoding, itemSet);
        else if (encoding == Encodings.MANUAL_OUTPUT) return ManualOutputSlotValues.loadManual(input, itemSet);
        else if (encoding == Encodings.ENERGY_INDICATOR) return EnergyIndicatorSlotValues.loadEnergy(input, itemSet);
        else if (encoding == Encodings.LINK) return LinkSlotValues.loadLink(input, itemSet);
        else if (encoding == Encodings.ACTION) return ActionSlotValues.loadAction(input, itemSet);
        else throw new UnknownEncodingException("ContainerSlot", encoding);
    }

    ContainerSlotValues(boolean mutable) {
        super(mutable);
    }

    public abstract void save(BitOutput output);

    @Override
    public abstract ContainerSlotValues copy(boolean mutable);

    public abstract ContainerSlotValues nonConflictingCopy(CustomContainerValues container);

    public abstract boolean canInsertItems();

    public abstract boolean canTakeItems();

    public abstract void validate(
            ItemSet itemSet, Collection<ContainerSlotValues> otherSlots
    ) throws ValidationException, ProgrammingValidationException;

    public abstract void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException;
}
