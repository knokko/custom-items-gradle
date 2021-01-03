package nl.knokko.customitems.effect;

public class PassivePotionEffect {

	protected final EffectType effect;
	protected final int level;
	
	public PassivePotionEffect(EffectType effect, int level) {
		this.effect = effect;
		this.level = level;
	}
	
	public EffectType getEffect() {
		return effect;
	}
	
	public int getLevel() {
		return level;
	}
}
