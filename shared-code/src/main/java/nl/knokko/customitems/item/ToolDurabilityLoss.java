package nl.knokko.customitems.item;

import nl.knokko.customitems.item.KciItemType.Category;

public class ToolDurabilityLoss {
	
	public static int defaultEntityHitDurabilityLoss(KciItemType itemType) {
		Category toolCategory = itemType.getMainCategory();
		if (toolCategory == Category.SWORD || toolCategory == Category.TRIDENT) return 1;
		else if (toolCategory == Category.PICKAXE || toolCategory == Category.AXE
				|| toolCategory == Category.SHOVEL) return 2;
		return 0;
	}
	
	public static int defaultBlockBreakDurabilityLoss(KciItemType itemType) {
		Category toolCategory = itemType.getMainCategory();
		if (toolCategory == Category.SWORD || toolCategory == Category.TRIDENT) return 2;
		else if (toolCategory == Category.PICKAXE || toolCategory == Category.AXE
				|| toolCategory == Category.SHOVEL || toolCategory == Category.SHEAR) return 1;
		return 0;
	}
}