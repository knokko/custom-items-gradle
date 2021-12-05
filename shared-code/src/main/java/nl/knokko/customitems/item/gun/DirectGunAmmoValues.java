package nl.knokko.customitems.item.gun;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class DirectGunAmmoValues extends GunAmmoValues {

    static DirectGunAmmoValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        DirectGunAmmoValues result = new DirectGunAmmoValues(false);

        if (encoding == ENCODING_DIRECT_1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("DirectGunAmmo", encoding);
        }

        return result;
    }

    private IngredientValues ammoItem;
    private int cooldown;

    public DirectGunAmmoValues(boolean mutable) {
        super(mutable);

        this.ammoItem = new NoIngredientValues();
        this.cooldown = 20;
    }

    public DirectGunAmmoValues(DirectGunAmmoValues toCopy, boolean mutable) {
        super(mutable);

        this.ammoItem = toCopy.getAmmoItem();
        this.cooldown = toCopy.getCooldown();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_DIRECT_1);
        save1(output);
    }

    private void save1(BitOutput output) {
        ammoItem.save(output);
        output.addInt(cooldown);
    }

    private void load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.ammoItem = IngredientValues.load(input, itemSet);
        this.cooldown = input.readInt();
    }

    public IngredientValues getAmmoItem() {
        return ammoItem;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (ammoItem == null) throw new ProgrammingValidationException("No ammo item");
        if (ammoItem instanceof NoIngredientValues) throw new ValidationException("You must pick an ammo item");

        if (cooldown < 1) throw new ValidationException("Cooldown must be positive");
    }

    @Override
    public void validateComplete(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        ammoItem.validateComplete(itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        ammoItem.validateExportVersion(version);
    }

    public void setAmmoItem(IngredientValues newAmmoItem) {
        assertMutable();
        Checks.notNull(newAmmoItem);
        this.ammoItem = newAmmoItem.copy(false);
    }

    public void setCooldown(int newCooldown) {
        assertMutable();
        this.cooldown = newCooldown;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == DirectGunAmmoValues.class) {
            DirectGunAmmoValues otherAmmo = (DirectGunAmmoValues) other;
            return this.ammoItem.equals(otherAmmo.ammoItem) && this.cooldown == otherAmmo.cooldown;
        } else {
            return false;
        }
    }

    @Override
    public DirectGunAmmoValues copy(boolean mutable) {
        return new DirectGunAmmoValues(this, mutable);
    }
}
