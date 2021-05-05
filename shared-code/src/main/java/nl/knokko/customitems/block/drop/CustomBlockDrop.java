package nl.knokko.customitems.block.drop;

import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ExceptionSupplier;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.function.Consumer;
import java.util.function.Function;

public class CustomBlockDrop {

    private static final byte ENCODING_1 = 1;

    public static CustomBlockDrop load(
            BitInput input, Function<String, CustomItem> getItemByName,
            ExceptionSupplier<Object, UnknownEncodingException> loadResult, boolean mutable
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        CustomBlockDrop result = new CustomBlockDrop(mutable);
        if (encoding == ENCODING_1) {
            result.load1(input, getItemByName, loadResult);
        } else {
            throw new UnknownEncodingException("CustomBlockDrop", encoding);
        }

        return result;
    }

    private RequiredItems requiredItems;
    private SilkTouchRequirement silkTouch;
    private OutputTable itemsToDrop;

    private final boolean mutable;

    public CustomBlockDrop(boolean mutable) {
        this.mutable = mutable;

        this.requiredItems = new RequiredItems(false);
        this.silkTouch = SilkTouchRequirement.OPTIONAL;
        this.itemsToDrop = new OutputTable();
    }

    public CustomBlockDrop(CustomBlockDrop toCopy, boolean mutable) {
        this.mutable = mutable;

        this.requiredItems = toCopy.getRequiredItems();
        this.silkTouch = toCopy.getSilkTouchRequirement();
        this.itemsToDrop = toCopy.getItemsToDrop();
    }

    private void load1(
            BitInput input, Function<String, CustomItem> getItemByName,
            ExceptionSupplier<Object, UnknownEncodingException> loadResult
    ) throws UnknownEncodingException {
        this.requiredItems = RequiredItems.load(input, getItemByName, false);
        this.silkTouch = SilkTouchRequirement.valueOf(input.readString());
        this.itemsToDrop = OutputTable.load1(input, loadResult);
    }

    public void save(BitOutput output, Consumer<Object> saveResult) {
        output.addByte(ENCODING_1);
        save1(output, saveResult);
    }

    private void save1(BitOutput output, Consumer<Object> saveResult) {
        requiredItems.save(output);
        output.addString(silkTouch.name());
        itemsToDrop.save1(output, saveResult);
    }

    public RequiredItems getRequiredItems() {
        return requiredItems;
    }

    public SilkTouchRequirement getSilkTouchRequirement() {
        return silkTouch;
    }

    public OutputTable getItemsToDrop() {
        return itemsToDrop.copy();
    }

    private void assertMutable() {
        if (!mutable) throw new UnsupportedOperationException("This CustomBlockDrop is immutable");
    }

    public void setRequiredItems(RequiredItems newRequiredItems) {
        assertMutable();
        this.requiredItems = new RequiredItems(newRequiredItems, false);
    }

    public void setSilkTouchRequirement(SilkTouchRequirement newRequirement) {
        assertMutable();
        this.silkTouch = newRequirement;
    }

    public void setItemsToDrop(OutputTable newItemsToDrop) {
        assertMutable();
        this.itemsToDrop = newItemsToDrop.copy();
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (requiredItems == null)
            throw new ProgrammingValidationException("requiredItems is null");
        requiredItems.validateIndependent();

        if (silkTouch == null)
            throw new ProgrammingValidationException("silkTouch is null");

        if (itemsToDrop == null)
            throw new ProgrammingValidationException("itemsToDrop is null");
        String itemsToDropError = itemsToDrop.validate();
        if (itemsToDropError != null)
            throw new ValidationException(itemsToDropError);
    }

    public void validateComplete(
            Iterable<? extends CustomItem> customItems
    ) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        requiredItems.validateComplete(customItems);
    }
}
