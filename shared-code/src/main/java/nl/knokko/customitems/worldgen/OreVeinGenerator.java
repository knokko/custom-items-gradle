package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.model.Model;

public class OreVeinGenerator extends Model<OreVeinGeneratorValues> {
    public OreVeinGenerator(OreVeinGeneratorValues values) {
        super(values);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
