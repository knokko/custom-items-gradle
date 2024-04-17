package nl.knokko.customitems.plugin.worldgen;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.worldgen.BlockProducer;
import nl.knokko.customitems.worldgen.VTreeType;
import nl.knokko.customitems.worldgen.ProducedBlock;
import nl.knokko.customitems.worldgen.TreeGenerator;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static nl.knokko.customitems.plugin.worldgen.KciPopulator.placeBlock;

public class TreeChangeDelegate implements BlockChangeDelegate {

    private final TreeGenerator generator;
    private final World world;
    private final Random random;

    public TreeChangeDelegate(TreeGenerator generator, World world, Random random) {
        this.generator = generator;
        this.world = world;
        this.random = random;
    }

    @Override
    public boolean setBlockData(int x, int y, int z, @NotNull BlockData blockData) {
        // Spigot wants to place dirt below all trees on unnatural terrain, but we don't
        if (blockData.getMaterial() == Material.DIRT) return true;

        Block block = world.getBlockAt(x, y, z);

        boolean isLeaves = blockData.getMaterial().name().contains("LEAVES");
        BlockProducer blockProducer = isLeaves ? generator.getLeavesMaterial() : generator.getLogMaterial();
        ProducedBlock producedBlock = blockProducer.produce(random);

        // Jungle trees have vines that I should ignore
        if (generator.getTreeType() == VTreeType.JUNGLE || generator.getTreeType() == VTreeType.SMALL_JUNGLE) {
            boolean isLog = blockData.getMaterial().name().contains("LOG");
            if (!isLeaves && !isLog) producedBlock = new ProducedBlock(VMaterial.AIR);
        }

        placeBlock(block, producedBlock);

        return true;
    }

    @NotNull
    @Override
    public BlockData getBlockData(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getBlockData();
    }

    @Override
    public int getHeight() {
        return world.getMaxHeight();
    }

    @Override
    public boolean isEmpty(int x, int y, int z) {
        return world.getBlockAt(x, y, z).isEmpty();
    }
}
