package nl.knokko.customitems.projectile.effects;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

/**
 * Represents a collection of ProjectileEffect's that will all be applied at the same moments (with the same
 * delay and period).
 * 
 * `period` ticks after a projectile with these ProjectileEffects is launched, all `effects` will be applied
 * for the first time. Thereafter, every `period` ticks (if the projectile is still in flight), all `effects` 
 * will be applied again.
 * @author knokko
 *
 */
public class ProjectileEffects {
	
	private static final byte ENCODING_1 = 0;
	
	public static ProjectileEffects fromBits(BitInput input) throws UnknownEncodingException {
		byte encoding = input.readByte();
		switch (encoding) {
		case ENCODING_1: {
			int delay = input.readInt();
			int period = input.readInt();
			int numEffects = input.readByte() & 0xFF;
			Collection<ProjectileEffect> effects = new ArrayList<>(numEffects);
			for (int counter = 0; counter < numEffects; counter++)
				effects.add(ProjectileEffect.fromBits(input));
			return new ProjectileEffects(delay, period, effects);
		}
		default: throw new UnknownEncodingException("ProjectileEffects", encoding);
		}
	}
	
	public int delay;
	public int period;
	
	public Collection<ProjectileEffect> effects;

	public ProjectileEffects(int delay, int period, Collection<ProjectileEffect> effects) {
		this.delay = delay;
		this.period = period;
		this.effects = effects;
	}
	
	@Override
	public String toString() {
		return effects.size() + " projectile effects with period " + period;
	}
	
	public String validate() {
		if (delay < 0)
			return "The time until first round must be a positive integer";
		if (period <= 0)
			return "The time between rounds must be a positive integer";
		if (effects == null)
			return "The effects per round can't be null";
		if (effects.isEmpty())
			return "There should be at least 1 effect per round";
		return null;
	}
	
	public void toBits(BitOutput output) {
		output.addByte(ENCODING_1);
		output.addInt(delay);
		output.addInt(period);
		output.addByte((byte) effects.size());
		for (ProjectileEffect effect : effects) {
			effect.toBits(output);
		}
	}
}
