package nl.knokko.customitems.plugin.container;

import nl.knokko.customitems.container.ContainerHost;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.drops.VEntityType;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.item.KciPocketContainer;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.data.PlayerData;
import nl.knokko.customitems.plugin.data.container.PassiveLocation;
import nl.knokko.customitems.plugin.multisupport.worldguard.WorldGuardSupport;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ContainerSelectionManager {

    static boolean hasPermission(Player player, KciContainer container) {
        return !container.requiresPermission() || player.hasPermission("customitems.container.openany") ||
                player.hasPermission("customitems.container.open." + container.getName());
    }

    private final ItemSetWrapper itemSet;
    private final ContainerManager manager;
    private final Map<UUID, PlayerData> playerData;

    private final Collection<Entry> entries = new ArrayList<>();

    public ContainerSelectionManager(
            ItemSetWrapper itemSet, ContainerManager manager,
            Map<UUID, PlayerData> playerData
    ) {
        this.itemSet = itemSet;
        this.manager = manager;
        this.playerData = playerData;
    }

    private List<KciContainer> getContainersToChooseFrom(
            Player player, Collection<KciContainer> candidates
    ) {
        return candidates.stream().filter(
                candidate -> hasPermission(player, candidate) && !candidate.isHidden()
        ).collect(Collectors.toList());
    }

    private Inventory createContainerSelectionMenu(
            Collection<KciContainer> containers) {
        int invSize = 1 + containers.size();
        if (invSize % 9 != 0) {
            invSize = 9 + 9 * (invSize / 9);
        }

        Inventory menu = Bukkit.createInventory(null, invSize, "Choose custom container");
        {
            ItemStack cancelStack = KciNms.instance.items.createStack(VMaterial.BARRIER.name(), 1);
            ItemMeta meta = cancelStack.getItemMeta();
            assert meta != null;
            meta.setDisplayName("Cancel");
            cancelStack.setItemMeta(meta);
            menu.setItem(0, cancelStack);
        }

        int listIndex = 0;
        for (KciContainer container : containers) {
            menu.setItem(listIndex + 1, ContainerInstance.fromDisplay(container.getSelectionIcon()));
            listIndex++;
        }

        return menu;
    }

    public List<KciContainer> getShown(HumanEntity player) {
        for (Entry entry : entries) {
            if (entry.inventory.getViewers().contains(player)) return entry.containers;
        }
        return null;
    }

    public Inventory getBlockContainerMenu(Location location, Player player, ContainerHost host) {
        if (!WorldGuardSupport.canInteract(location.getBlock(), player)) {
            return null;
        }

        List<KciContainer> permittedContainers = getContainersToChooseFrom(player, itemSet.getContainers(host));

        if (permittedContainers.isEmpty()) {
            return null;
        } else if (permittedContainers.size() == 1) {
            return manager.getCustomContainer(
                    location, null, player,
                    permittedContainers.iterator().next()
            ).getInventory();
        } else {
            PlayerData pd = PlayerData.get(player, playerData);
            pd.containerSelectionLocation = new PassiveLocation(location);

            Inventory selectionMenu = createContainerSelectionMenu(permittedContainers);
            entries.add(new Entry(selectionMenu, permittedContainers));
            return selectionMenu;
        }
    }

    public void openPocketContainerMenu(Player player, KciPocketContainer pocketContainer) {
        List<KciContainer> permittedContainers = getContainersToChooseFrom(
                player, pocketContainer.getContainers()
        );
        PlayerData pd = PlayerData.get(player, playerData);
        pd.pocketContainerSelection = !permittedContainers.isEmpty();

        if (permittedContainers.isEmpty()) {
            player.sendMessage(ChatColor.DARK_RED + "You don't have permission to open any container of this item");
        } else if (permittedContainers.size() == 1) {
            manager.selectCustomContainer(player, permittedContainers.iterator().next());
        } else {
            Inventory selectionMenu = createContainerSelectionMenu(permittedContainers);
            player.openInventory(selectionMenu);
            entries.add(new Entry(selectionMenu, permittedContainers));
        }
    }

    public void openEntityContainerMenu(Player player, Entity entity) {
        VEntityType entityType;
        try {
            entityType = VEntityType.valueOf(entity.getType().name());
        } catch (IllegalArgumentException notSupported) {
            return;
        }

        Collection<KciContainer> candidates = itemSet.getContainers(new ContainerHost(entityType));
        List<KciContainer> permittedContainers = getContainersToChooseFrom(player, candidates);

        PlayerData pd = PlayerData.get(player, playerData);

        if (permittedContainers.size() == 1) {
            manager.selectCustomContainer(player, permittedContainers.iterator().next());
        }
        if (permittedContainers.size() > 1) {
            pd.containerSelectionEntity = entity;
            Inventory selectionMenu = createContainerSelectionMenu(permittedContainers);
            player.openInventory(selectionMenu);
            entries.add(new Entry(selectionMenu, permittedContainers));
        }
    }

    public void close(Player player) {
        PlayerData pd = PlayerData.get(player, playerData);
        pd.containerSelectionLocation = null;
        pd.containerSelectionEntity = null;
        pd.pocketContainerSelection = false;
    }

    public void clean() {
        entries.removeIf(candidate -> candidate.inventory.getViewers().isEmpty());
    }

    private static class Entry {

        final Inventory inventory;
        final List<KciContainer> containers;

        Entry(Inventory inventory, List<KciContainer> containers) {
            this.inventory = inventory;
            this.containers = containers;
        }
    }
}
