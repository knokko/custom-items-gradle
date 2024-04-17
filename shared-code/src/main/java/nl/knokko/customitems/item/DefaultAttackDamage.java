package nl.knokko.customitems.item;

public class DefaultAttackDamage {
	
	public static double get(KciItemType i) {
		double attackDamage;
		if (i == KciItemType.NETHERITE_AXE) {
			attackDamage = 10;
		} else if (i == KciItemType.DIAMOND_AXE || i == KciItemType.IRON_AXE || i == KciItemType.STONE_AXE){
			attackDamage = 9;
		} else if (i == KciItemType.NETHERITE_SWORD) {
			attackDamage = 8;
		} else if (i == KciItemType.DIAMOND_SWORD || i == KciItemType.WOOD_AXE || i == KciItemType.GOLD_AXE) {
			attackDamage = 7;
		} else if (i == KciItemType.NETHERITE_SHOVEL) {
			attackDamage = 6.5;
		} else if (i == KciItemType.IRON_SWORD || i == KciItemType.NETHERITE_PICKAXE) {
			attackDamage = 6;
		} else if (i == KciItemType.DIAMOND_SHOVEL) {
			attackDamage = 5.5;
		} else if (i == KciItemType.DIAMOND_PICKAXE || i == KciItemType.STONE_SWORD) {
			attackDamage = 5;
		} else if (i == KciItemType.IRON_SHOVEL) {
			attackDamage = 4.5;
		} else if (i == KciItemType.WOOD_SWORD || i == KciItemType.GOLD_SWORD  || i == KciItemType.IRON_PICKAXE) {
			attackDamage = 4;
		} else if (i == KciItemType.STONE_SHOVEL) {
			attackDamage = 3.5;
		} else if (i == KciItemType.STONE_PICKAXE) {
			attackDamage = 3;
		} else if (i == KciItemType.WOOD_SHOVEL || i == KciItemType.GOLD_SHOVEL) {
			attackDamage = 2.5;
		} else if (i == KciItemType.WOOD_PICKAXE || i == KciItemType.GOLD_PICKAXE) {
			attackDamage = 2;
		} else {
			attackDamage = 1;
		}
		return attackDamage;
	}
}