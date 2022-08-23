package nl.knokko.customitems.container.energy;

import nl.knokko.customitems.NameHelper;

public enum RecipeEnergyOperation {
    REQUIRE_AT_LEAST,
    REQUIRE_AT_MOST,
    DECREASE,
    INCREASE;

    RecipeEnergyOperation() {}

    @Override
    public String toString() {
        return NameHelper.getNiceEnumName(this.name());
    }
}
