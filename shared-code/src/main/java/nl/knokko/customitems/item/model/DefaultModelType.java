package nl.knokko.customitems.item.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum DefaultModelType {
    BASIC("item/handheld", "item/generated"),
    SHIELD("item/handheld"),
    SHIELD_BLOCKING("item/handheld"),
    TRIDENT("item/generated"),
    TRIDENT_IN_HAND("item/handheld"),
    TRIDENT_THROWING("item/handheld");

    public final List<String> recommendedParents;

    DefaultModelType(String... recommendedParents) {
        List<String> recommendedParentsList = new ArrayList<>(recommendedParents.length);
        Collections.addAll(recommendedParentsList, recommendedParents);
        this.recommendedParents = Collections.unmodifiableList(recommendedParentsList);
    }
}
