package nl.knokko.customitems.item.gun;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class IndirectGunAmmoValues extends GunAmmoValues {

    static IndirectGunAmmoValues load(
            BitInput input, byte encoding, SItemSet itemSet
    ) throws UnknownEncodingException {
        IndirectGunAmmoValues result = new IndirectGunAmmoValues(false);

        if (encoding == ENCODING_INDIRECT_1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("IndirectGunAmmo", encoding);
        }

        return result;
    }

    private IngredientValues reloadItem;
    private int cooldown;
    private int storedAmmo;
    private int reloadTime;

    private CISound startReloadSound;
    private CISound endReloadSound;

    public IndirectGunAmmoValues(boolean mutable) {
        super(mutable);

        this.reloadItem = new NoIngredientValues();
        this.cooldown = 20;
        this.storedAmmo = 10;
        this.reloadTime = 40;

        this.startReloadSound = null;
        this.endReloadSound = null;
    }

    public IndirectGunAmmoValues(IndirectGunAmmoValues toCopy, boolean mutable) {
        super(mutable);

        this.reloadItem = toCopy.getReloadItem();
        this.cooldown = toCopy.getCooldown();
        this.storedAmmo = toCopy.getStoredAmmo();
        this.reloadTime = toCopy.getReloadTime();

        this.startReloadSound = toCopy.getStartReloadSound();
        this.endReloadSound = toCopy.getEndReloadSound();
    }

    @Override
    public IndirectGunAmmoValues copy(boolean mutable) {
        return new IndirectGunAmmoValues(this, mutable);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_INDIRECT_1);
        save1(output);
    }

    private void save1(BitOutput output) {
        reloadItem.save(output);
        output.addInt(cooldown);
        output.addInt(storedAmmo);
        output.addInt(reloadTime);

        output.addBoolean(startReloadSound != null);
        if (startReloadSound != null) {
            output.addString(startReloadSound.name());
        }

        output.addBoolean(endReloadSound != null);
        if (endReloadSound != null) {
            output.addString(endReloadSound.name());
        }
    }

    private void load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.reloadItem = IngredientValues.load(input, itemSet);
        this.cooldown = input.readInt();
        this.storedAmmo = input.readInt();
        this.reloadTime = input.readInt();

        if (input.readBoolean()) {
            this.startReloadSound = CISound.valueOf(input.readString());
        } else {
            this.startReloadSound = null;
        }

        if (input.readBoolean()) {
            this.endReloadSound = CISound.valueOf(input.readString());
        } else {
            this.endReloadSound = null;
        }
    }

    public IngredientValues getReloadItem() {
        return reloadItem;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    public int getStoredAmmo() {
        return storedAmmo;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public CISound getStartReloadSound() {
        return startReloadSound;
    }

    public CISound getEndReloadSound() {
        return endReloadSound;
    }

    public void setReloadItem(IngredientValues newReloadItem) {
        assertMutable();
        Checks.notNull(newReloadItem);
        this.reloadItem = newReloadItem.copy(false);
    }

    public void setCooldown(int newCooldown) {
        assertMutable();
        this.cooldown = newCooldown;
    }

    public void setStoredAmmo(int newStoredAmmo) {
        assertMutable();
        this.storedAmmo = newStoredAmmo;
    }

    public void setReloadTime(int newReloadTime) {
        assertMutable();
        this.reloadTime = newReloadTime;
    }

    public void setStartReloadSound(CISound newStartReloadSound) {
        assertMutable();
        this.startReloadSound = newStartReloadSound;
    }

    public void setEndReloadSound(CISound newEndReloadSound) {
        assertMutable();
        this.endReloadSound = newEndReloadSound;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (reloadItem != null) Validation.scope("Reload item", reloadItem::validateIndependent);
        if (cooldown < 1) throw new ValidationException("Cooldown must be positive");
        if (storedAmmo < 1) throw new ValidationException("Stored ammo must be positive");
        if (reloadTime < 1) throw new ValidationException("Reload time must be positive");
    }

    @Override
    public void validateComplete(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();
        if (reloadItem != null) Validation.scope("Reload item", () -> reloadItem.validateComplete(itemSet));
    }
}
