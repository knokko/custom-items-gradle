package nl.knokko.customitems.recipe.ingredient;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.constraint.IngredientConstraints;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;

import static nl.knokko.customitems.encoding.RecipeEncoding.Ingredient.*;

public class SimpleVanillaIngredient extends KciIngredient {

    static SimpleVanillaIngredient load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        SimpleVanillaIngredient result = new SimpleVanillaIngredient(false);

        if (encoding == VANILLA_SIMPLE) {
            result.load1(input);
        } else if (encoding == VANILLA_SIMPLE_2) {
            result.load2(input, itemSet);
        } else if (encoding == VANILLA_SIMPLE_NEW) {
            result.loadNew(input, itemSet);
        } else {
            throw new UnknownEncodingException("SimpleVanillaIngredient", encoding);
        }

        return result;
    }

    public static SimpleVanillaIngredient createQuick(VMaterial material, int amount) {
        return createQuick(material, amount, null, new IngredientConstraints(true));
    }

    public static SimpleVanillaIngredient createQuick(
            VMaterial material, int amount, KciResult remainingItem, IngredientConstraints constraints
    ) {
        SimpleVanillaIngredient result = new SimpleVanillaIngredient(true);
        result.setMaterial(material);
        result.setAmount((byte) amount);
        result.setRemainingItem(remainingItem);
        result.setConstraints(constraints);
        return result;
    }

    private byte amount;
    private VMaterial material;

    public SimpleVanillaIngredient(boolean mutable) {
        super(mutable);

        this.amount = 1;
        this.material = VMaterial.STONE;
    }

    public SimpleVanillaIngredient(SimpleVanillaIngredient toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.amount = toCopy.getAmount();
        this.material = toCopy.getMaterial();
    }

    @Override
    public boolean conflictsWith(KciIngredient other) {
        if (other instanceof SimpleVanillaIngredient) {
            return this.material == ((SimpleVanillaIngredient) other).material;
        } else if (other instanceof DataVanillaIngredient) {
            return this.material == ((DataVanillaIngredient) other).getMaterial();
        } else {
            return false;
        }
    }

    @Override
    public SimpleVanillaIngredient copy(boolean mutable) {
        return new SimpleVanillaIngredient(this, mutable);
    }

    @Override
    public String toString() {
        return toString(null);
    }

    @Override
    public String toString(String emptyString) {
        return material + amountToString(amount) + remainingToString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SimpleVanillaIngredient) {
            SimpleVanillaIngredient otherIngredient = (SimpleVanillaIngredient) other;
            return this.material == otherIngredient.material && this.amount == otherIngredient.amount
                    && Objects.equals(this.remainingItem, otherIngredient.remainingItem)
                    && this.constraints.equals(otherIngredient.constraints);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return amount + 15 * material.ordinal() + 123 * Objects.hashCode(remainingItem);
    }

    private void load1(BitInput input) {
        this.amount = 1;
        this.remainingItem = null;
        this.material = VMaterial.valueOf(input.readJavaString());
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.amount = input.readByte();
        loadRemainingItem(input, itemSet);
        this.material = VMaterial.valueOf(input.readJavaString());
    }

    private void loadNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("InternalSimpleIngredient", encoding);

        this.amount = input.readByte();
        this.material = VMaterial.valueOf(input.readString());
        loadRemainingItem(input, itemSet);
        this.constraints = IngredientConstraints.load(input);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(VANILLA_SIMPLE_NEW);
        output.addByte((byte) 1);

        output.addByte(amount);
        output.addString(material.name());
        saveRemainingItem(output);
        constraints.save(output);
    }

    @Override
    public byte getAmount() {
        return amount;
    }

    public VMaterial getMaterial() {
        return material;
    }

    public void setAmount(byte newAmount) {
        assertMutable();
        this.amount = newAmount;
    }

    public void setMaterial(VMaterial newMaterial) {
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
        if (material == VMaterial.AIR) throw new ValidationException("Air is not allowed");
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(version);

        if (version < material.firstVersion) {
            throw new ValidationException(material + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > material.lastVersion) {
            throw new ValidationException(material + " was renamed after mc " + MCVersions.createString(material.lastVersion));
        }
    }
}
