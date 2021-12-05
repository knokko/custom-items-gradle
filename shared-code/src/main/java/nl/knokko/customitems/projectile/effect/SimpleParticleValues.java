package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.particle.CIParticle;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.Locale;

import static nl.knokko.customitems.util.Checks.isClose;

public class SimpleParticleValues extends ProjectileEffectValues {

    static SimpleParticleValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        SimpleParticleValues result = new SimpleParticleValues(false);

        if (encoding == ENCODING_SIMPLE_PARTICLE_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("SimpleParticleProjectileEffect", encoding);
        }

        return result;
    }

    public static SimpleParticleValues createQuick(CIParticle particle, float minRadius, float maxRadius, int amount) {
        SimpleParticleValues result = new SimpleParticleValues(true);
        result.setParticle(particle);
        result.setMinRadius(minRadius);
        result.setMaxRadius(maxRadius);
        result.setAmount(amount);
        return result;
    }

    private CIParticle particle;
    private float minRadius, maxRadius;
    private int amount;

    public SimpleParticleValues(boolean mutable) {
        super(mutable);
        this.particle = CIParticle.CRIT_MAGIC;
        this.minRadius = 0f;
        this.maxRadius = 0.5f;
        this.amount = 10;
    }

    public SimpleParticleValues(SimpleParticleValues toCopy, boolean mutable) {
        super(mutable);
        this.particle = toCopy.getParticle();
        this.minRadius = toCopy.getMinRadius();
        this.maxRadius = toCopy.getMaxRadius();
        this.amount = toCopy.getAmount();
    }

    @Override
    public String toString() {
        return "SimpleParticles(" + particle.name().toLowerCase(Locale.ROOT) + ")";
    }

    private void load1(BitInput input) {
        this.particle = CIParticle.valueOf(input.readString());
        this.minRadius = input.readFloat();
        this.maxRadius = input.readFloat();
        this.amount = input.readInt();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_SIMPLE_PARTICLE_1);
        output.addString(particle.name());
        output.addFloats(minRadius, maxRadius);
        output.addInt(amount);
    }

    @Override
    public SimpleParticleValues copy(boolean mutable) {
        return new SimpleParticleValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == SimpleParticleValues.class) {
            SimpleParticleValues otherEffect = (SimpleParticleValues) other;
            return this.particle == otherEffect.particle && isClose(this.minRadius, otherEffect.minRadius)
                    && isClose(this.maxRadius, otherEffect.maxRadius) && this.amount == otherEffect.amount;
        } else {
            return false;
        }
    }

    public CIParticle getParticle() {
        return particle;
    }

    public float getMinRadius() {
        return minRadius;
    }

    public float getMaxRadius() {
        return maxRadius;
    }

    public int getAmount() {
        return amount;
    }

    public void setParticle(CIParticle particle) {
        assertMutable();
        Checks.notNull(particle);
        this.particle = particle;
    }

    public void setMinRadius(float minRadius) {
        assertMutable();
        this.minRadius = minRadius;
    }

    public void setMaxRadius(float maxRadius) {
        assertMutable();
        this.maxRadius = maxRadius;
    }

    public void setAmount(int amount) {
        assertMutable();
        this.amount = amount;
    }

    @Override
    public void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (particle == null) throw new ProgrammingValidationException("No particle");
        if (!Float.isFinite(minRadius)) throw new ValidationException("Minimum radius must be finite");
        if (minRadius < 0f) throw new ValidationException("Minimum radius can't be negative");
        if (!Float.isFinite(maxRadius)) throw new ValidationException("Maximum radius must be finite");
        if (maxRadius < 0f) throw new ValidationException("Maximum radius can't be negative");
        if (minRadius > maxRadius) throw new ValidationException("Minimum radius can't be larger than maximum radius");
        if (amount <= 0) throw new ValidationException("Amount must be positive");
    }

    @Override
    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (version < particle.firstVersion) {
            throw new ValidationException(particle + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > particle.lastVersion) {
            throw new ValidationException(particle + " doesn't exist anymore in mc " + MCVersions.createString(version));
        }
    }
}
