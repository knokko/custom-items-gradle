package nl.knokko.customitems.item;

public class CustomItemDamage {
	
	public static double getDefaultAttackDamage(CustomItemType i) {
		double attackDamage;
		if (i == CustomItemType.NETHERITE_AXE) {
			attackDamage = 10;
		} else if (i == CustomItemType.DIAMOND_AXE || i == CustomItemType.IRON_AXE || i == CustomItemType.STONE_AXE){
			attackDamage = 9;
		} else if (i == CustomItemType.NETHERITE_SWORD) {
			attackDamage = 8;
		} else if (i == CustomItemType.DIAMOND_SWORD || i == CustomItemType.WOOD_AXE || i == CustomItemType.GOLD_AXE) {
			attackDamage = 7;
		} else if (i == CustomItemType.NETHERITE_SHOVEL) {
			attackDamage = 6.5;
		} else if (i == CustomItemType.IRON_SWORD || i == CustomItemType.NETHERITE_PICKAXE) {
			attackDamage = 6;
		} else if (i == CustomItemType.DIAMOND_SHOVEL) {
			attackDamage = 5.5;
		} else if (i == CustomItemType.DIAMOND_PICKAXE || i == CustomItemType.STONE_SWORD) {
			attackDamage = 5;
		} else if (i == CustomItemType.IRON_SHOVEL) {
			attackDamage = 4.5;
		} else if (i == CustomItemType.WOOD_SWORD || i == CustomItemType.GOLD_SWORD  || i == CustomItemType.IRON_PICKAXE) {
			attackDamage = 4;
		} else if (i == CustomItemType.STONE_SHOVEL) {
			attackDamage = 3.5;
		} else if (i == CustomItemType.STONE_PICKAXE) {
			attackDamage = 3;
		} else if (i == CustomItemType.WOOD_SHOVEL || i == CustomItemType.GOLD_SHOVEL) {
			attackDamage = 2.5;
		} else if (i == CustomItemType.WOOD_PICKAXE || i == CustomItemType.GOLD_PICKAXE) {
			attackDamage = 2;
		} else {
			attackDamage = 1;
		}
		return attackDamage;
	}
}