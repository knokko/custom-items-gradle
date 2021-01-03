package nl.knokko.customitems.item;

import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.customitems.projectile.ProjectileCover;

public interface ItemSetBase {
	
	CustomItem getCustomItemByName(String name);
	
	CIProjectile getProjectileByName(String name);
	
	ProjectileCover getProjectileCoverByName(String name);
	
	// Simple hash, doesn't have to be cryptographically strong
	default long hash(byte[] content) {
		long result = 0;
		for (byte b : content) {
			int i = b + 129;
			result += i;
			result += i * b;
		}
		return result;
	}
}
