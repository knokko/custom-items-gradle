package nl.knokko.customitems.recipe.result;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.List;

public abstract class KciResult extends ModelValues  {

    public static KciResult load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding == RecipeEncoding.Result.VANILLA_SIMPLE) {
            return SimpleVanillaResult.load(input, encoding);
        } else if (encoding == RecipeEncoding.Result.VANILLA_DATA) {
            return DataVanillaResult.load(input, encoding);
        } else if (encoding == RecipeEncoding.Result.CUSTOM) {
            return CustomItemResult.load(input, encoding, itemSet);
        } else if (encoding == RecipeEncoding.Result.COPIED) {
            return CopiedResult.load(input, encoding);
        } else if (encoding == RecipeEncoding.Result.MIMIC) {
            return MimicResult.load(input);
        } else if (encoding == RecipeEncoding.Result.ITEM_BRIDGE) {
            return ItemBridgeResult.load(input);
        } else if (encoding == RecipeEncoding.Result.UPGRADE) {
            return UpgradeResult.load(input, itemSet);
        } else {
            throw new UnknownEncodingException("Result", encoding);
        }
    }

    KciResult(boolean mutable) {
        super(mutable);
    }

    public abstract KciResult copy(boolean mutable);

    @Override
    public abstract String toString();

    public abstract List<String> getInfo();

    protected byte loadAmount(BitInput input) {
        return (byte) (1 + input.readNumber((byte) 6, false));
    }

    public abstract void save(BitOutput output);

    protected void saveAmount(BitOutput output, byte amount) {
        output.addNumber(amount - 1, (byte) 6, false);
    }

    public abstract byte getAmount();

    public abstract byte guessMaxStackSize();

    public abstract void validateIndependent() throws ValidationException, ProgrammingValidationException;

    public abstract void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException;

    public abstract void validateExportVersion(int version) throws ValidationException;
}
