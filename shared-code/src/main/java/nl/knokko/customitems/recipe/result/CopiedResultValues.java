package nl.knokko.customitems.recipe.result;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.StringEncoder;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.List;

public class CopiedResultValues extends ResultValues {

    static CopiedResultValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        CopiedResultValues result = new CopiedResultValues(false);

        if (encoding == RecipeEncoding.Result.COPIED) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("CopiedResult", encoding);
        }

        return result;
    }

    public static CopiedResultValues createQuick(String encodedItem) {
        CopiedResultValues result = new CopiedResultValues(true);
        result.setEncodedItem(encodedItem);
        return result;
    }

    private String encoded;

    CopiedResultValues(boolean mutable) {
        super(mutable);

        this.encoded = null;
    }

    CopiedResultValues(CopiedResultValues toCopy, boolean mutable) {
        super(mutable);

        this.encoded = toCopy.getEncodedItem();
    }

    @Override
    public String toString() {
        return "copied item";
    }

    @Override
    protected List<String> getInfo() {
        List<String> result = new ArrayList<>(1);
        result.add("Copied from a server item");
        return result;
    }

    @Override
    public CopiedResultValues copy(boolean mutable) {
        return new CopiedResultValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CopiedResultValues) {
            return this.encoded.equals(((CopiedResultValues) other).encoded);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return encoded.hashCode();
    }

    private void load1(BitInput input) {
        // Discard the amount (we don't need it, but it is stored for stupid legacy reasons)
        input.readNumber((byte) 6, false);
        this.encoded = input.readString();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Result.COPIED);
        save1(output);
    }

    private void save1(BitOutput output) {
        // Save 0 for legacy reasons
        output.addNumber(0, (byte) 6, false);
        output.addString(encoded);
    }

    public String getEncodedItem() {
        return encoded;
    }

    public void setEncodedItem(String newEncodedItem) {
        assertMutable();
        Checks.notNull(newEncodedItem);
        this.encoded = newEncodedItem;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (encoded == null) throw new ValidationException("You still need to paste the copied text");
        try {
            StringEncoder.decode(encoded);
        } catch (IllegalArgumentException encodedIsInvalid) {
            throw new ValidationException("The copied text is invalid");
        }
    }

    @Override
    public void validateComplete(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException {
        // Checking this kind of result is too difficult
    }
}
