package nl.knokko.customitems.recipe.result;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class ResultValues extends ModelValues  {

    public static ResultValues load(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == RecipeEncoding.Result.VANILLA_SIMPLE) {
            return SimpleVanillaResultValues.load(input, encoding);
        } else if (encoding == RecipeEncoding.Result.VANILLA_DATA) {
            return DataVanillaResultValues.load(input, encoding);
        } else if (encoding == RecipeEncoding.Result.CUSTOM) {
            return CustomItemResultValues.load(input, encoding, itemSet);
        } else if (encoding == RecipeEncoding.Result.COPIED) {
            return CopiedResultValues.load(input, encoding);
        } else {
            throw new UnknownEncodingException("Result", encoding);
        }
    }

    ResultValues(boolean mutable) {
        super(mutable);
    }

    public abstract ResultValues copy(boolean mutable);

    @Override
    public abstract String toString();

    protected byte loadAmount(BitInput input) {
        return (byte) (1 + input.readNumber((byte) 6, false));
    }

    public abstract void save(BitOutput output);

    protected void saveAmount(BitOutput output, byte amount) {
        output.addNumber(amount - 1, (byte) 6, false);
    }

    public abstract void validateIndependent() throws ValidationException, ProgrammingValidationException;

    public abstract void validateComplete(SItemSet itemSet) throws ValidationException, ProgrammingValidationException;
}