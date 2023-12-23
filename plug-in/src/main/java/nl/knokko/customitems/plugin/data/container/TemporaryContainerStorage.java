package nl.knokko.customitems.plugin.data.container;

import nl.knokko.customitems.plugin.container.ContainerInstance;
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
        while (tempIterator.hasNext()) {
            TemporaryContainerInstance tempInstance = tempIterator.next();
            if (!tempInstance.viewer.getOpenInventory().getTopInventory().equals(tempInstance.instance.getInventory())) {
                tempIterator.remove();
                tempInstance.instance.dropAllItems(tempInstance.viewer.getLocation());
            }
        }
    }

    public void close(Player player) {
        Iterator<TemporaryContainerInstance> tempIterator = tempContainers.iterator();
        while (tempIterator.hasNext()) {
            TemporaryContainerInstance tempInstance = tempIterator.next();
            if (tempInstance.viewer.getUniqueId().equals(player.getUniqueId())) {
                tempIterator.remove();
                tempInstance.instance.dropAllItems(tempInstance.viewer.getLocation());
            }
        }
    }

    public void closeAll() {
        tempContainers.forEach(entry -> {
            entry.instance.dropAllItems(entry.viewer.getLocation());
            new ArrayList<>(entry.instance.getInventory().getViewers()).forEach(HumanEntity::closeInventory);
        });
        tempContainers.clear();
    }
}
