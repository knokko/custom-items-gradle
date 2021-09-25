package nl.knokko.customitems.item.gun;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.ingredient.SIngredient;
import nl.knokko.customitems.recipe.ingredient.SNoIngredient;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
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

    private SIngredient ammoItem;
    private int cooldown;

    public DirectGunAmmoValues(boolean mutable) {
        super(mutable);

        this.ammoItem = new SNoIngredient();
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
        this.ammoItem = SIngredient.load(input, itemSet);
        this.cooldown = input.readInt();
    }

    public SIngredient getAmmoItem() {
        return ammoItem;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (ammoItem == null) throw new ProgrammingValidationException("No ammo item");
        if (ammoItem instanceof SNoIngredient) throw new ValidationException("You must pick an ammo item");

        if (cooldown < 1) throw new ValidationException("Cooldown must be positive");
    }

    @Override
    public void validateComplete(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        ammoItem.validateComplete(itemSet);
    }

    public void setAmmoItem(SIngredient newAmmoItem) {
        assertMutable();
        Checks.notNull(newAmmoItem);
        this.ammoItem = newAmmoItem.copy(false);
    }

    public void setCooldown(int newCooldown) {
        assertMutable();
        this.cooldown = newCooldown;
    }

    @Override
    public DirectGunAmmoValues copy(boolean mutable) {
        return new DirectGunAmmoValues(this, mutable);
    }
}
