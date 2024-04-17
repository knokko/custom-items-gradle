package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.particle.VParticle;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Locale;

import static nl.knokko.customitems.util.Checks.isClose;

public class PESimpleParticle extends ProjectileEffect {

    static PESimpleParticle load(BitInput input, byte encoding) throws UnknownEncodingException {
        PESimpleParticle result = new PESimpleParticle(false);

        if (encoding == ENCODING_SIMPLE_PARTICLE_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("SimpleParticleProjectileEffect", encoding);
        }

        return result;
    }

    public static PESimpleParticle createQuick(VParticle particle, float minRadius, float maxRadius, int amount) {
        PESimpleParticle result = new PESimpleParticle(true);
        result.setParticle(particle);
        result.setMinRadius(minRadius);
        result.setMaxRadius(maxRadius);
        result.setAmount(amount);
        return result;
    }

    private VParticle particle;
    private float minRadius, maxRadius;
    private int amount;

    public PESimpleParticle(boolean mutable) {
        super(mutable);
        this.particle = VParticle.CRIT_MAGIC;
        this.minRadius = 0f;
        this.maxRadius = 0.5f;
        this.amount = 10;
    }

    public PESimpleParticle(PESimpleParticle toCopy, boolean mutable) {
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
        this.particle = VParticle.valueOf(input.readString());
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
    public PESimpleParticle copy(boolean mutable) {
        return new PESimpleParticle(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == PESimpleParticle.class) {
            PESimpleParticle otherEffect = (PESimpleParticle) other;
            return this.particle == otherEffect.particle && isClose(this.minRadius, otherEffect.minRadius)
                    && isClose(this.maxRadius, otherEffect.maxRadius) && this.amount == otherEffect.amount;
        } else {
            return false;
        }
    }

    public VParticle getParticle() {
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

    public void setParticle(VParticle particle) {
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
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
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
