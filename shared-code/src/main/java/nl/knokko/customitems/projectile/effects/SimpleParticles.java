package nl.knokko.customitems.projectile.effects;

import nl.knokko.customitems.particle.CIParticle;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SimpleParticles extends ProjectileEffect {
	
	static SimpleParticles load1(BitInput input) {
		return new SimpleParticles(CIParticle.valueOf(input.readString()), 
				input.readFloat(), input.readFloat(), input.readInt());
	}
	
	public CIParticle particle;
	
	public float minRadius, maxRadius;
	
	public int amount;

	public SimpleParticles(CIParticle particle, float minRadius, float maxRadius, int amount) {
		this.particle = particle;
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		return particle + " particles";
	}

	@Override
	public void toBits(BitOutput output) {
		output.addByte(ENCODING_SIMPLE_PARTICLE_1);
		output.addString(particle.name());
		output.addFloats(minRadius, maxRadius);
		output.addInt(amount);
	}

	@Override
	public String validate() {
		if (particle == null)
			return "You must select a particle";
		if (!(minRadius >= 0))
			return "The minimum radius can't be negative";
		if (!(maxRadius >= 0))
			return "The maximum radius can't be negative";
		if (minRadius > maxRadius)
			return "The minimum radius can't be larger than the maximum radius";
		if (amount <= 0)
			return "The amount must be a positive integer";
		return null;
	}
}
