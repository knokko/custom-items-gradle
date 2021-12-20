package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ProjectileReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Objects;

public class CustomWandValues extends CustomItemValues {

    public static CustomWandValues load(
            BitInput input, byte encoding, SItemSet itemSet
    ) throws UnknownEncodingException {
        CustomWandValues result = new CustomWandValues(false);

        if (encoding == ItemEncoding.ENCODING_WAND_9) {
            result.load9(input, itemSet);
            result.initDefaults9();
        } else if (encoding == ItemEncoding.ENCODING_WAND_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else {
            throw new UnknownEncodingException("CustomWand", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    private ProjectileReference projectile;

    private int cooldown;

    /** If this is null, the wand doesn't need charges and is only limited by its cooldown */
    private WandChargeValues charges;

    private int amountPerShot;

    public CustomWandValues(boolean mutable) {
        super(mutable, CustomItemType.DIAMOND_HOE);

        this.projectile = null;
        this.cooldown = 40;
        this.charges = null;
        this.amountPerShot = 1;
    }

    public CustomWandValues(CustomWandValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.projectile = toCopy.getProjectileReference();
        this.cooldown = toCopy.getCooldown();
        this.charges = toCopy.getCharges();
        this.amountPerShot = toCopy.getAmountPerShot();
    }

    @Override
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_WAND_10);
        save10(output);

        if (side == SItemSet.Side.EDITOR) {
            saveEditorOnlyProperties1(output);
        }
    }

    private void save10(BitOutput output) {
        saveIdentityProperties10(output);
        saveTextDisplayProperties1(output);
        saveVanillaBasedPowers4(output);
        saveItemFlags6(output);
        savePotionProperties10(output);
        saveRightClickProperties10(output);
        saveWandOnlyProperties9(output);
        saveExtraProperties10(output);
    }

    private void saveWandOnlyProperties9(BitOutput output) {
        output.addString(projectile.get().getName());
        output.addInt(cooldown);
        output.addBoolean(charges != null);
        if (charges != null) {
            charges.save1(output);
        }
        output.addInt(amountPerShot);
    }

    @Override
    public byte getMaxStacksize() {
        return 1;
    }

    protected boolean areWandPropertiesEqual(CustomWandValues other) {
        return areBaseItemPropertiesEqual(other) && this.projectile.equals(other.projectile)
                && this.amountPerShot == other.amountPerShot && this.cooldown == other.cooldown
                && Objects.equals(this.charges, other.charges);
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomWandValues.class && areWandPropertiesEqual((CustomWandValues) other);
    }

    @Override
    public CustomWandValues copy(boolean mutable) {
        return new CustomWandValues(this, mutable);
    }

    private void load9(BitInput input, SItemSet itemSet) {
        loadIdentityProperties1(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadItemFlags6(input);
        loadPotionProperties9(input);
        loadRightClickProperties9(input);
        loadWandOnlyProperties9(input, itemSet);
    }

    private void load10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadItemFlags6(input);
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadWandOnlyProperties9(input, itemSet);
        loadExtraProperties10(input);
    }

    private void loadWandOnlyProperties9(BitInput input, SItemSet itemSet) {
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

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (projectile == null) throw new ValidationException("You must choose a projectile");
        if (cooldown < 1) throw new ValidationException("Cooldown must be positive");
        if (charges != null) Validation.scope("Charges", charges::validate);
        if (amountPerShot < 1) throw new ValidationException("Amount per shot must be positive");
    }

    @Override
    public void validateComplete(SItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (!itemSet.isReferenceValid(projectile)) throw new ProgrammingValidationException("Projectile is no longer valid");
    }
}
