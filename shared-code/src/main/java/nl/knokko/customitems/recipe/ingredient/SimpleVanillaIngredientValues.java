package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import static nl.knokko.customitems.encoding.RecipeEncoding.Ingredient.*;

public class SimpleVanillaIngredientValues extends IngredientValues {

    static SimpleVanillaIngredientValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        SimpleVanillaIngredientValues result = new SimpleVanillaIngredientValues(false);

        if (encoding == VANILLA_SIMPLE) {
            result.load1(input);
        } else if (encoding == VANILLA_SIMPLE_2) {
            result.load2(input, itemSet);
        } else {
            throw new UnknownEncodingException("SimpleVanillaIngredient", encoding);
        }

        return result;
    }

    private byte amount;
    private CIMaterial material;

    public SimpleVanillaIngredientValues(boolean mutable) {
        super(mutable);

        this.amount = 1;
        this.material = null;
    }

    public SimpleVanillaIngredientValues(SimpleVanillaIngredientValues toCopy, boolean mutable) {
        super(mutable);

        this.amount = toCopy.getAmount();
        this.material = toCopy.getMaterial();
    }

    @Override
    public boolean conflictsWith(IngredientValues other) {
        if (other instanceof SimpleVanillaIngredientValues) {
            return this.material == ((SimpleVanillaIngredientValues) other).material;
        } else if (other instanceof DataVanillaIngredientValues) {
            return this.material == ((DataVanillaIngredientValues) other).getMaterial();
        } else {
            return false;
        }
    }

    @Override
    public SimpleVanillaIngredientValues copy(boolean mutable) {
        return new SimpleVanillaIngredientValues(this, mutable);
    }

    private void load1(BitInput input) {
        this.amount = 1;
        this.remainingItem = null;
        this.material = CIMaterial.valueOf(input.readJavaString());
    }

    private void load2(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.amount = input.readByte();
        loadRemainingItem(input, itemSet);
        this.material = CIMaterial.valueOf(input.readJavaString());
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(VANILLA_SIMPLE_2);
        save2(output);
    }

    private void save2(BitOutput output) {
        output.addByte(amount);
        saveRemainingItem(output);
        output.addJavaString(material.name());
    }

    public byte getAmount() {
        return amount;
    }

    public CIMaterial getMaterial() {
        return material;
    }

    public void setAmount(byte newAmount) {
        assertMutable();
        this.amount = newAmount;
    }

    public void setMaterial(CIMaterial newMaterial) {
        assertMutable();
        Checks.notNull(newMaterial);
        this.material = newMaterial;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (amount < 1) throw new ValidationException("Amount must be positive");
        if (amount > 64) throw new ValidationException("Amount can be at most 64");
        if (material == null) throw new ValidationException("You need to choose a material");
        if (material == CIMaterial.AIR) throw new ValidationException("Air is not allowed");
    }
}
