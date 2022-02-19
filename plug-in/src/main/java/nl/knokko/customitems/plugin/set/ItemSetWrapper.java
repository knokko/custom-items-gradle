package nl.knokko.customitems.plugin.set;

import nl.knokko.customitems.container.CustomContainerHost;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomTridentValues;
import nl.knokko.customitems.itemset.BlockDropsView;
import nl.knokko.customitems.itemset.MobDropsView;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.plugin.container.ContainerInfo;
import nl.knokko.customitems.plugin.set.item.CustomItemNBT;
import nl.knokko.customitems.plugin.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemSetWrapper {

    private ItemSet currentItemSet;

    private Map<String, CustomItemValues> itemMap;
    private boolean hasCustomTridents;
    private Map<CIEntityType, Collection<MobDrop>> mobDropMap;
    private Map<BlockType, Collection<BlockDrop>> blockDropMap;
    private Map<String, ContainerInfo> containerInfoMap;
    private Map<CustomContainerHost, List<CustomContainerValues>> containerHostMap;

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
        this.itemMap = new HashMap<>(this.currentItemSet.getItems().size());
        for (CustomItemValues item : this.currentItemSet.getItems()) {
            this.itemMap.put(item.getName(), item);
            if (item instanceof CustomTridentValues) {
                this.hasCustomTridents = true;
            }
        }
    }

    private void initMobDropMap() {
        this.mobDropMap = new EnumMap<>(CIEntityType.class);
        for (MobDropValues mobDrop : this.currentItemSet.getMobDrops()) {

            if (!this.mobDropMap.containsKey(mobDrop.getEntityType())) {
                this.mobDropMap.put(mobDrop.getEntityType(), new ArrayList<>());
            }

            this.mobDropMap.get(mobDrop.getEntityType()).add(new MobDrop(mobDrop));
        }
    }

    private void initBlockDropMap() {
        this.blockDropMap = new EnumMap<>(BlockType.class);
        for (BlockDropValues blockDrop : this.currentItemSet.getBlockDrops()) {

            if (!this.blockDropMap.containsKey(blockDrop.getBlockType())) {
                this.blockDropMap.put(blockDrop.getBlockType(), new ArrayList<>());
            }

            this.blockDropMap.get(blockDrop.getBlockType()).add(new BlockDrop(blockDrop));
        }
    }

    private void initContainerInfoMap() {
        this.containerInfoMap = new HashMap<>(this.currentItemSet.getContainers().size());
        for (CustomContainerValues container : this.currentItemSet.getContainers()) {
            this.containerInfoMap.put(container.getName(), new ContainerInfo(container));
        }
    }

    private void initContainerTypeMap() {
        this.containerHostMap = new HashMap<>();
        for (CustomContainerValues container : this.currentItemSet.getContainers()) {
            List<CustomContainerValues> hostContainers = this.containerHostMap.computeIfAbsent(
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

    public CustomItemValues getItem(String name) {
        return this.itemMap.get(name);
    }

    public CustomItemValues getItem(ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack)) return null;

        String[] pItemName = {null};
        CustomItemNBT.readOnly(itemStack, nbt -> {
            if (nbt.hasOurNBT()) {
                pItemName[0] = nbt.getName();
            }
        });

        String itemName = pItemName[0];
        if (itemName == null) return null;

        return this.itemMap.get(itemName);
    }

    public MobDropsView getMobDrops(CIEntityType entityType) {
        Collection<MobDrop> rawCollection = this.mobDropMap.get(entityType);
        return new MobDropsView(rawCollection == null ? Collections.emptyList() : rawCollection);
    }

    public Collection<MobDropValues> getMobDrops(Entity entity) {

        CIEntityType entityType;
        if (entity instanceof Player) {
            Player player = (Player) entity;

            // The first check attempts to prevent the need for the possibly expensive second check
            if (player.hasMetadata("NPC") || !Bukkit.getOnlinePlayers().contains(player)) {
                entityType = CIEntityType.NPC;
            } else {
                entityType = CIEntityType.PLAYER;
            }
        } else {
            entityType = CIEntityType.fromBukkitEntityType(entity.getType());
        }

        if (entityType == null) return Collections.emptyList();
        MobDropsView potentialDrops = this.getMobDrops(entityType);

        int numDrops = 0;
        for (MobDropValues mobDrop : potentialDrops) {
            if (mobDrop.getRequiredName() == null || mobDrop.getRequiredName().equals(entity.getName())) {
                numDrops++;
            }
        }
        if (numDrops == 0) return Collections.emptyList();

        Collection<MobDropValues> result = new ArrayList<>(numDrops);
        for (MobDropValues mobDrop : potentialDrops) {
            if (mobDrop.getRequiredName() == null || mobDrop.getRequiredName().equals(entity.getName())) {
                result.add(mobDrop);
            }
        }

        return result;
    }

    public BlockDropsView getBlockDrops(BlockType blockType) {
        Collection<BlockDrop> rawCollection = this.blockDropMap.get(blockType);
        return new BlockDropsView(rawCollection == null ? Collections.emptyList() : rawCollection);
    }

    public BlockDropsView getBlockDrops(CIMaterial material) {
        if (material == null) return new BlockDropsView(Collections.emptyList());

        BlockType blockType = BlockType.fromBukkitMaterial(material);
        if (blockType == null) return new BlockDropsView(Collections.emptyList());
        return this.getBlockDrops(blockType);
    }

    public ContainerInfo getContainerInfo(String containerName) {
        return this.containerInfoMap.get(containerName);
    }

    public ContainerInfo getContainerInfo(CustomContainerValues container) {
        return this.getContainerInfo(container.getName());
    }

    public List<CustomContainerValues> getContainers(CustomContainerHost host) {
        List<CustomContainerValues> maybeContainerList = this.containerHostMap.get(host);
        return maybeContainerList != null ? Collections.unmodifiableList(maybeContainerList) : Collections.emptyList();
    }

    public Map<CustomContainerHost, List<CustomContainerValues>> getContainerHostMap() {
        return Collections.unmodifiableMap(this.containerHostMap);
    }
}
