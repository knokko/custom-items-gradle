package nl.knokko.customitems.plugin.set;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import nl.knokko.customitems.container.ContainerHost;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciTrident;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.plugin.container.ContainerInfo;
import nl.knokko.customitems.plugin.set.item.CustomItemWrapper;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemSetWrapper {

    private ItemSet currentItemSet;

    private Map<String, KciItem> itemMap;
    private boolean hasCustomTridents;
    private Map<VEntityType, Collection<MobDrop>> mobDropMap;
    private Map<VBlockType, Collection<BlockDrop>> blockDropMap;
    private Map<String, ContainerInfo> containerInfoMap;
    private Map<ContainerHost, List<KciContainer>> containerHostMap;

    public void setItemSet(ItemSet newItemSet) {
        this.currentItemSet = newItemSet;

        this.initItemMap();
        this.initMobDropMap();
        this.initBlockDropMap();
        this.initContainerInfoMap();
        this.initContainerTypeMap();
    }

    private void initItemMap() {
        this.hasCustomTridents = false;
        this.itemMap = new HashMap<>(this.currentItemSet.items.size());
        for (KciItem item : this.currentItemSet.items) {
            this.itemMap.put(item.getName(), item);
            if (item instanceof KciTrident) {
                this.hasCustomTridents = true;
            }
        }
    }

    private void initMobDropMap() {
        this.mobDropMap = new EnumMap<>(VEntityType.class);
        for (MobDrop mobDrop : this.currentItemSet.mobDrops) {

            if (!this.mobDropMap.containsKey(mobDrop.getEntityType())) {
                this.mobDropMap.put(mobDrop.getEntityType(), new ArrayList<>());
            }

            this.mobDropMap.get(mobDrop.getEntityType()).add(mobDrop);
        }
    }

    private void initBlockDropMap() {
        this.blockDropMap = new EnumMap<>(VBlockType.class);
        for (BlockDrop blockDrop : this.currentItemSet.blockDrops) {

            if (!this.blockDropMap.containsKey(blockDrop.getBlockType())) {
                this.blockDropMap.put(blockDrop.getBlockType(), new ArrayList<>());
            }

            this.blockDropMap.get(blockDrop.getBlockType()).add(blockDrop);
        }
    }

    private void initContainerInfoMap() {
        this.containerInfoMap = new HashMap<>(this.currentItemSet.containers.size());
        for (KciContainer container : this.currentItemSet.containers) {
            this.containerInfoMap.put(container.getName(), new ContainerInfo(container));
        }
    }

    private void initContainerTypeMap() {
        this.containerHostMap = new HashMap<>();
        for (KciContainer container : this.currentItemSet.containers) {
            List<KciContainer> hostContainers = this.containerHostMap.computeIfAbsent(
                    container.getHost(), k -> new ArrayList<>()
            );
            hostContainers.add(container);
        }
    }

    public ItemSet get() {
        return currentItemSet;
    }

    public boolean hasCustomTridents() {
        return this.hasCustomTridents;
    }

    public KciItem getItem(String name) {
        return this.itemMap.get(name);
    }

    public KciItem getItem(ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack)) return null;

        String itemName = NBT.get(itemStack, nbt -> {
            ReadableNBT customNbt = nbt.getCompound(CustomItemWrapper.NBT_KEY);
            if (customNbt == null) return null;
            return customNbt.getString("Name");
        });
        if (itemName == null) return null;

        return this.itemMap.get(itemName);
    }

    public ItemReference getItemReference(ItemStack itemStack) {
        KciItem itemValues = this.getItem(itemStack);
        if (itemValues == null) return null;
        return this.get().items.getReference(itemValues.getName());
    }

    public Iterable<MobDrop> getMobDrops(VEntityType entityType) {
        Collection<MobDrop> rawCollection = this.mobDropMap.get(entityType);
        return rawCollection == null ? Collections.emptyList() : rawCollection;
    }

    public Iterable<MobDrop> getMobDrops(Entity entity) {

        VEntityType entityType;
        if (entity instanceof Player) {
            Player player = (Player) entity;

            // The first check attempts to prevent the need for the possibly expensive second check
            if (player.hasMetadata("NPC") || !Bukkit.getOnlinePlayers().contains(player)) {
                entityType = VEntityType.NPC;
            } else {
                entityType = VEntityType.PLAYER;
            }
        } else {
            entityType = VEntityType.fromBukkitEntityType(entity.getType());
        }

        if (entityType == null) return Collections.emptyList();
        Iterable<MobDrop> potentialDrops = this.getMobDrops(entityType);

        int numDrops = 0;
        for (MobDrop mobDrop : potentialDrops) {
            if (mobDrop.getRequiredName() == null || mobDrop.getRequiredName().equals(entity.getName())) {
                numDrops++;
            }
        }
        if (numDrops == 0) return Collections.emptyList();

        Collection<MobDrop> result = new ArrayList<>(numDrops);
        for (MobDrop mobDrop : potentialDrops) {
            if (mobDrop.getRequiredName() == null || mobDrop.getRequiredName().equals(entity.getName())) {
                result.add(mobDrop);
            }
        }

        return result;
    }

    public Iterable<BlockDrop> getBlockDrops(VBlockType blockType) {
        Collection<BlockDrop> rawCollection = this.blockDropMap.get(blockType);
        return rawCollection == null ? Collections.emptyList() : rawCollection;
    }

    public Iterable<BlockDrop> getBlockDrops(VMaterial material) {
        if (material == null) return Collections.emptyList();

        VBlockType blockType = VBlockType.fromBukkitMaterial(material);
        if (blockType == null) return Collections.emptyList();
        return this.getBlockDrops(blockType);
    }

    public ContainerInfo getContainerInfo(String containerName) {
        return this.containerInfoMap.get(containerName);
    }

    public ContainerInfo getContainerInfo(KciContainer container) {
        return this.getContainerInfo(container.getName());
    }

    public List<KciContainer> getContainers(ContainerHost host) {
        List<KciContainer> maybeContainerList = this.containerHostMap.get(host);
        return maybeContainerList != null ? Collections.unmodifiableList(maybeContainerList) : Collections.emptyList();
    }
}
