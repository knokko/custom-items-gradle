package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.itemset.ItemSet;

class ResourcepackOverrider {

    private final ItemSet itemSet;
    private final int mcVersion;

    ResourcepackOverrider(ItemSet itemSet, int mcVersion) {
        this.itemSet = itemSet;
        this.mcVersion = mcVersion;
    }
}
