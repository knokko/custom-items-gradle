package nl.knokko.customitems.effect;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class PotionEffect {

	public static PotionEffect load1(BitInput input) {
		return new PotionEffect(EffectType.valueOf(input.readString()), input.readInt(), input.readInt());
	}

	protected final int duration;
	protected final int level;
	protected final EffectType effect;
	
	public PotionEffect(EffectType effect, int duration, int level) {
		this.effect = effect;
		this.duration = duration;
		this.level = level;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof PotionEffect) {
			PotionEffect potion = (PotionEffect) other;
			return duration == potion.duration && level == potion.level && effect == potion.effect;
		} else {
			return false;
		}
	}
	
	public EffectType getEffect () {
		return this.effect;
	}
	
	public int getDuration () {
		return this.duration;
	}
	
	public int getLevel () {
		return this.level;
	}

	public String validate() {
		if (effect == null) return "You need to choose an effect type";
		if (duration <= 0) return "The duration must be positive";
		if (level <= 0) return "The level must be positive";
		return null;
	}

	public void save1(BitOutput output) {
		output.addString(effect.name());
		output.addInt(duration);
		output.addInt(level);
	}
}
