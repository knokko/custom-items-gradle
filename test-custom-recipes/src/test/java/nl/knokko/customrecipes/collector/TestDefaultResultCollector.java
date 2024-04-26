package nl.knokko.customrecipes.collector;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestDefaultResultCollector {

    @Test
    public void testShiftClickEnoughSpace() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());

        Inventory inventory = server.createInventory(null, 9);
        inventory.setItem(0, new ItemStack(Material.STONE));
        // empty slot
        inventory.setItem(2, new ItemStack(Material.COAL, 60));
        inventory.setItem(3, new ItemStack(Material.STICK));
        inventory.setItem(4, new ItemStack(Material.BIRCH_BOAT));
        inventory.setItem(5, new ItemStack(Material.COAL, 62));
        inventory.setItem(6, new ItemStack(Material.STICK));
        // 2 empty slots

        ResultCollectorEvent event = new ResultCollectorEvent(
                new ItemStack(Material.COAL, 9), 10, inventory,
                null, null, InventoryAction.MOVE_TO_OTHER_INVENTORY
        );
        new DefaultResultCollector().accept(event);

        assertEquals(10, event.actualProductionCount);
        assertEquals(new ItemStack(Material.COAL, 64), inventory.getItem(1));
        assertEquals(new ItemStack(Material.COAL, 64), inventory.getItem(2));
        assertEquals(new ItemStack(Material.COAL, 64), inventory.getItem(5));
        assertEquals(new ItemStack(Material.COAL, 20), inventory.getItem(7));
        assertNull(inventory.getItem(8));
    }

    @Test
    public void testShiftClickNotEnoughSpace() {
        ServerMock server = Objects.requireNonNull(MockBukkit.getOrCreateMock());

        Inventory inventory = server.createInventory(null, 9);
        inventory.setItem(0, new ItemStack(Material.STONE));
        // empty slot
        inventory.setItem(2, new ItemStack(Material.COAL, 60));
        inventory.setItem(3, new ItemStack(Material.STICK));
        inventory.setItem(4, new ItemStack(Material.BIRCH_BOAT));
        inventory.setItem(5, new ItemStack(Material.COAL, 50));
        inventory.setItem(6, new ItemStack(Material.STICK));
        inventory.setItem(7, new ItemStack(Material.STICK));
        inventory.setItem(8, new ItemStack(Material.BIRCH_BOAT));

        ResultCollectorEvent event = new ResultCollectorEvent(
                new ItemStack(Material.COAL, 9), 10, inventory,
                null, null, InventoryAction.MOVE_TO_OTHER_INVENTORY
        );
        new DefaultResultCollector().accept(event);

        assertEquals(9, event.actualProductionCount);
        assertEquals(new ItemStack(Material.COAL, 63), inventory.getItem(1));
        assertEquals(new ItemStack(Material.COAL, 64), inventory.getItem(2));
        assertEquals(new ItemStack(Material.COAL, 64), inventory.getItem(5));
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
