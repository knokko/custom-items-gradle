package nl.knokko.core.plugin.item;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemHelper {
	
	public static String getStackName(ItemStack stack) {
		net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
		return nms.getName();
	}
	
	public static String getTagAsString(ItemStack stack) {
		net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
		if (nms.hasTag()) {
			return nms.getTag().toString();
		} else {
			return "No NBT";
		}
	}
	
	public static String getMaterialName(Block block) {
		return block.getType().name();
	}
	
	public static void setMaterial(ItemStack stack, String newMaterialName) {
		stack.setType(Material.getMaterial(newMaterialName));
	}
	
	public static boolean isMaterialSolid(Block block) {
		return block.getType().isSolid();
	}
	
	public static String getMaterialName(ItemStack stack) {
		return stack.getType().name();
	}
	
	public static ItemStack createStack(String materialName, int amount) throws UnknownMaterialException {
		Material material = Material.getMaterial(materialName);
		if (material == null)
			throw new UnknownMaterialException(materialName);
		return new ItemStack(material, amount);
	}
}