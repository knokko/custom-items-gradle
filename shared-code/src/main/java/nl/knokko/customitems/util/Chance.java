package nl.knokko.customitems.util;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Random;

public class Chance {

    private static final int ONE_PERCENT = 1_000_000;
    public static final int NUM_BACK_DIGITS = 6;

    public static Chance percentage(int percentage) {
        if (percentage < 0) throw new IllegalArgumentException("Percentage (" + percentage + ") can't be negative");
        if (percentage > 100) throw new IllegalArgumentException("Percentage (" + percentage + ") can be at most 100");
        return new Chance(percentage * ONE_PERCENT);
    }

    public static Chance load(BitInput input) {
        return new Chance(input.readInt());
    }

    private final int rawValue;

    public Chance(int rawValue) {
        if (rawValue < 0) throw new IllegalArgumentException("Raw value (" + rawValue + ") can't be negative");
        if (rawValue > 100 * ONE_PERCENT) throw new IllegalArgumentException("Raw value (" + rawValue + ") can be at most " + 100 * ONE_PERCENT);
        this.rawValue = rawValue;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Chance && this.rawValue == ((Chance) other).rawValue;
    }

    public int getRawValue() {
        return rawValue;
    }

    public void save(BitOutput output) {
        output.addInt(this.rawValue);
    }

    public boolean apply(Random random) {
        return this.rawValue > random.nextInt(100 * ONE_PERCENT);
    }
}
