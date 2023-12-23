package nl.knokko.customitems.plugin.data.container;

import nl.knokko.customitems.plugin.container.ContainerInstance;
import org.bukkit.entity.Player;

public class TemporaryContainerInstance {

    public final ContainerInstance instance;
    public final Player viewer;

    public TemporaryContainerInstance(ContainerInstance instance, Player viewer) {
        this.instance = instance;
        this.viewer = viewer;
    }
}
