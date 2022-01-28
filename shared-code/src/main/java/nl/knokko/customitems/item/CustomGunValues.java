package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.gun.DirectGunAmmoValues;
import nl.knokko.customitems.item.gun.GunAmmoValues;
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

public class CustomGunValues extends CustomItemValues {

    static CustomGunValues load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        CustomGunValues result = new CustomGunValues(false);

        if (encoding == ItemEncoding.ENCODING_GUN_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else {
            throw new UnknownEncodingException("CustomGun", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    private ProjectileReference projectile;
    private GunAmmoValues ammo;
    private int amountPerShot;

    public CustomGunValues(boolean mutable) {
        super(mutable, CustomItemType.DIAMOND_HOE);

        this.projectile = null;
        this.ammo = new DirectGunAmmoValues(false);
        this.amountPerShot = 1;
    }

    public CustomGunValues(CustomGunValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.projectile = toCopy.getProjectileReference();
        this.ammo = toCopy.getAmmo();
        this.amountPerShot = toCopy.getAmountPerShot();
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_GUN_10);
        save10(output);

        if (side == ItemSet.Side.EDITOR) {
            saveEditorOnlyProperties1(output);
        }
    }

    private void save10(BitOutput output) {
        saveBase10(output);
        saveGunOnlyProperties10(output);
    }

    private void saveGunOnlyProperties10(BitOutput output) {
        output.addString(projectile.get().getName());
        ammo.save(output);
        output.addInt(amountPerShot);
    }

    @Override
    public byte getMaxStacksize() {
        return 1;
    }

    protected boolean areGunPropertiesEqual(CustomGunValues other) {
        return areBaseItemPropertiesEqual(other) && this.projectile.equals(other.projectile)
                && this.ammo.equals(other.ammo) && this.amountPerShot == other.amountPerShot;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == CustomGunValues.class && areGunPropertiesEqual((CustomGunValues) other);
    }

    @Override
    public CustomGunValues copy(boolean mutable) {
        return new CustomGunValues(this, mutable);
    }

    private void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadBase10(input, itemSet);
        loadGunOnlyProperties10(input, itemSet);
    }

    private void loadGunOnlyProperties10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.projectile = itemSet.getProjectileReference(input.readString());
        this.ammo = GunAmmoValues.load(input, itemSet);
        this.amountPerShot = input.readInt();
    }

    private void initDefaults10() {
        initBaseDefaults10();
        initGunOnlyDefaults10();
    }

    private void initGunOnlyDefaults10() {
        // Nothing to be done until the next gun encoding is known
    }

    public ProjectileReference getProjectileReference() {
        return projectile;
    }

    public CustomProjectileValues getProjectile() {
        return projectile.get();
    }

    public GunAmmoValues getAmmo() {
        return ammo;
    }

    public int getAmountPerShot() {
        return amountPerShot;
    }

    public void setProjectile(ProjectileReference newProjectile) {
        assertMutable();
        Checks.notNull(newProjectile);
        this.projectile = newProjectile;
    }

    public void setAmmo(GunAmmoValues newAmmo) {
        assertMutable();
        Checks.notNull(newAmmo);
        this.ammo = newAmmo.copy(false);
    }

    public void setAmountPerShot(int newAmount) {
        assertMutable();
        this.amountPerShot = newAmount;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (projectile == null) throw new ValidationException("You must choose a projectile");
        if (ammo == null) throw new ProgrammingValidationException("No ammo");
        ammo.validateIndependent();
        if (amountPerShot < 1) throw new ValidationException("Amount per shot must be positive");
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (!itemSet.isReferenceValid(projectile)) throw new ProgrammingValidationException("Projectile is no longer valid");
        ammo.validateComplete(itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(version);
        Validation.scope("Ammo", () -> ammo.validateExportVersion(version));
    }
}
