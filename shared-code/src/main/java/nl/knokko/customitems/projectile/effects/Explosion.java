package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class Explosion extends ProjectileEffect {
	
	static Explosion load1(BitInput input) {
		return new Explosion(input.readFloat(), input.readBoolean(), input.readBoolean());
	}
	
	public float power;
	
	public boolean destroyBlocks;
	public boolean setFire;

	public Explosion(float power, boolean destroyBlocks, boolean setFire) {
		this.power = power;
		this.destroyBlocks = destroyBlocks;
		this.setFire = setFire;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Explosion) {
			Explosion explosion = (Explosion) other;
			return power == explosion.power && destroyBlocks == explosion.destroyBlocks && setFire == explosion.setFire;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Explosion(power=" + power + ")";
	}

	@Override
	public void toBits(BitOutput output) {
		output.addByte(ENCODING_EXPLOSION_1);
		output.addFloat(power);
		output.addBoolean(destroyBlocks);
		output.addBoolean(setFire);
	}

	@Override
	public String validate() {
		if (!(power >= 0))
			return "The explosion power can't be negative";
		return null;
	}
}
