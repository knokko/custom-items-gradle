package nl.knokko.customrecipes.smithing;

import org.bukkit.Material;

import java.util.Arrays;

class WeakSmithingRecipe {

    final Material[] materials;

    WeakSmithingRecipe(Material... materials) {
        if (materials.length != 3) throw new IllegalArgumentException("Length of materials must be 3");
        this.materials = materials;
    }

    @Override
    public String toString() {
        return "WeakSmithing(" + materials[0] + "," + materials[1] + "," + materials[2] + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof WeakSmithingRecipe) return Arrays.equals(materials, ((WeakSmithingRecipe) other).materials);
        else return false;
    }

    @Override
    public int hashCode() {
        return materials[0].hashCode() + 13 * materials[1].hashCode() + 37 * materials[2].hashCode();
    }
}
