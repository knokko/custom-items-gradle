package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.model.Model;

public class TreeGenerator extends Model<TreeGeneratorValues> {
    public TreeGenerator(TreeGeneratorValues values) {
        super(values);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
