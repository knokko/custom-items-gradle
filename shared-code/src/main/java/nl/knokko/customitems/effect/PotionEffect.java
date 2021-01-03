package nl.knokko.customitems.effect;

public class PotionEffect {
	
	protected final int duration;
	protected final int level;
	protected final EffectType effect;
	
	public PotionEffect(EffectType effect, int duration, int level) {
		this.effect = effect;
		this.duration = duration;
		this.level = level;
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
}
