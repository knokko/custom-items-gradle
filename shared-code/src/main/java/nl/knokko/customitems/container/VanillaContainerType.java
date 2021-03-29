package nl.knokko.customitems.container;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.item.CIMaterial;

public enum VanillaContainerType {

	CRAFTING_TABLE(VERSION1_12, VERSION1_16),
	FURNACE(VERSION1_12, VERSION1_16),
	ENCHANTING_TABLE(VERSION1_12, VERSION1_16),
	ANVIL(VERSION1_12, VERSION1_16),

	LOOM(VERSION1_14, VERSION1_16),
	BLAST_FURNACE(VERSION1_14, VERSION1_16),
	SMOKER(VERSION1_14, VERSION1_16),
	STONE_CUTTER(VERSION1_14, VERSION1_16),
	GRINDSTONE(VERSION1_14, VERSION1_16),

	/**
	 * If this vanilla type is chosen, the container can only be used as pocket container
	 */
	NONE(VERSION1_12, VERSION1_16);
	
	public static VanillaContainerType fromMaterial(CIMaterial material) {
		if (material == CIMaterial.CRAFTING_TABLE || 
				material == CIMaterial.WORKBENCH) {
			return CRAFTING_TABLE;
		} else if (material == CIMaterial.FURNACE || 
				material == CIMaterial.BURNING_FURNACE) {
			return FURNACE;
		} else if (material == CIMaterial.ENCHANTING_TABLE || 
				material == CIMaterial.ENCHANTMENT_TABLE) {
			return ENCHANTING_TABLE;
		} else if (material == CIMaterial.ANVIL || 
				material == CIMaterial.CHIPPED_ANVIL || 
				material == CIMaterial.DAMAGED_ANVIL) {
			return ANVIL;
		} else if (material == CIMaterial.LOOM) {
			return LOOM;
		} else if (material == CIMaterial.BLAST_FURNACE) {
			return BLAST_FURNACE;
		} else if (material == CIMaterial.SMOKER) {
			return SMOKER;
		} else if (material == CIMaterial.STONECUTTER) {
			return STONE_CUTTER;
		} else if (material == CIMaterial.GRINDSTONE) {
			return GRINDSTONE;
		} else {
			return null;
		}
	}
	
	public final int firstVersion;
	public final int lastVersion;
	
	VanillaContainerType(int firstVersion, int lastVersion) {
		this.firstVersion = firstVersion;
		this.lastVersion = lastVersion;
	}
}
