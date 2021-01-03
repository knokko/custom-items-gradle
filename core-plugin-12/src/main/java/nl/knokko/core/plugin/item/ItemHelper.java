/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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