package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.gun.DirectGunAmmoValues;
import nl.knokko.customitems.item.gun.GunAmmoValues;
import nl.knokko.customitems.itemset.ProjectileReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomGunValues extends CustomItemValues {

    static CustomGunValues load(BitInput input, byte encoding, SItemSet itemSet) throws UnknownEncodingException {
        // Note: Initial item type doesn't matter because it will be overwritten during loading
        CustomGunValues result = new CustomGunValues(false, CustomItemType.DIAMOND_HOE);

        if (encoding == ItemEncoding.ENCODING_GUN_10) {
            result.load10(input, itemSet);
            result.initDefaults10();
        } else {
            throw new UnknownEncodingException("CustomGun", encoding);
        }

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            result.loadEditorOnlyProperties1(input, itemSet, true);
        }

        return result;
    }

    private ProjectileReference projectile;
    private GunAmmoValues ammo;
    private int amountPerShot;

    public CustomGunValues(boolean mutable, CustomItemType initialItemType) {
        super(mutable, initialItemType);

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
    public void save(BitOutput output, SItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_GUN_10);
        save10(output);

        if (side == SItemSet.Side.EDITOR) {
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

    @Override
    public CustomGunValues copy(boolean mutable) {
        return new CustomGunValues(this, mutable);
    }

    private void load10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        loadIdentityProperties10(input);
        loadTextDisplayProperties1(input);
        loadVanillaBasedPowers4(input);
        loadItemFlags6(input);
        loadPotionProperties10(input);
        loadRightClickProperties10(input, itemSet);
        loadExtraProperties10(input);
        loadGunOnlyProperties10(input, itemSet);
    }

    private void loadGunOnlyProperties10(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
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
    public void validateComplete(SItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (!itemSet.isReferenceValid(projectile)) throw new ProgrammingValidationException("Projectile is no longer valid");
        ammo.validateComplete(itemSet);
    }
}
