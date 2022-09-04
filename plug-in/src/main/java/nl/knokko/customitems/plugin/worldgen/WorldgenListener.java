package nl.knokko.customitems.plugin.worldgen;

import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldgenListener implements Listener {

    private final ItemSetWrapper itemSet;

    public WorldgenListener(ItemSetWrapper itemSet) {
        this.itemSet = itemSet;
    }

    @EventHandler
    public void registerBlockPopulators(WorldInitEvent event) {
        event.getWorld().getPopulators().add(new KciPopulator(itemSet));
    }
}
