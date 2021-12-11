package nl.knokko.customitems.editor.resourcepack;

import nl.knokko.customitems.itemset.SItemSet;

class ResourcepackOverrider {

    private final SItemSet itemSet;
    private final int mcVersion;

    ResourcepackOverrider(SItemSet itemSet, int mcVersion) {
        this.itemSet = itemSet;
        this.mcVersion = mcVersion;
    }
}
