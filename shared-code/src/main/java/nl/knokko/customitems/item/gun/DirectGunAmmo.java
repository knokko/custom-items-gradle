package nl.knokko.customitems.item.gun;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class DirectGunAmmo extends GunAmmo {

    static DirectGunAmmo load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        DirectGunAmmo result = new DirectGunAmmo(false);

        if (encoding == ENCODING_DIRECT_1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("DirectGunAmmo", encoding);
        }

        return result;
    }

    private KciIngredient ammoItem;
    private int cooldown;

    public DirectGunAmmo(boolean mutable) {
        super(mutable);

        this.ammoItem = new NoIngredient();
        this.cooldown = 20;
    }

    public DirectGunAmmo(DirectGunAmmo toCopy, boolean mutable) {
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

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.ammoItem = KciIngredient.load(input, itemSet);
        this.cooldown = input.readInt();
    }

    public KciIngredient getAmmoItem() {
        return ammoItem;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (ammoItem == null) throw new ProgrammingValidationException("No ammo item");
        if (ammoItem instanceof NoIngredient) throw new ValidationException("You must pick an ammo item");

        if (cooldown < 1) throw new ValidationException("Cooldown must be positive");
    }

    @Override
    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        ammoItem.validateComplete(itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        ammoItem.validateExportVersion(version);
    }

    public void setAmmoItem(KciIngredient newAmmoItem) {
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
        if (other.getClass() == DirectGunAmmo.class) {
            DirectGunAmmo otherAmmo = (DirectGunAmmo) other;
            return this.ammoItem.equals(otherAmmo.ammoItem) && this.cooldown == otherAmmo.cooldown;
        } else {
            return false;
        }
    }

    @Override
    public DirectGunAmmo copy(boolean mutable) {
        return new DirectGunAmmo(this, mutable);
    }
}
