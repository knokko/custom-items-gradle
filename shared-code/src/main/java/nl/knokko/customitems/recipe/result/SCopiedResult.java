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

public class SCopiedResult extends SResult {

    static SCopiedResult load(BitInput input, byte encoding) throws UnknownEncodingException {
        SCopiedResult result = new SCopiedResult(false);

        if (encoding == RecipeEncoding.Result.COPIED) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("CopiedResult", encoding);
        }

        return result;
    }

    private String encoded;

    SCopiedResult(boolean mutable) {
        super(mutable);

        this.encoded = null;
    }

    SCopiedResult(SCopiedResult toCopy, boolean mutable) {
        super(mutable);

        this.encoded = toCopy.getEncodedItem();
    }

    @Override
    public SCopiedResult copy(boolean mutable) {
        return new SCopiedResult(this, mutable);
    }

    private void load1(BitInput input) {
        this.encoded = input.readString();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Result.COPIED);
        save1(output);
    }

    private void save1(BitOutput output) {
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
}