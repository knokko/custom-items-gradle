package nl.knokko.customrecipes.crafting;

import java.util.Arrays;

class ShapelessPlacement {

    final int[] permutation;

    ShapelessPlacement(int[] permutation) {
        this.permutation = permutation;
    }

    @Override
    public String toString() {
        return "ShapelessPlacement(" + Arrays.toString(permutation) + ")";
    }
}
