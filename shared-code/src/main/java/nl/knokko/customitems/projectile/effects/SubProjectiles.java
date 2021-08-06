package nl.knokko.customitems.projectile.effects;

import nl.knokko.customitems.item.ItemSetBase;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class SubProjectiles extends ProjectileEffect {
	
	static SubProjectiles load1(BitInput input) {
		String childName = input.readString();
		boolean useParentLifeTime = input.readBoolean();
		int minAmount = input.readInt();
		int maxAmount = input.readInt();
		float angleToParent = input.readFloat();
		SubProjectiles sub = new SubProjectiles(null, useParentLifeTime, minAmount, maxAmount, angleToParent);
		sub.childName = childName;
		return sub;
	}
	
	public CIProjectile child;
	
	/** A bit of a hack to be able to find the child projectile */
	private String childName;
	
	public boolean useParentLifeTime;
	
	public int minAmount, maxAmount;
	
	public float angleToParent;

	public SubProjectiles(CIProjectile child, boolean useParentLifeTime, 
			int minAmount, int maxAmount, float angleToParent) {
		this.child = child;
		this.useParentLifeTime = useParentLifeTime;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.angleToParent = angleToParent;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SubProjectiles) {
			SubProjectiles sub = (SubProjectiles) other;
			return child == sub.child && useParentLifeTime == sub.useParentLifeTime && minAmount == sub.minAmount
					&& maxAmount == sub.maxAmount && angleToParent == sub.angleToParent;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Launch " + child.name;
	}

	@Override
	public void toBits(BitOutput output) {
		output.addByte(ENCODING_SUB_PROJECTILE_1);
		output.addString(child.name);
		output.addBoolean(useParentLifeTime);
		output.addInts(minAmount, maxAmount);
		output.addFloat(angleToParent);
	}

	@Override
	public String validate() {
		if (child == null)
			return "You must select a child projectile";
		if (childName != null)
			return "The childName should be null at this point";
		if (minAmount < 0)
			return "The minimum amount can't be negative";
		if (maxAmount <= 0)
			return "The maximum amount must be positive";
		if (minAmount > maxAmount)
			return "The minimum amount can't be greater than the maximum amount";
		if (angleToParent < 0)
			return "The angle to parent can't be negative";
		if (angleToParent > 180)
			return "The angle to parent can't be greater than 180 degrees";
		return null;
	}
	
	@Override
	public void afterProjectilesAreLoaded(ItemSetBase set) {
		child = set.getProjectileByName(childName);
		childName = null;
	}
}
