package nl.knokko.customitems.projectile;

import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.util.bits.BitInput;

public class ProjectileCover {
	
	public CustomItemType itemType;
	public short itemDamage;
	
	public String name;

	public ProjectileCover(CustomItemType itemType, short itemDamage, String name) {
		this.itemType = itemType;
		this.itemDamage = itemDamage;
		this.name = name;
	}
	
	public ProjectileCover(BitInput input) {
		itemType = CustomItemType.valueOf(input.readString());
		itemDamage = input.readShort();
		name = input.readString();
	}
}
