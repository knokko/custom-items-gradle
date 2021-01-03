package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitOutput;

public abstract class ProjectileAccelleration extends ProjectileEffect {

	public float minAccelleration, maxAccelleration;

	public ProjectileAccelleration(float minAccelleration, float maxAccelleration) {
		this.minAccelleration = minAccelleration;
		this.maxAccelleration = maxAccelleration;
	}

	@Override
	public void toBits(BitOutput output) {
		output.addByte(getEncoding());
		output.addFloats(minAccelleration, maxAccelleration);
	}

	@Override
	public String validate() {
		if (!Float.isFinite(minAccelleration))
			return "The minimum accelleration must be finite";
		if (!Float.isFinite(maxAccelleration))
			return "The maximum accelleration musts be finite";
		if (minAccelleration > maxAccelleration)
			return "The minimum accelleration can't be greater than the maximum accelleration";
		return null;
	}
	
	protected abstract byte getEncoding();
}
