package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.gun.DirectGunAmmo;
import nl.knokko.customitems.item.gun.GunAmmo;
import nl.knokko.customitems.itemset.ProjectileReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.KciProjectile;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class KciGun extends KciItem {

    static KciGun load(BitInput input, byte encoding, ItemSet itemSet) throws UnknownEncodingException {
        KciGun result = new KciGun(false);

        if (encoding == ItemEncoding.ENCODING_GUN_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else if (encoding == ItemEncoding.ENCODING_GUN_12) {
            result.loadGunPropertiesNew(input, itemSet);
            return result;
        } else {
            throw new UnknownEncodingException("CustomGun", encoding);
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    private ProjectileReference projectile;
    private GunAmmo ammo;
    private int amountPerShot;
    private boolean requiresPermission;

    public KciGun(boolean mutable) {
        super(mutable, KciItemType.DIAMOND_HOE);

        this.projectile = null;
        this.ammo = new DirectGunAmmo(false);
        this.amountPerShot = 1;
        this.requiresPermission = false;
    }

    public KciGun(KciGun toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.projectile = toCopy.getProjectileReference();
        this.ammo = toCopy.getAmmo();
        this.amountPerShot = toCopy.getAmountPerShot();
        this.requiresPermission = toCopy.requiresPermission();
    }

    protected void loadGunPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadSharedPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("CustomGunNew", encoding);

        this.projectile = itemSet.projectiles.getReference(input.readString());
        this.ammo = GunAmmo.load(input, itemSet);
        this.amountPerShot = input.readInt();

        if (encoding >= 2) {
            this.requiresPermission = input.readBoolean();
        } else {
            this.requiresPermission = false;
        }
    }

    protected void saveGunPropertiesNew(BitOutput output, ItemSet.Side targetSide) {
        this.saveSharedPropertiesNew(output, targetSide);

        output.addByte((byte) 2);

        output.addString(this.projectile.get().getName());
        this.ammo.save(output);
        output.addInt(this.amountPerShot);
        output.addBoolean(this.requiresPermission);
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_GUN_12);
        this.saveGunPropertiesNew(output, side);
    }

    @Override
    public byte getMaxStacksize() {
        return 1;
    }

    protected boolean areGunPropertiesEqual(KciGun other) {
        return areBaseItemPropertiesEqual(other) && this.projectile.equals(other.projectile)
                && this.ammo.equals(other.ammo) && this.amountPerShot == other.amountPerShot
                && this.requiresPermission == other.requiresPermission;
    }

    @Override
    public boolean equals(Object other) {
        return other.getClass() == KciGun.class && areGunPropertiesEqual((KciGun) other);
    }

    @Override
    public KciGun copy(boolean mutable) {
        return new KciGun(this, mutable);
    }

    private void load10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadBase10(input, itemSet);
        loadGunOnlyProperties10(input, itemSet);
    }

    private void loadGunOnlyProperties10(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.projectile = itemSet.projectiles.getReference(input.readString());
        this.ammo = GunAmmo.load(input, itemSet);
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

    public KciProjectile getProjectile() {
        return projectile.get();
    }

    public GunAmmo getAmmo() {
        return ammo;
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

    public void setAmmo(GunAmmo newAmmo) {
        assertMutable();
        Checks.notNull(newAmmo);
        this.ammo = newAmmo.copy(false);
    }

    public void setAmountPerShot(int newAmount) {
        assertMutable();
        this.amountPerShot = newAmount;
    }

    public void setRequiresPermission(boolean requiresPermission) {
        assertMutable();
        this.requiresPermission = requiresPermission;
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

        if (!itemSet.projectiles.isValid(projectile)) throw new ProgrammingValidationException("Projectile is no longer valid");
        ammo.validateComplete(itemSet);
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        super.validateExportVersion(version);
        Validation.scope("Ammo", () -> ammo.validateExportVersion(version));
    }
}
