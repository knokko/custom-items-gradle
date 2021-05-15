package nl.knokko.customitems.projectile;

import java.util.ArrayList;
import java.util.Collection;

import nl.knokko.customitems.effect.PotionEffect;
import nl.knokko.customitems.item.ItemSetBase;
import nl.knokko.customitems.projectile.effects.ProjectileEffect;
import nl.knokko.customitems.projectile.effects.ProjectileEffects;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CIProjectile {
	
	private static final byte ENCODING_1 = 0;
	private static final byte ENCODING_2 = 1;

	private static Collection<ProjectileEffect> loadImpactEffects(BitInput input) throws UnknownEncodingException {
		int numImpactEffects = input.readByte() & 0xFF;
		Collection<ProjectileEffect> impactEffects = new ArrayList<>(numImpactEffects);

		for (int counter = 0; counter < numImpactEffects; counter++)
			impactEffects.add(ProjectileEffect.fromBits(input));

		return impactEffects;
	}

	private static Collection<ProjectileEffects> loadInFlightEffects(BitInput input) throws UnknownEncodingException {
		int numFlightEffects = input.readByte() & 0xFF;
		Collection<ProjectileEffects> inFlightEffects = new ArrayList<>(numFlightEffects);

		for (int counter = 0; counter < numFlightEffects; counter++)
			inFlightEffects.add(ProjectileEffects.fromBits(input));

		return inFlightEffects;
	}

	private static Collection<PotionEffect> loadImpactPotionEffects(BitInput input) {
		int numEffects = input.readInt();
		Collection<PotionEffect> effects = new ArrayList<>(numEffects);

		for (int counter = 0; counter < numEffects; counter++) {
			effects.add(PotionEffect.load1(input));
		}

		return effects;
	}
	
	public static CIProjectile fromBits(BitInput input, ItemSetBase set) throws UnknownEncodingException {
		byte encoding = input.readByte();
		switch (encoding) {
		case ENCODING_1: return load1(input, set);
		case ENCODING_2: return load2(input, set);
		default: throw new UnknownEncodingException("Projectile", encoding);
		}
	}
	
	private static CIProjectile load1(BitInput input, ItemSetBase set) throws UnknownEncodingException {
		String name = input.readString();
		float damage = input.readFloat();

		float minLaunchAngle = input.readFloat();
		float maxLaunchAngle = input.readFloat();
		float minStartSpeed = input.readFloat();
		float maxStartSpeed = input.readFloat();

		float gravity = input.readFloat();
		int maxLifeTime = input.readInt();
		
		Collection<ProjectileEffects> inFlightEffects = loadInFlightEffects(input);
		Collection<ProjectileEffect> impactEffects = loadImpactEffects(input);
		
		String coverName = input.readString();
		ProjectileCover cover = coverName == null ? null : set.getProjectileCoverByName(coverName);
		
		return new CIProjectile(
				name, damage, minLaunchAngle, maxLaunchAngle, minStartSpeed, maxStartSpeed, gravity,
				0f, 0f, new ArrayList<>(0), maxLifeTime,
				inFlightEffects, impactEffects, cover
		);
	}

	private static CIProjectile load2(BitInput input, ItemSetBase set) throws UnknownEncodingException {
		String name = input.readString();
		float damage = input.readFloat();
		float minLaunchAngle = input.readFloat();
		float maxLaunchAngle = input.readFloat();
		float minStartSpeed = input.readFloat();
		float maxStartSpeed = input.readFloat();
		float gravity = input.readFloat();

		float launchKnockback = input.readFloat();
		float impactKnockback = input.readFloat();
		Collection<PotionEffect> impactPotionEffects = loadImpactPotionEffects(input);
		int maxLifeTime = input.readInt();

		int numFlightEffects = input.readByte() & 0xFF;
		Collection<ProjectileEffects> inFlightEffects = new ArrayList<>(numFlightEffects);
		for (int counter = 0; counter < numFlightEffects; counter++)
			inFlightEffects.add(ProjectileEffects.fromBits(input));

		int numImpactEffects = input.readByte() & 0xFF;
		Collection<ProjectileEffect> impactEffects = new ArrayList<>(numImpactEffects);
		for (int counter = 0; counter < numImpactEffects; counter++)
			impactEffects.add(ProjectileEffect.fromBits(input));

		String coverName = input.readString();
		ProjectileCover cover = coverName == null ? null : set.getProjectileCoverByName(coverName);

		return new CIProjectile(
				name, damage, minLaunchAngle, maxLaunchAngle, minStartSpeed, maxStartSpeed, gravity,
				launchKnockback, impactKnockback, impactPotionEffects, maxLifeTime,
				inFlightEffects, impactEffects, cover
		);
	}
	
	public String name;
	
	public float damage;
	
	/** 
	 * The minimum and maximum angle (in degrees) between the direction the shooter is facing and the
	 * direction the projectile will be launched.
	 */
	public float minLaunchAngle, maxLaunchAngle;
	public float minLaunchSpeed, maxLaunchSpeed;
	
	/** The gravity acceleration of the projectile, in meter/tick/tick */
	public float gravity;

	/** The magnitude of the velocity that will be given to the shooter/target, in meters/tick */
	public float launchKnockback, impactKnockback;

	/** A collection of potion effects that will be given to the entity struck by the projectile (if any) */
	public Collection<PotionEffect> impactPotionEffects;
	
	/** The maximum lifetime of this projectile, in ticks. */
	public int maxLifeTime;
	
	public Collection<ProjectileEffects> inFlightEffects;
	// Please note the 's' at the end of ProjectileEffectS above, that is intentional
	public Collection<ProjectileEffect> impactEffects;
	
	public ProjectileCover cover;

	public CIProjectile(
			String name, float damage,
			float minLaunchAngle, float maxLaunchAngle, float minLaunchSpeed, float maxLaunchSpeed, float gravity,
			float launchKnockback, float impactKnockback, Collection<PotionEffect> impactPotionEffects,
			int maxLifeTime,
			Collection<ProjectileEffects> inFlightEffects, Collection<ProjectileEffect> impactEffects,
			ProjectileCover cover
	) {
		this.name = name;
		this.damage = damage;
		this.minLaunchAngle = minLaunchAngle;
		this.maxLaunchAngle = maxLaunchAngle;
		this.minLaunchSpeed = minLaunchSpeed;
		this.maxLaunchSpeed = maxLaunchSpeed;
		this.gravity = gravity;
		this.launchKnockback = launchKnockback;
		this.impactKnockback = impactKnockback;
		this.impactPotionEffects = impactPotionEffects;
		this.maxLifeTime = maxLifeTime;
		this.inFlightEffects = inFlightEffects;
		this.impactEffects = impactEffects;
		this.cover = cover;
	}
	
	// For validation checks (and some other stuff), it is very important that the equals() method of custom 
    // projectiles only return true when `other` refers to the same object as `this`.
    @Override
    public final boolean equals(Object other) {
    	return this == other;
    }
	
	@Override
	public String toString() {
		return name;
	}

	private void saveEffects(BitOutput output) {
		output.addByte((byte) inFlightEffects.size());
		for (ProjectileEffects effects : inFlightEffects)
			effects.toBits(output);
		output.addByte((byte) impactEffects.size());
		for (ProjectileEffect effect : impactEffects)
			effect.toBits(output);
	}

	private void saveLaunchMotion(BitOutput output) {
		output.addFloats(minLaunchAngle, maxLaunchAngle, minLaunchSpeed, maxLaunchSpeed);
	}

	private void saveImpactPotionEffects(BitOutput output) {
		output.addInt(impactPotionEffects.size());
		for (PotionEffect effect : impactPotionEffects) {
			effect.save1(output);
		}
	}

	public void toBits(BitOutput output) {

		/* The first encoding
		output.addByte(ENCODING_1);
		output.addString(name);
		output.addFloats(damage, minLaunchAngle, maxLaunchAngle, minLaunchSpeed, maxLaunchSpeed, gravity);
		output.addInt(maxLifeTime);
		saveEffects(output);
		output.addString(cover == null ? null : cover.name);
		 */

		output.addByte(ENCODING_2);
		output.addString(name);
		output.addFloat(damage);
		saveLaunchMotion(output);
		output.addFloat(gravity);
		output.addFloat(launchKnockback);
		output.addFloat(impactKnockback);
		saveImpactPotionEffects(output);
		output.addInt(maxLifeTime);
		saveEffects(output);
		output.addString(cover == null ? null : cover.name);
	}
	
	public void afterProjectilesAreLoaded(ItemSetBase set) {
		for (ProjectileEffects effects : inFlightEffects)
			for (ProjectileEffect effect : effects.effects)
				effect.afterProjectilesAreLoaded(set);
		for (ProjectileEffect effect : impactEffects)
			effect.afterProjectilesAreLoaded(set);
	}
}
