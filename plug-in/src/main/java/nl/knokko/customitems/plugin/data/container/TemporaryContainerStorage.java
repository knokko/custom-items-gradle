package nl.knokko.customitems.plugin.data.container;

import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class TemporaryContainerStorage {

    private final Collection<TemporaryContainerInstance> tempContainers;

    public TemporaryContainerStorage() {
        this.tempContainers = new ArrayList<>();
    }

    public ContainerInstance add(ContainerInstance instance, Player viewer) {
        TemporaryContainerInstance tempContainer = new TemporaryContainerInstance(instance, viewer);
        tempContainers.add(tempContainer);
        return tempContainer.instance;
    }

    public void update() {
        tempContainers.forEach(tempContainer -> tempContainer.instance.update());
    }

    public ContainerInstance getViewedContainer(Player viewer) {
        for (TemporaryContainerInstance temp : tempContainers) {
            if (temp.instance.getInventory().getViewers().contains(viewer)) {
                return temp.instance;
            }
        }
        return null;
    }

    public void clean() {
        Iterator<TemporaryContainerInstance> tempIterator = tempContainers.iterator();
        ItemSetWrapper itemSet = CustomItemsPlugin.getInstance().getSet();
        while (tempIterator.hasNext()) {
            TemporaryContainerInstance tempInstance = tempIterator.next();
            if (!tempInstance.viewer.getOpenInventory().getTopInventory().equals(tempInstance.instance.getInventory())) {
                tempIterator.remove();
                tempInstance.instance.dropOrGiveAllItems(
                        itemSet, tempInstance.viewer.getLocation(), tempInstance.viewer.getInventory()
                );
            }
        }
    }

    public void close(Player player) {
        Iterator<TemporaryContainerInstance> tempIterator = tempContainers.iterator();
        ItemSetWrapper itemSet = CustomItemsPlugin.getInstance().getSet();
        while (tempIterator.hasNext()) {
            TemporaryContainerInstance tempInstance = tempIterator.next();
            if (tempInstance.viewer.getUniqueId().equals(player.getUniqueId())) {
                tempIterator.remove();
                tempInstance.instance.dropOrGiveAllItems(
                        itemSet, tempInstance.viewer.getLocation(), tempInstance.viewer.getInventory()
                );
            }
        }
    }

    public void closeAll() {
        ItemSetWrapper itemSet = CustomItemsPlugin.getInstance().getSet();
        tempContainers.forEach(entry -> {
            entry.instance.dropOrGiveAllItems(itemSet, entry.viewer.getLocation(), entry.viewer.getInventory());
            new ArrayList<>(entry.instance.getInventory().getViewers()).forEach(HumanEntity::closeInventory);
        });
        tempContainers.clear();
    }
}
