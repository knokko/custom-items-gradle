package nl.knokko.customrecipes.furnace;

import be.seeseemelk.mockbukkit.block.state.AbstractFurnaceMock;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class FurnaceMock extends AbstractFurnaceMock {
    public FurnaceMock(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull BlockState getSnapshot() {
        return this;
    }
}
