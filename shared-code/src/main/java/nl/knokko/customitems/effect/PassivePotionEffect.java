package nl.knokko.customitems.effect;

public class PassivePotionEffect {

	protected final EffectType effect;
	protected final int level;
	
	public PassivePotionEffect(EffectType effect, int level) {
		this.effect = effect;
		this.level = level;
	}

	@Override
	public String toString() {
		return effect + " level " + level;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof PassivePotionEffect) {
			PassivePotionEffect otherEffect = (PassivePotionEffect) other;
			return effect == otherEffect.effect && level == otherEffect.level;
		} else {
			return false;
		}
	}
	
	public EffectType getEffect() {
		return effect;
	}
	
	public int getLevel() {
		return level;
	}
}
