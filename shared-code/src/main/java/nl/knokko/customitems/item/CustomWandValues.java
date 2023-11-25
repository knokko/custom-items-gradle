package nl.knokko.customitems.item;

import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.itemset.ProjectileReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.List;
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
    private float manaCost;

    /** If this is null, the wand doesn't need charges and is only limited by its cooldown */
    private WandChargeValues charges;

    private int amountPerShot;

    private boolean requiresPermission;
    private List<String> magicSpells;

    public CustomWandValues(boolean mutable) {
        super(mutable, CustomItemType.DIAMOND_HOE);

        this.projectile = null;
        this.cooldown = 40;
        this.manaCost = 0f;
        this.charges = null;
        this.amountPerShot = 1;
        this.requiresPermission = false;
        this.magicSpells = new ArrayList<>();
    }

    public CustomWandValues(CustomWandValues toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.projectile = toCopy.getProjectileReference();
        this.cooldown = toCopy.getCooldown();
        this.manaCost = toCopy.getManaCost();
        this.charges = toCopy.getCharges();
        this.amountPerShot = toCopy.getAmountPerShot();
        this.requiresPermission = toCopy.requiresPermission();
        this.magicSpells = toCopy.getMagicSpells();
    }

    protected void loadWandPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.loadSharedPropertiesNew(input, itemSet);

        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 3) throw new UnknownEncodingException("WandNew", encoding);

        String projectileName = input.readString();
        if (projectileName != null) this.projectile = itemSet.getProjectileReference(projectileName);
        else this.projectile = null;

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

        if (encoding >= 3) {
            this.manaCost = input.readFloat();
            int numSpells = input.readInt();
            this.magicSpells = new ArrayList<>(numSpells);
            for (int counter = 0; counter < numSpells; counter++) this.magicSpells.add(input.readString());
        } else {
            this.manaCost = 0f;
            this.magicSpells = new ArrayList<>();
        }
    }

    protected void saveWandPropertiesNew(BitOutput output, ItemSet.Side side) {
        this.saveSharedPropertiesNew(output, side);

        output.addByte((byte) 3);

        if (this.projectile != null) output.addString(this.projectile.get().getName());
        else output.addString(null);
        output.addBoolean(this.charges != null);
        if (this.charges != null) {
            this.charges.save1(output);
        }
        output.addInts(this.cooldown, this.amountPerShot);
        output.addBoolean(this.requiresPermission);
        output.addFloat(this.manaCost);
        output.addInt(this.magicSpells.size());
        for (String spell : this.magicSpells) output.addString(spell);
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
                && this.manaCost == other.manaCost && this.magicSpells.equals(other.magicSpells)
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
        this.manaCost = 0f;
        this.magicSpells = new ArrayList<>();
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
        return projectile != null ? projectile.get() : null;
    }

    public int getCooldown() {
        return cooldown;
    }

    public float getManaCost() {
        return manaCost;
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

    public List<String> getMagicSpells() {
        return new ArrayList<>(magicSpells);
    }

    public void setProjectile(ProjectileReference newProjectile) {
        assertMutable();
        this.projectile = newProjectile;
    }

    public void setCooldown(int newCooldown) {
        assertMutable();
        this.cooldown = newCooldown;
    }

    public void setManaCost(float newCost) {
        assertMutable();
        this.manaCost = newCost;
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

    public void setMagicSpells(List<String> newSpells) {
        assertMutable();
        this.magicSpells = new ArrayList<>(newSpells);
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();

        if (magicSpells == null) throw new ProgrammingValidationException("No magic spells");
        if (projectile == null && magicSpells.isEmpty()) throw new ValidationException("You must choose a projectile or spell(s)");
        if (cooldown < 1) throw new ValidationException("Cooldown must be positive");
        if (manaCost < 0f) throw new ValidationException("Mana cost can't be negative");
        if (manaCost != manaCost) throw new ValidationException("Mana cost can't be NaN");
        if (charges != null) Validation.scope("Charges", charges::validate);
        if (amountPerShot < 1) throw new ValidationException("Amount per shot must be positive");
    }

    @Override
    public void validateComplete(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        super.validateComplete(itemSet, oldName);

        if (projectile != null && !itemSet.isReferenceValid(projectile)) {
            throw new ProgrammingValidationException("Projectile is no longer valid");
        }
    }
}
