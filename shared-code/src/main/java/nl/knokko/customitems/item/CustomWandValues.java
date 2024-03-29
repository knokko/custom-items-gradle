package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ProjectileReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;

public class CustomWandValues extends CustomItemValues {

    public static CustomWandValues load(
            BitInput input, byte encoding, ItemSet itemSet
    ) throws UnknownEncodingException {
        CustomWandValues result = new CustomWandValues(false);

        if (encoding == ItemEncoding.ENCODING_WAND_9) {
            result.load9(input, itemSet);
            result.initDefaults9();
        } else if (encoding == ItemEncoding.ENCODING_WAND_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else if (encoding == ItemEncoding.ENCODING_WAND_12) {
            result.loadWandPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("CustomWand", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    private ProjectileReference projectile;

    private int cooldown;

    /** If this is null, the wand doesn't need charges and is only limited by its cooldown */
    private WandChargeValues charges;

    private int amountPerShot;

    private boolean requiresPermission;

    public CustomWandValues(boolean mutable) {
        super(mutable, CustomItemType.DIAMOND_HOE);

        this.projectile = null;
        this.cooldown = 40;
        this.charges = null;
        this.amountPerShot = 1;
        this.requiresPermission = false;
    }

    public CustomWandValues(CustomWandValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.projectile = toCopy.getProjectileReference();
        this.cooldown = toCopy.getCooldown();
        this.charges = toCopy.getCharges();
        this.amountPerShot = toCopy.getAmountPerShot();
        this.requiresPermission = toCopy.requiresPermission();
    }

    protected void loadWandPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadSharedPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("WandNew", encoding);

        this.projectile = itemSet.getProjectileReference(input.readString());
        if (input.readBoolean()) {
            this.charges = WandChargeValues.load1(input);
        } else {
            this.charges = null;
        }
        this.cooldown = input.readInt();
        this.amountPerShot = input.readInt();

        if (encoding >= 2) {
            this.requiresPermission = input.readBoolean();
        } else {
            this.requiresPermission = false;
        }
    }

    protected void saveWandPropertiesNew(BitOutput output, ItemSet.Side side) {
        this.saveSharedPropertiesNew(output, side);

        output.addByte((byte) 2);

        output.addString(this.projectile.get().getName());
        output.addBoolean(this.charges != null);
        if (this.charges != null) {
            this.charges.save1(output);
        }
        output.addInts(this.cooldown, this.amountPerShot);
        output.addBoolean(this.requiresPermission);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_WAND_12);
        this.saveWandPropertiesNew(output, side);
    }

    @Override
    public byte getMaxStacksize() {
        return 1;
    }

    protected boolean areWandPropertiesEqual(CustomWandValues other) {
        return areBaseItemPropertiesEqual(other) && this.projectile.equals(other.projectile)
                && this.amountPerShot == other.amountPerShot && this.cooldown == other.cooldown
                && Objects.equals(this.charges, other.charges) && this.requiresPermission == other.requiresPermission;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomWandValues.class && areWandPropertiesEqual((CustomWandValues) other);
    }

    @Override
    public CustomWandValues copy(boolean mutable) {
        return new CustomWandValues(this, mutable);
    }

    private void load9(BitInput input, ItemSet itemSet) {
        loadIdentityProperties1(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadItemFlags6(input);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
        loadWandOnlyProperties9(input, itemSet);
    }

    private void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadItemFlags6(input);
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadWandOnlyProperties9(input, itemSet);
        loadExtraProperties10(input);
    }

    private void loadWandOnlyProperties9(BitInput input, ItemSet itemSet) {
        this.projectile = itemSet.getProjectileReference(input.readString());
        this.cooldown = input.readInt();
        if (input.readBoolean()) {
            this.charges = WandChargeValues.load1(input);
        } else {
            this.charges = null;
        }
        this.amountPerShot = input.readInt();
    }

    private void initDefaults10() {
        initBaseDefaults10();
        initWandOnlyDefaults10();
    }

    private void initWandOnlyDefaults10() {
        // There is nothing to be done until the next encoding
    }

    private void initDefaults9() {
        initBaseDefaults9();
        initWandOnlyDefaults9();
    }

    private void initWandOnlyDefaults9() {
        initWandOnlyDefaults10();
        // No new wand-only properties were added in encoding 10
    }

    public ProjectileReference getProjectileReference() {
        return projectile;
    }

    public CustomProjectileValues getProjectile() {
        return projectile.get();
    }

    public int getCooldown() {
        return cooldown;
    }

    public WandChargeValues getCharges() {
        return charges;
    }

    public int getAmountPerShot() {
        return amountPerShot;
    }

    public boolean requiresPermission() {
        return requiresPermission;
    }

    public void setProjectile(ProjectileReference newProjectile) {
        assertMutable();
        Checks.notNull(newProjectile);
        this.projectile = newProjectile;
    }

    public void setCooldown(int newCooldown) {
        assertMutable();
        this.cooldown = newCooldown;
    }

    public void setCharges(WandChargeValues newCharges) {
        assertMutable();
        this.charges = newCharges != null ? newCharges.copy(false) : null;
    }

    public void setAmountPerShot(int newAmountPerShot) {
        assertMutable();
        this.amountPerShot = newAmountPerShot;
    }

    public void setRequiresPermission(boolean requiresPermission) {
        assertMutable();
        this.requiresPermission = requiresPermission;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (projectile == null) throw new ValidationException("You must choose a projectile");
        if (cooldown < 1) throw new ValidationException("Cooldown must be positive");
        if (charges != null) Validation.scope("Charges", charges::validate);
        if (amountPerShot < 1) throw new ValidationException("Amount per shot must be positive");
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (!itemSet.isReferenceValid(projectile)) throw new ProgrammingValidationException("Projectile is no longer valid");
    }
}
