package nl.knokko.customitems.container;

import static nl.knokko.customitems.MCVersions.*;

import nl.knokko.customitems.item.VMaterial;

public enum VContainerType {

	CRAFTING_TABLE(VERSION1_12, LAST_VERSION),
	FURNACE(VERSION1_12, LAST_VERSION),
	ENCHANTING_TABLE(VERSION1_12, LAST_VERSION),
	ANVIL(VERSION1_12, LAST_VERSION),

	LOOM(VERSION1_14, LAST_VERSION),
	BLAST_FURNACE(VERSION1_14, LAST_VERSION),
	SMOKER(VERSION1_14, LAST_VERSION),
	STONE_CUTTER(VERSION1_14, LAST_VERSION),
	GRINDSTONE(VERSION1_14, LAST_VERSION),

	/**
	 * If this vanilla type is chosen, the container can only be used as pocket container
	 */
	NONE(VERSION1_12, LAST_VERSION);
	
	public static VContainerType fromMaterial(VMaterial material) {
		if (material == VMaterial.CRAFTING_TABLE ||
				material == VMaterial.WORKBENCH) {
			return CRAFTING_TABLE;
		} else if (material == VMaterial.FURNACE ||
				material == VMaterial.BURNING_FURNACE) {
			return FURNACE;
		} else if (material == VMaterial.ENCHANTING_TABLE ||
				material == VMaterial.ENCHANTMENT_TABLE) {
			return ENCHANTING_TABLE;
		} else if (material == VMaterial.ANVIL ||
				material == VMaterial.CHIPPED_ANVIL ||
				material == VMaterial.DAMAGED_ANVIL) {
			return ANVIL;
		} else if (material == VMaterial.LOOM) {
			return LOOM;
		} else if (material == VMaterial.BLAST_FURNACE) {
			return BLAST_FURNACE;
		} else if (material == VMaterial.SMOKER) {
			return SMOKER;
		} else if (material == VMaterial.STONECUTTER) {
			return STONE_CUTTER;
		} else if (material == VMaterial.GRINDSTONE) {
			return GRINDSTONE;
		} else {
			return null;
		}
	}
	
	public final int firstVersion;
	public final int lastVersion;
	
	VContainerType(int firstVersion, int lastVersion) {
		this.firstVersion = firstVersion;
		this.lastVersion = lastVersion;
	}
}
