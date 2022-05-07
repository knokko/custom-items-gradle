package nl.knokko.customitems.item.model;

public enum DefaultModelType {
    // TODO Handle 3d helmets
    // TODO Handle block items, bows, and crossbows
    BASIC("item/handheld", "item/generated"),
    SHIELD("item/handheld"),
    SHIELD_BLOCKING("item/handheld"),
    TRIDENT("item/generated"),
    TRIDENT_IN_HAND("item/handheld"),
    TRIDENT_THROWING("item/handheld");

    private String[] recommendedParents;

    DefaultModelType(String... recommendedParents) {
        this.recommendedParents = recommendedParents;
    }
}
