package nl.knokko.customitems.projectile;

import nl.knokko.customitems.effect.PotionEffectValues;
import nl.knokko.customitems.itemset.ProjectileCoverReference;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.projectile.effect.ProjectileEffectValues;
import nl.knokko.customitems.projectile.effect.ProjectileEffectsValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.util.Checks.isClose;

public class CustomProjectileValues extends ModelValues {

    private static final byte ENCODING_1 = 0;
    private static final byte ENCODING_2 = 1;

    public static CustomProjectileValues load(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        CustomProjectileValues result = new CustomProjectileValues(false);

        if (encoding == ENCODING_1) {
            result.load1(input, itemSet);
            result.initDefaults1();
        } else if (encoding == ENCODING_2) {
            result.load2(input, itemSet);
        } else {
            throw new UnknownEncodingException("CustomProjectile", encoding);
        }

        return result;
    }

    private String name;

    private float damage;
    private float minLaunchAngle, maxLaunchAngle;
    private float minLaunchSpeed, maxLaunchSpeed;
    private float gravity;
    private float launchKnockback, impactKnockback;

    private Collection<PotionEffectValues> impactPotionEffects;
    private int maxLifetime;
    private Collection<ProjectileEffectsValues> inFlightEffects;
    private Collection<ProjectileEffectValues> impactEffects;

    private ProjectileCoverReference cover;

    public CustomProjectileValues(boolean mutable) {
        super(mutable);
        this.name = "";
        this.damage = 5f;
        this.minLaunchAngle = 0f;
        this.maxLaunchAngle = 5f;
        this.minLaunchSpeed = 1.1f;
        this.maxLaunchSpeed = 1.3f;
        this.gravity = 0.02f;
        this.launchKnockback = 0f;
        this.impactKnockback = 0f;
        this.impactPotionEffects = new ArrayList<>(0);
        this.maxLifetime = 200;
        this.inFlightEffects = new ArrayList<>();
        this.impactEffects = new ArrayList<>();
    }

    public CustomProjectileValues(CustomProjectileValues toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.damage = toCopy.getDamage();
        this.minLaunchAngle = toCopy.getMinLaunchAngle();
        this.maxLaunchAngle = toCopy.getMaxLaunchAngle();
        this.minLaunchSpeed = toCopy.getMinLaunchSpeed();
        this.maxLaunchSpeed = toCopy.getMaxLaunchSpeed();
        this.gravity = toCopy.getGravity();
        this.launchKnockback = toCopy.getLaunchKnockback();
        this.impactKnockback = toCopy.getImpactKnockback();
        this.impactPotionEffects = toCopy.getImpactPotionEffects();
        this.maxLifetime = toCopy.getMaxLifetime();
        this.inFlightEffects = toCopy.getInFlightEffects();
        this.impactEffects = toCopy.getImpactEffects();
        this.cover = toCopy.getCoverReference();
    }

    private void loadProjectileEffects(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        int numFlightEffects = input.readByte() & 0xFF;
        this.inFlightEffects = new ArrayList<>(numFlightEffects);
        for (int counter = 0; counter < numFlightEffects; counter++) {
            this.inFlightEffects.add(ProjectileEffectsValues.load(input, itemSet));
        }

        int numImpactEffects = input.readByte() & 0xFF;
        this.impactEffects = new ArrayList<>(numImpactEffects);
        for (int counter = 0; counter < numImpactEffects; counter++) {
            this.impactEffects.add(ProjectileEffectValues.load(input, itemSet));
        }
    }

    private void load1(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.name = input.readString();
        this.damage = input.readFloat();
        this.minLaunchAngle = input.readFloat();
        this.maxLaunchAngle = input.readFloat();
        this.minLaunchSpeed = input.readFloat();
        this.maxLaunchSpeed = input.readFloat();
        this.gravity = input.readFloat();
        this.maxLifetime = input.readInt();
        this.loadProjectileEffects(input, itemSet);
        String coverName = input.readString();
        if (coverName != null) {
            this.cover = itemSet.getProjectileCoverReference(coverName);
        } else {
            this.cover = null;
        }
    }

    private void load2(BitInput input, SItemSet itemSet) throws UnknownEncodingException {
        this.name = input.readString();
        this.damage = input.readFloat();
        this.minLaunchAngle = input.readFloat();
        this.maxLaunchAngle = input.readFloat();
        this.minLaunchSpeed = input.readFloat();
        this.maxLaunchSpeed = input.readFloat();
        this.gravity = input.readFloat();
        this.launchKnockback = input.readFloat();
        this.impactKnockback = input.readFloat();

        int numImpactPotionEffects = input.readInt();
        this.impactPotionEffects = new ArrayList<>(numImpactPotionEffects);
        for (int counter = 0; counter < numImpactPotionEffects; counter++) {
            this.impactPotionEffects.add(PotionEffectValues.load2(input, false));
        }

        this.maxLifetime = input.readInt();
        loadProjectileEffects(input, itemSet);

        String coverName = input.readString();
        if (coverName != null) {
            this.cover = itemSet.getProjectileCoverReference(coverName);
        } else {
            this.cover = null;
        }
    }

    private void initDefaults1() {
        this.launchKnockback = 0f;
        this.impactKnockback = 0f;
        this.impactPotionEffects = new ArrayList<>(0);
    }

    public void save(BitOutput output) {
        output.addByte(ENCODING_2);
        output.addString(name);
        output.addFloats(damage, minLaunchAngle, maxLaunchAngle, minLaunchSpeed, maxLaunchSpeed, gravity, launchKnockback, impactKnockback);

        output.addInt(impactPotionEffects.size());
        for (PotionEffectValues effect : impactPotionEffects) {
            effect.save2(output);
        }

        output.addInt(maxLifetime);

        output.addByte((byte) inFlightEffects.size());
        for (ProjectileEffectsValues effects : inFlightEffects) {
            effects.save(output);
        }
        output.addByte((byte) impactEffects.size());
        for (ProjectileEffectValues effect : impactEffects) {
            effect.save(output);
        }
        output.addString(cover == null ? null : cover.get().getName());
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == CustomProjectileValues.class) {
            CustomProjectileValues otherProjectile = (CustomProjectileValues) other;
            return this.name.equals(otherProjectile.name) && isClose(this.damage, otherProjectile.damage)
                    && isClose(this.minLaunchAngle, otherProjectile.minLaunchAngle)
                    && isClose(this.maxLaunchAngle, otherProjectile.maxLaunchAngle)
                    && isClose(this.minLaunchSpeed, otherProjectile.minLaunchSpeed)
                    && isClose(this.maxLaunchSpeed, otherProjectile.maxLaunchSpeed)
                    && isClose(this.gravity, otherProjectile.gravity)
                    && isClose(this.launchKnockback, otherProjectile.launchKnockback)
                    && isClose(this.impactKnockback, otherProjectile.impactKnockback)
                    && this.impactPotionEffects.equals(otherProjectile.impactPotionEffects)
                    && this.maxLifetime == otherProjectile.maxLifetime
                    && this.inFlightEffects.equals(otherProjectile.inFlightEffects)
                    && this.impactEffects.equals(otherProjectile.impactEffects);
        } else {
            return false;
        }
    }

    @Override
    public CustomProjectileValues copy(boolean mutable) {
        return new CustomProjectileValues(this, mutable);
    }

    public String getName() {
        return name;
    }

    public float getDamage() {
        return damage;
    }

    public float getMinLaunchAngle() {
        return minLaunchAngle;
    }

    public float getMaxLaunchAngle() {
        return maxLaunchAngle;
    }

    public float getMinLaunchSpeed() {
        return minLaunchSpeed;
    }

    public float getMaxLaunchSpeed() {
        return maxLaunchSpeed;
    }

    public float getGravity() {
        return gravity;
    }

    public float getLaunchKnockback() {
        return launchKnockback;
    }

    public float getImpactKnockback() {
        return impactKnockback;
    }

    public Collection<PotionEffectValues> getImpactPotionEffects() {
        return new ArrayList<>(impactPotionEffects);
    }

    public int getMaxLifetime() {
        return maxLifetime;
    }

    public Collection<ProjectileEffectsValues> getInFlightEffects() {
        return new ArrayList<>(inFlightEffects);
    }

    public Collection<ProjectileEffectValues> getImpactEffects() {
        return new ArrayList<>(impactEffects);
    }

    public ProjectileCoverReference getCoverReference() {
        return cover;
    }

    public ProjectileCoverValues getCover() {
        return cover == null ? null : cover.get();
    }

    public void setDamage(float newDamage) {
        assertMutable();
        this.damage = newDamage;
    }

    public void setMinLaunchAngle(float newLaunchAngle) {
        assertMutable();
        this.minLaunchAngle = newLaunchAngle;
    }

    public void setMaxLaunchAngle(float newLaunchAngle) {
        assertMutable();
        this.maxLaunchAngle = newLaunchAngle;
    }

    public void setMinLaunchSpeed(float newLaunchSpeed) {
        assertMutable();
        this.minLaunchSpeed = newLaunchSpeed;
    }

    public void setMaxLaunchSpeed(float newLaunchSpeed) {
        assertMutable();
        this.maxLaunchSpeed = newLaunchSpeed;
    }

    public void setGravity(float newGravity) {
        assertMutable();
        this.gravity = newGravity;
    }

    public void setLaunchKnockback(float newKnockback) {
        assertMutable();
        this.launchKnockback = newKnockback;
    }

    public void setImpactKnockback(float newKnockback) {
        assertMutable();
        this.impactKnockback = newKnockback;
    }

    public void setImpactPotionEffects(Collection<PotionEffectValues> newImpactPotionEffects) {
        assertMutable();
        Checks.nonNull(newImpactPotionEffects);
        this.impactPotionEffects = Mutability.createDeepCopy(newImpactPotionEffects, false);
    }

    public void setMaxLifetime(int newLifetime) {
        assertMutable();
        this.maxLifetime = newLifetime;
    }

    public void setInFlightEffects(Collection<ProjectileEffectsValues> newFlightEffects) {
        assertMutable();
        Checks.nonNull(newFlightEffects);
        this.inFlightEffects = Mutability.createDeepCopy(newFlightEffects, false);
    }

    public void setImpactEffects(Collection<ProjectileEffectValues> newImpactEffects) {
        assertMutable();
        Checks.nonNull(newImpactEffects);
        this.impactEffects = Mutability.createDeepCopy(newImpactEffects, false);
    }

    public void validate(SItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("You need to choose a name");
        if (!name.equals(oldName) && itemSet.getProjectile(name).isPresent())
            throw new ValidationException("A projectile with this name already exists");

        if (damage < 0f) throw new ValidationException("Damage can't be negative");
        if (Float.isNaN(damage)) throw new ValidationException("Damage can't be NaN");
        if (minLaunchAngle < 0f) throw new ValidationException("Minimum launch angle can't be negative");
        if (Float.isNaN(minLaunchAngle)) throw new ValidationException("Minimum launch angle can't be NaN");
        if (minLaunchAngle > maxLaunchAngle)
            throw new ValidationException("Minimum launch angle can't be larger than maximum launch angle");
        if (Float.isNaN(maxLaunchAngle)) throw new ValidationException("Maximum launch angle can't be NaN");
        if (minLaunchSpeed < 0f) throw new ValidationException("Minimum launch speed can't be negative");
        if (Float.isNaN(minLaunchSpeed)) throw new ValidationException("Minimum launch speed can't be NaN");
        if (minLaunchSpeed > maxLaunchSpeed)
            throw new ValidationException("Minimum launch speed can't be larger than maximum launch speed");
        if (Float.isNaN(maxLaunchSpeed)) throw new ValidationException("Maximum launch speed can't be NaN");
        if (Float.isNaN(gravity)) throw new ValidationException("Gravity can't be NaN");
        if (Float.isNaN(launchKnockback)) throw new ValidationException("Launch knockback can't be NaN");
        if (Float.isNaN(impactKnockback)) throw new ValidationException("Impact knockback can't be NaN");

        if (impactPotionEffects == null) throw new ProgrammingValidationException("No impact potion effects");
        for (PotionEffectValues impactEffect : impactPotionEffects) {
            if (impactEffect == null) throw new ProgrammingValidationException("Missing an impact potion effect");
            Validation.scope(
                    "Impact potion effect", impactEffect::validate
            );
        }

        if (maxLifetime <= 0) throw new ValidationException("Maximum lifetime must be positive");

        if (inFlightEffects == null) throw new ProgrammingValidationException("No in-flight effects");
        if (inFlightEffects.size() > Byte.MAX_VALUE) throw new ValidationException("Too many in-flight effects");
        for (ProjectileEffectsValues effects : inFlightEffects) {
            if (effects == null) throw new ProgrammingValidationException("Missing an in-flight effect save");
            Validation.scope(
                    "In-flight effect", () -> effects.validate(itemSet)
            );
        }

        if (impactEffects == null) throw new ProgrammingValidationException("No impact effects");
        if (impactEffects.size() > Byte.MAX_VALUE) throw new ValidationException("Too many impact effects");
        for (ProjectileEffectValues effect : impactEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an impact effect");
            Validation.scope(
                    "Impact effect", () -> effect.validate(itemSet)
            );
        }

        if (cover != null && !itemSet.isReferenceValid(cover))
            throw new ProgrammingValidationException("Projectile cover is no longer valid");
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        for (PotionEffectValues impactEffect : impactPotionEffects) {
            Validation.scope("Impact potion effects", () -> impactEffect.validateExportVersion(version));
        }
        for (ProjectileEffectValues impactEffect : impactEffects) {
            Validation.scope("Impact effects", () -> impactEffect.validateExportVersion(version));
        }
        for (ProjectileEffectsValues flightEffects : inFlightEffects) {
            Validation.scope("In-flight effects", () -> flightEffects.validateExportVersion(version));
        }
    }
}
