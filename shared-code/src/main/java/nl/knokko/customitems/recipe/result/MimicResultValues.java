package nl.knokko.customitems.recipe.result;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class MimicResultValues extends ResultValues {

    public static MimicResultValues load(BitInput input) throws UnknownEncodingException {
        byte internalEncoding = input.readByte();
        if (internalEncoding != 1) throw new UnknownEncodingException("MimicResult", internalEncoding);

        MimicResultValues result = new MimicResultValues(false);
        result.itemId = input.readString();
        result.amount = input.readInt();
        return result;
    }

    private String itemId;
    private int amount;

    public MimicResultValues(boolean mutable) {
        super(mutable);
        this.itemId = "minecraft:stone";
        this.amount = 1;
    }

    public MimicResultValues(MimicResultValues toCopy, boolean mutable) {
        super(mutable);
        this.itemId = toCopy.getItemId();
        this.amount = toCopy.getAmount();
    }

    @Override
    public MimicResultValues copy(boolean mutable) {
        return new MimicResultValues(this, mutable);
    }

    @Override
    public String toString() {
        return "Mimic(" + this.itemId + " x " + amount + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MimicResultValues) {
            MimicResultValues otherMimic = (MimicResultValues) other;
            return this.itemId.equals(otherMimic.itemId) && this.amount == otherMimic.amount;
        } else {
            return false;
        }
    }

    @Override
    public List<String> getInfo() {
        List<String> result = new ArrayList<>(2);
        result.add("Mimic result:");
        result.add(this.itemId + " x " + this.amount);
        return result;
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(RecipeEncoding.Result.MIMIC);
        output.addByte((byte) 1);
        output.addString(this.itemId);
        output.addInt(this.amount);
    }

    public String getItemId() {
        return this.itemId;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setItemId(String itemId) {
        Checks.notNull(itemId);
        assertMutable();
        this.itemId = itemId;
    }

    public void setAmount(int amount) {
        assertMutable();
        this.amount = amount;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (this.itemId == null) throw new ProgrammingValidationException("No item id");
        if (!this.itemId.contains(":")) throw new ValidationException("Item id must contain ':'");
        if (this.amount < 1) throw new ValidationException("Amount must be positive");
        if (this.amount > 64) throw new ValidationException("Amount can be at most 64");
    }

    @Override
    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        this.validateIndependent();
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException {
        if (version < MCVersions.VERSION1_13) {
            throw new ValidationException("Mimic is only available for minecraft 1.13 and later");
        }
    }
}
