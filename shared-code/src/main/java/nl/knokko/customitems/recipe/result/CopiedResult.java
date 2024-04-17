package nl.knokko.customitems.recipe.result;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.StringEncoder;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.List;

public class CopiedResult extends KciResult {

    static CopiedResult load(BitInput input, byte encoding) throws UnknownEncodingException {
        CopiedResult result = new CopiedResult(false);

        if (encoding == RecipeEncoding.Result.COPIED) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("CopiedResult", encoding);
        }

        return result;
    }

    public static CopiedResult createQuick(String encodedItem) {
        CopiedResult result = new CopiedResult(true);
        result.setEncodedItem(encodedItem);
        return result;
    }

    private String encoded;

    CopiedResult(boolean mutable) {
        super(mutable);

        this.encoded = null;
    }

    CopiedResult(CopiedResult toCopy, boolean mutable) {
        super(mutable);

        this.encoded = toCopy.getEncodedItem();
    }

    @Override
    public String toString() {
        return "copied item";
    }

    @Override
    public List<String> getInfo() {
        List<String> result = new ArrayList<>(1);
        result.add("Copied from a server item");
        return result;
    }

    @Override
    public CopiedResult copy(boolean mutable) {
        return new CopiedResult(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CopiedResult) {
            return this.encoded.equals(((CopiedResult) other).encoded);
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
    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException {
        // Checking this kind of result is too difficult
    }
}
