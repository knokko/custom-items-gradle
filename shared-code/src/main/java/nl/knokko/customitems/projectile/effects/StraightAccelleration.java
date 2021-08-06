package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitInput;

public class StraightAccelleration extends ProjectileAccelleration {
	
	static StraightAccelleration load1(BitInput input) {
		return new StraightAccelleration(input.readFloat(), input.readFloat());
	}
	
	public StraightAccelleration(float minAccelleration, float maxAccelleration) {
		super(minAccelleration, maxAccelleration);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof StraightAccelleration) {
			StraightAccelleration sa = (StraightAccelleration) other;
			return minAccelleration == sa.minAccelleration && maxAccelleration == sa.maxAccelleration;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Straight accelleration";
	}

	@Override
	protected byte getEncoding() {
		return ENCODING_STRAIGHT_ACCELLERATION_1;
	}
}
