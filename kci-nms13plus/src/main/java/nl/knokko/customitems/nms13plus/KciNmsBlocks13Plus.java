package nl.knokko.customitems.nms13plus;

import nl.knokko.customitems.nms.KciNmsBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;

public class KciNmsBlocks13Plus implements KciNmsBlocks {

    @Override
    public boolean areEnabled() {
        return true;
    }

    @Override
    public void place(Block destination, boolean[] directions, String materialName) {
        MultipleFacing mushroomData = (MultipleFacing) Bukkit.createBlockData(Material.valueOf(materialName));
        mushroomData.setFace(BlockFace.DOWN, directions[0]);
        mushroomData.setFace(BlockFace.EAST, directions[1]);
        mushroomData.setFace(BlockFace.NORTH, directions[2]);
        mushroomData.setFace(BlockFace.SOUTH, directions[3]);
        mushroomData.setFace(BlockFace.UP, directions[4]);
        mushroomData.setFace(BlockFace.WEST, directions[5]);
        destination.setBlockData(mushroomData);
    }

    @Override
    public boolean[] getDirections(Block toCheck) {
        MultipleFacing mushroomData = (MultipleFacing) toCheck.getBlockData();
        boolean[] result = {
                mushroomData.hasFace(BlockFace.DOWN),
                mushroomData.hasFace(BlockFace.EAST),
                mushroomData.hasFace(BlockFace.NORTH),
                mushroomData.hasFace(BlockFace.SOUTH),
                mushroomData.hasFace(BlockFace.UP),
                mushroomData.hasFace(BlockFace.WEST),
        };
        return result;
    }
}
