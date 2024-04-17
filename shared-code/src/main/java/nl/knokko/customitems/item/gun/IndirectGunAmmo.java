package nl.knokko.customitems.item.gun;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.sound.KciSound;
import nl.knokko.customitems.sound.VSoundType;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;

public class IndirectGunAmmo extends GunAmmo {

    static IndirectGunAmmo load(
            BitInput input, byte encoding, ItemSet itemSet
    ) throws UnknownEncodingException {
        IndirectGunAmmo result = new IndirectGunAmmo(false);

        if (encoding == ENCODING_INDIRECT_1) {
            result.load1(input, itemSet);
        } else {
            throw new UnknownEncodingException("IndirectGunAmmo", encoding);
        }

        return result;
    }

    static IndirectGunAmmo loadNew(
            BitInput input, ItemSet itemSet
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("IndirectGunAmmo", encoding);

        IndirectGunAmmo result = new IndirectGunAmmo(false);
        result.reloadItem = KciIngredient.load(input, itemSet);
        result.cooldown = input.readInt();
        result.storedAmmo = input.readInt();
        result.reloadTime = input.readInt();
        if (input.readBoolean()) {
            result.startReloadSound = KciSound.load(input, itemSet);
        } else {
            result.startReloadSound = null;
        }
        if (input.readBoolean()) {
            result.endReloadSound = KciSound.load(input, itemSet);
        } else {
            result.endReloadSound = null;
        }
        return result;
    }

    private KciIngredient reloadItem;
    private int cooldown;
    private int storedAmmo;
    private int reloadTime;

    private KciSound startReloadSound;
    private KciSound endReloadSound;

    public IndirectGunAmmo(boolean mutable) {
        super(mutable);

        this.reloadItem = new NoIngredient();
        this.cooldown = 20;
        this.storedAmmo = 10;
        this.reloadTime = 40;

        this.startReloadSound = null;
        this.endReloadSound = null;
    }

    public IndirectGunAmmo(IndirectGunAmmo toCopy, boolean mutable) {
        super(mutable);

        this.reloadItem = toCopy.getReloadItem();
        this.cooldown = toCopy.getCooldown();
        this.storedAmmo = toCopy.getStoredAmmo();
        this.reloadTime = toCopy.getReloadTime();

        this.startReloadSound = toCopy.getStartReloadSound();
        this.endReloadSound = toCopy.getEndReloadSound();
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == IndirectGunAmmo.class) {
            IndirectGunAmmo otherAmmo = (IndirectGunAmmo) other;
            return this.reloadItem.equals(otherAmmo.reloadItem) && this.cooldown == otherAmmo.cooldown
                    && this.storedAmmo == otherAmmo.storedAmmo && this.reloadTime == otherAmmo.reloadTime
                    && Objects.equals(this.startReloadSound, otherAmmo.startReloadSound)
                    && Objects.equals(this.endReloadSound, otherAmmo.endReloadSound);
        } else {
            return false;
        }
    }

    @Override
    public IndirectGunAmmo copy(boolean mutable) {
        return new IndirectGunAmmo(this, mutable);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_INDIRECT_NEW);
        output.addByte((byte) 1);
        saveNew(output);
    }

    private void saveNew(BitOutput output) {
        reloadItem.save(output);
        output.addInt(cooldown);
        output.addInt(storedAmmo);
        output.addInt(reloadTime);

        output.addBoolean(startReloadSound != null);
        if (startReloadSound != null) {
            startReloadSound.save(output);
        }

        output.addBoolean(endReloadSound != null);
        if (endReloadSound != null) {
            endReloadSound.save(output);
        }
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.reloadItem = KciIngredient.load(input, itemSet);
        this.cooldown = input.readInt();
        this.storedAmmo = input.readInt();
        this.reloadTime = input.readInt();

        if (input.readBoolean()) {
            this.startReloadSound = KciSound.createQuick(VSoundType.valueOf(input.readString()), 1f, 1f).copy(false);
        } else {
            this.startReloadSound = null;
        }

        if (input.readBoolean()) {
            this.endReloadSound = KciSound.createQuick(VSoundType.valueOf(input.readString()), 1f, 1f).copy(false);
        } else {
            this.endReloadSound = null;
        }
    }

    public KciIngredient getReloadItem() {
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

    public KciSound getStartReloadSound() {
        return startReloadSound;
    }

    public KciSound getEndReloadSound() {
        return endReloadSound;
    }

    public void setReloadItem(KciIngredient newReloadItem) {
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

    public void setStartReloadSound(KciSound newStartReloadSound) {
        assertMutable();
        this.startReloadSound = newStartReloadSound != null ? newStartReloadSound.copy(false) : null;
    }

    public void setEndReloadSound(KciSound newEndReloadSound) {
        assertMutable();
        this.endReloadSound = newEndReloadSound != null ? newEndReloadSound.copy(false) : null;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (reloadItem != null) Validation.scope("Reload item", reloadItem::validateIndependent);
        if (cooldown < 1) throw new ValidationException("Cooldown must be positive");
        if (storedAmmo < 1) throw new ValidationException("Stored ammo must be positive");
        if (reloadTime < 1) throw new ValidationException("Reload time must be positive");
    }

    @Override
    public void validateComplete(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        validateIndependent();
        if (reloadItem != null) Validation.scope("Reload item", () -> reloadItem.validateComplete(itemSet));
        if (startReloadSound != null) Validation.scope("Start reload sound", startReloadSound::validate, itemSet);
        if (endReloadSound != null) Validation.scope("End reload sound", endReloadSound::validate, itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (reloadItem != null) reloadItem.validateExportVersion(version);
        if (startReloadSound != null) Validation.scope("Start reload sound", () -> startReloadSound.validateExportVersion(version));
        if (endReloadSound != null) Validation.scope("End reload sound", () -> endReloadSound.validateExportVersion(version));
    }
}
