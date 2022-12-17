package nl.knokko.customitems.item;

import nl.knokko.customitems.attack.effect.AttackEffectGroupValues;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static nl.knokko.customitems.encoding.ItemEncoding.ENCODING_ARROW;
import static nl.knokko.customitems.util.Checks.isClose;

public class CustomArrowValues extends CustomItemValues {

    static CustomArrowValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CustomArrow", encoding);

        CustomArrowValues arrow = new CustomArrowValues(false);
        arrow.loadSharedPropertiesNew(input, itemSet);
        arrow.maxStacksize = input.readByte();
        arrow.damageMultiplier = input.readFloat();
        arrow.speedMultiplier = input.readFloat();
        arrow.knockbackStrength = input.readInt();
        arrow.hasGravity = input.readBoolean();

        int numShootEffects = input.readInt();
        List<AttackEffectGroupValues> shootEffects = new ArrayList<>(numShootEffects);
        for (int counter = 0; counter < numShootEffects; counter++) {
            shootEffects.add(AttackEffectGroupValues.load(input, itemSet));
        }
        arrow.shootEffects = Collections.unmodifiableList(shootEffects);

        return arrow;
    }

    private byte maxStacksize;
    private float damageMultiplier;
    private float speedMultiplier;
    private int knockbackStrength;
    private boolean hasGravity;
    private Collection<AttackEffectGroupValues> shootEffects;

    public CustomArrowValues(boolean mutable) {
        super(mutable, CustomItemType.OTHER);
        this.otherMaterial = CIMaterial.ARROW;
        this.maxStacksize = 64;
        this.damageMultiplier = 1f;
        this.speedMultiplier = 1f;
        this.knockbackStrength = 0;
        this.hasGravity = true;
        this.shootEffects = Collections.emptyList();
    }

    public CustomArrowValues(CustomArrowValues toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.maxStacksize = toCopy.getMaxStacksize();
        this.damageMultiplier = toCopy.getDamageMultiplier();
        this.speedMultiplier = toCopy.getSpeedMultiplier();
        this.knockbackStrength = toCopy.getKnockbackStrength();
        this.hasGravity = toCopy.shouldHaveGravity();
        this.shootEffects = toCopy.getShootEffects();
    }

    @Override
    public CustomArrowValues copy(boolean mutable) {
        return new CustomArrowValues(this, mutable);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ENCODING_ARROW);
        output.addByte((byte) 1);
        saveSharedPropertiesNew(output, side);

        output.addByte(maxStacksize);
        output.addFloat(damageMultiplier);
        output.addFloat(speedMultiplier);
        output.addInt(knockbackStrength);
        output.addBoolean(hasGravity);
        output.addInt(shootEffects.size());
        for (AttackEffectGroupValues effect : shootEffects) effect.save(output);
    }

    @Override
    public byte getMaxStacksize() {
        return maxStacksize;
    }

    protected boolean areArrowPropertiesEqual(CustomArrowValues other) {
        return areBaseItemPropertiesEqual(other) && other.maxStacksize == this.maxStacksize
                && isClose(other.damageMultiplier, this.damageMultiplier)
                && isClose(other.speedMultiplier, this.speedMultiplier)
                && other.knockbackStrength == this.knockbackStrength
                && other.hasGravity == this.hasGravity
                && other.shootEffects.equals(this.shootEffects);
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomArrowValues.class && areArrowPropertiesEqual((CustomArrowValues) other);
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public int getKnockbackStrength() {
        return knockbackStrength;
    }

    public boolean shouldHaveGravity() {
        return hasGravity;
    }

    public Collection<AttackEffectGroupValues> getShootEffects() {
        return shootEffects;
    }

    public void setMaxStacksize(byte newStacksize) {
        assertMutable();
        this.maxStacksize = newStacksize;
    }

    public void setDamageMultiplier(float newDamageMultiplier) {
        assertMutable();
        this.damageMultiplier = newDamageMultiplier;
    }

    public void setSpeedMultiplier(float newSpeedMultiplier) {
        assertMutable();
        this.speedMultiplier = newSpeedMultiplier;
    }

    public void setKnockbackStrength(int newKnockbackStrength) {
        assertMutable();
        this.knockbackStrength = newKnockbackStrength;
    }

    public void setGravity(boolean shouldHaveGravity) {
        assertMutable();
        this.hasGravity = shouldHaveGravity;
    }

    public void setShootEffects(Collection<AttackEffectGroupValues> newShootEffects) {
        assertMutable();
        this.shootEffects = Collections.unmodifiableList(new ArrayList<>(newShootEffects));
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (itemType != CustomItemType.OTHER) throw new ValidationException("Item type must be OTHER");
        if (otherMaterial != CIMaterial.ARROW) throw new ValidationException("Material must be ARROW");

        if (maxStacksize < 1 || maxStacksize > 64) throw new ValidationException("Maximum stacksize must be between 1 and 64");
        if (damageMultiplier < 0f) throw new ValidationException("Damage multiplier can't be negative");
        // Note: speed multiplier and knockback strength are allowed to be negative

        if (shootEffects == null) throw new ProgrammingValidationException("No shoot effects");
        if (shootEffects.contains(null)) throw new ProgrammingValidationException("Missing a shoot effect");
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        for (AttackEffectGroupValues effects : shootEffects) Validation.scope("Shoot effect", effects::validate, itemSet);
    }

    @Override
    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(mcVersion);

        for (AttackEffectGroupValues effects : shootEffects) {
            Validation.scope("Shoot effect", () -> effects.validateExportVersion(mcVersion));
        }
    }
}
