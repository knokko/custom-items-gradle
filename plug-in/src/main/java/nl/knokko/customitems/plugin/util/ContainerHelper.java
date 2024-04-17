package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.block.KciBlock;
import nl.knokko.customitems.container.ContainerHost;
import nl.knokko.customitems.container.VContainerType;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import org.bukkit.block.Block;

public class ContainerHelper {

    public static boolean shouldHostAcceptBlock(String containerName, ContainerHost host, Block block) {
        if (host.getVanillaType() != null) {
            VMaterial blockMaterial = VMaterial.valueOf(
                    KciNms.instance.items.getMaterialName(block)
            );
            VContainerType vanillaType = VContainerType.fromMaterial(blockMaterial);
            return host.getVanillaType() == vanillaType;
        } else if (host.getVanillaMaterial() != null) {
            VMaterial blockMaterial = VMaterial.valueOf(
                    KciNms.instance.items.getMaterialName(block)
            );
            return host.getVanillaMaterial() == blockMaterial;
        } else if (host.getCustomBlockReference() != null) {
            KciBlock customBlock = MushroomBlockHelper.getMushroomBlock(block);
            return customBlock != null && customBlock.getInternalID() == host.getCustomBlockReference().get().getInternalID();
        } else {
            throw new IllegalStateException("Custom container " + containerName + " has an invalid host");
        }
    }
}
