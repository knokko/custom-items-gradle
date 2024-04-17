package nl.knokko.customitems.projectile;

import nl.knokko.customitems.effect.KciPotionEffect;
import nl.knokko.customitems.itemset.DamageSourceReference;
import nl.knokko.customitems.itemset.ProjectileCoverReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.projectile.cover.ProjectileCover;
import nl.knokko.customitems.projectile.effect.ProjectileEffect;
import nl.knokko.customitems.projectile.effect.ProjectileEffects;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import static nl.knokko.customitems.util.Checks.isClose;

public class KciProjectile extends ModelValues {

    private static final byte ENCODING_1 = 0;
    private static final byte ENCODING_2 = 1;
    private static final byte ENCODING_NEW = 2;

    public static KciProjectile load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        KciProjectile result = new KciProjectile(false);

        if (encoding == ENCODING_1) {
            result.load1(input, itemSet);
            result.initDefaults1();
        } else if (encoding == ENCODING_2) {
            result.load2(input, itemSet);
            result.initDefaults2();
        } else if (encoding == ENCODING_NEW) {
            result.loadNew(input, itemSet);
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

    private Collection<KciPotionEffect> impactPotionEffects;
    private int maxLifetime;
    private int maxPiercedEntities;
    private Collection<ProjectileEffects> inFlightEffects;
    private Collection<ProjectileEffect> impactEffects;
    private boolean applyImpactEffectsAtExpiration, applyImpactEffectsAtPierce;
    private DamageSourceReference customDamageSource;

    private ProjectileCoverReference cover;

    public KciProjectile(boolean mutable) {
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
        this.maxPiercedEntities = 0;
        this.inFlightEffects = new ArrayList<>();
        this.impactEffects = new ArrayList<>();
        this.applyImpactEffectsAtExpiration = false;
        this.applyImpactEffectsAtPierce = true;
        this.customDamageSource = null;
    }

    public KciProjectile(KciProjectile toCopy, boolean mutable) {
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
        this.maxPiercedEntities = toCopy.getMaxPiercedEntities();
        this.inFlightEffects = toCopy.getInFlightEffects();
        this.impactEffects = toCopy.getImpactEffects();
        this.applyImpactEffectsAtExpiration = toCopy.shouldApplyImpactEffectsAtExpiration();
        this.applyImpactEffectsAtPierce = toCopy.shouldApplyImpactEffectsAtPierce();
        this.cover = toCopy.getCoverReference();
        this.customDamageSource = toCopy.getCustomDamageSourceReference();
    }

    private void loadProjectileEffects(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        int numFlightEffects = input.readByte() & 0xFF;
        this.inFlightEffects = new ArrayList<>(numFlightEffects);
        for (int counter = 0; counter < numFlightEffects; counter++) {
            this.inFlightEffects.add(ProjectileEffects.load(input, itemSet));
        }

        int numImpactEffects = input.readByte() & 0xFF;
        this.impactEffects = new ArrayList<>(numImpactEffects);
        for (int counter = 0; counter < numImpactEffects; counter++) {
            this.impactEffects.add(ProjectileEffect.load(input, itemSet));
        }
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
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
            this.cover = itemSet.projectileCovers.getReference(coverName);
        } else {
            this.cover = null;
        }
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
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
            this.impactPotionEffects.add(KciPotionEffect.load2(input, false));
        }

        this.maxLifetime = input.readInt();
        loadProjectileEffects(input, itemSet);

        String coverName = input.readString();
        if (coverName != null) {
            this.cover = itemSet.projectileCovers.getReference(coverName);
        } else {
            this.cover = null;
        }
    }

    private void loadNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("CustomProjectileNew", encoding);

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
            this.impactPotionEffects.add(KciPotionEffect.load2(input, false));
        }

        this.maxLifetime = input.readInt();
        loadProjectileEffects(input, itemSet);

        String coverName = input.readString();
        if (coverName != null) {
            this.cover = itemSet.projectileCovers.getReference(coverName);
        } else {
            this.cover = null;
        }
        if (input.readBoolean()) {
            this.customDamageSource = itemSet.damageSources.getReference(new UUID(input.readLong(), input.readLong()));
        } else this.customDamageSource = null;

        if (encoding > 1) {
            this.maxPiercedEntities = input.readInt();
            this.applyImpactEffectsAtExpiration = input.readBoolean();
            this.applyImpactEffectsAtPierce = input.readBoolean();
        } else {
            this.maxPiercedEntities = 0;
            this.applyImpactEffectsAtExpiration = false;
            this.applyImpactEffectsAtPierce = true;
        }
    }

    private void initDefaults1() {
        this.launchKnockback = 0f;
        this.impactKnockback = 0f;
        this.impactPotionEffects = new ArrayList<>(0);
        initDefaults2();
    }

    private void initDefaults2() {
        this.customDamageSource = null;
    }

    public void save(BitOutput output) {
        output.addByte(ENCODING_NEW);
        output.addByte((byte) 2);
        output.addString(name);
        output.addFloats(damage, minLaunchAngle, maxLaunchAngle, minLaunchSpeed, maxLaunchSpeed, gravity, launchKnockback, impactKnockback);

        output.addInt(impactPotionEffects.size());
        for (KciPotionEffect effect : impactPotionEffects) {
            effect.save2(output);
        }

        output.addInt(maxLifetime);

        output.addByte((byte) inFlightEffects.size());
        for (ProjectileEffects effects : inFlightEffects) {
            effects.save(output);
        }
        output.addByte((byte) impactEffects.size());
        for (ProjectileEffect effect : impactEffects) {
            effect.save(output);
        }
        output.addString(cover == null ? null : cover.get().getName());
        output.addBoolean(customDamageSource != null);
        if (customDamageSource != null) {
            output.addLong(customDamageSource.get().getId().getMostSignificantBits());
            output.addLong(customDamageSource.get().getId().getLeastSignificantBits());
        }
        output.addInt(maxPiercedEntities);
        output.addBoolean(applyImpactEffectsAtExpiration);
        output.addBoolean(applyImpactEffectsAtPierce);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == KciProjectile.class) {
            KciProjectile otherProjectile = (KciProjectile) other;
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
                    && this.impactEffects.equals(otherProjectile.impactEffects)
                    && this.applyImpactEffectsAtExpiration == otherProjectile.applyImpactEffectsAtExpiration
                    && this.applyImpactEffectsAtPierce == otherProjectile.applyImpactEffectsAtPierce
                    && this.maxPiercedEntities == otherProjectile.maxPiercedEntities
                    && Objects.equals(this.customDamageSource, otherProjectile.customDamageSource);
        } else {
            return false;
        }
    }

    @Override
    public KciProjectile copy(boolean mutable) {
        return new KciProjectile(this, mutable);
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

    public Collection<KciPotionEffect> getImpactPotionEffects() {
        return new ArrayList<>(impactPotionEffects);
    }

    public int getMaxLifetime() {
        return maxLifetime;
    }

    public int getMaxPiercedEntities() {
        return maxPiercedEntities;
    }

    public Collection<ProjectileEffects> getInFlightEffects() {
        return new ArrayList<>(inFlightEffects);
    }

    public Collection<ProjectileEffect> getImpactEffects() {
        return new ArrayList<>(impactEffects);
    }

    public boolean shouldApplyImpactEffectsAtExpiration() {
        return applyImpactEffectsAtExpiration;
    }

    public boolean shouldApplyImpactEffectsAtPierce() {
        return applyImpactEffectsAtPierce;
    }

    public ProjectileCoverReference getCoverReference() {
        return cover;
    }

    public ProjectileCover getCover() {
        return cover == null ? null : cover.get();
    }

    public DamageSourceReference getCustomDamageSourceReference() {
        return customDamageSource;
    }

    public void setName(String newName) {
        assertMutable();
        Checks.notNull(newName);
        this.name = newName;
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

    public void setImpactPotionEffects(Collection<KciPotionEffect> newImpactPotionEffects) {
        assertMutable();
        Checks.nonNull(newImpactPotionEffects);
        this.impactPotionEffects = Mutability.createDeepCopy(newImpactPotionEffects, false);
    }

    public void setMaxLifetime(int newLifetime) {
        assertMutable();
        this.maxLifetime = newLifetime;
    }

    public void setMaxPiercedEntities(int maxPiercedEntities) {
        assertMutable();
        this.maxPiercedEntities = maxPiercedEntities;
    }

    public void setInFlightEffects(Collection<ProjectileEffects> newFlightEffects) {
        assertMutable();
        Checks.nonNull(newFlightEffects);
        this.inFlightEffects = Mutability.createDeepCopy(newFlightEffects, false);
    }

    public void setImpactEffects(Collection<ProjectileEffect> newImpactEffects) {
        assertMutable();
        Checks.nonNull(newImpactEffects);
        this.impactEffects = Mutability.createDeepCopy(newImpactEffects, false);
    }

    public void setApplyImpactEffectsAtExpiration(boolean apply) {
        assertMutable();
        this.applyImpactEffectsAtExpiration = apply;
    }

    public void setApplyImpactEffectsAtPierce(boolean applyImpactEffectsAtPierce) {
        assertMutable();
        this.applyImpactEffectsAtPierce = applyImpactEffectsAtPierce;
    }

    public void setCover(ProjectileCoverReference newCover) {
        assertMutable();
        this.cover = newCover;
    }

    public void setCustomDamageSource(DamageSourceReference customDamageSource) {
        assertMutable();
        this.customDamageSource = customDamageSource;
    }

    public void validate(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("You need to choose a name");
        if (!name.equals(oldName) && itemSet.projectiles.get(name).isPresent())
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
        for (KciPotionEffect impactEffect : impactPotionEffects) {
            if (impactEffect == null) throw new ProgrammingValidationException("Missing an impact potion effect");
            Validation.scope(
                    "Impact potion effect", impactEffect::validate
            );
        }

        if (maxLifetime <= 0) throw new ValidationException("Maximum lifetime must be positive");
        if (maxPiercedEntities < 0) throw new ValidationException("Maximum pierced entities can't be negative");

        if (inFlightEffects == null) throw new ProgrammingValidationException("No in-flight effects");
        if (inFlightEffects.size() > Byte.MAX_VALUE) throw new ValidationException("Too many in-flight effects");
        for (ProjectileEffects effects : inFlightEffects) {
            if (effects == null) throw new ProgrammingValidationException("Missing an in-flight effect save");
            Validation.scope(
                    "In-flight effect", () -> effects.validate(itemSet)
            );
        }

        if (impactEffects == null) throw new ProgrammingValidationException("No impact effects");
        if (impactEffects.size() > Byte.MAX_VALUE) throw new ValidationException("Too many impact effects");
        for (ProjectileEffect effect : impactEffects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an impact effect");
            Validation.scope(
                    "Impact effect", () -> effect.validate(itemSet)
            );
        }

        if (cover != null && !itemSet.projectileCovers.isValid(cover))
            throw new ProgrammingValidationException("Projectile cover is no longer valid");

        if (customDamageSource != null && !itemSet.damageSources.isValid(customDamageSource))
            throw new ProgrammingValidationException("Invalid custom damage source");
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        for (KciPotionEffect impactEffect : impactPotionEffects) {
            Validation.scope("Impact potion effects", () -> impactEffect.validateExportVersion(version));
        }
        for (ProjectileEffect impactEffect : impactEffects) {
            Validation.scope("Impact effects", () -> impactEffect.validateExportVersion(version));
        }
        for (ProjectileEffects flightEffects : inFlightEffects) {
            Validation.scope("In-flight effects", () -> flightEffects.validateExportVersion(version));
        }
    }
}
