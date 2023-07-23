package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.container.CustomContainerHost;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.nms.KciNms;
import nl.knokko.customitems.plugin.set.block.MushroomBlockHelper;
import org.bukkit.block.Block;

public class ContainerHelper {

    public static boolean shouldHostAcceptBlock(String containerName, CustomContainerHost host, Block block) {
        if (host.getVanillaType() != null) {
            CIMaterial blockMaterial = CIMaterial.valueOf(
                    KciNms.instance.items.getMaterialName(block)
            );
            VanillaContainerType vanillaType = VanillaContainerType.fromMaterial(blockMaterial);
            return host.getVanillaType() == vanillaType;
        } else if (host.getVanillaMaterial() != null) {
            CIMaterial blockMaterial = CIMaterial.valueOf(
                    KciNms.instance.items.getMaterialName(block)
            );
            return host.getVanillaMaterial() == blockMaterial;
        } else if (host.getCustomBlockReference() != null) {
            CustomBlockValues customBlock = MushroomBlockHelper.getMushroomBlock(block);
            return customBlock != null && customBlock.getInternalID() == host.getCustomBlockReference().get().getInternalID();
        } else {
            throw new IllegalStateException("Custom container " + containerName + " has an invalid host");
        }
    }
}
