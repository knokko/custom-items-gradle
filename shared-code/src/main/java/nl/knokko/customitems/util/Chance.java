package nl.knokko.customitems.util;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Random;

public class Chance {

    public static final int ONE_PERCENT = 1_000_000;
    public static final int HUNDRED_PERCENT = 100 * ONE_PERCENT;
    public static final int NUM_BACK_DIGITS = 6;

    public static Chance percentage(int percentage) {
        if (percentage < 0) throw new IllegalArgumentException("Percentage (" + percentage + ") can't be negative");
        if (percentage > 100) throw new IllegalArgumentException("Percentage (" + percentage + ") can be at most 100");
        return new Chance(percentage * ONE_PERCENT);
    }

    public static Chance nonIntegerPercentage(double percentage) {
        if (percentage < 0) throw new IllegalArgumentException("Percentage (" + percentage + ") can't be negative");
        if (percentage > 100) throw new IllegalArgumentException("Percentage (" + percentage + ") can be at most 100");
        return new Chance((int) Math.round(percentage * ONE_PERCENT));
    }

    public static Chance subtract(Chance left, Chance right) {
        int rawResult = left.getRawValue() - right.getRawValue();
        if (rawResult < 0) return null;
        else return new Chance(rawResult);
    }

    public static Chance load(BitInput input) {
        return new Chance(input.readInt());
    }

    private final int rawValue;

    public Chance(int rawValue) {
        if (rawValue < 0) throw new IllegalArgumentException("Raw value (" + rawValue + ") can't be negative");
        if (rawValue > HUNDRED_PERCENT) throw new IllegalArgumentException("Raw value (" + rawValue + ") can be at most " + HUNDRED_PERCENT);
        this.rawValue = rawValue;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Chance && this.rawValue == ((Chance) other).rawValue;
    }

    @Override
    public String toString() {
        StringBuilder backPart = new StringBuilder(Integer.toString(this.rawValue % ONE_PERCENT));
        while (backPart.length() < NUM_BACK_DIGITS) {
            backPart.insert(0, "0");
        }
        while (backPart.length() > 0 && backPart.charAt(backPart.length() - 1) == '0') {
            backPart.deleteCharAt(backPart.length() - 1);
        }
        if (backPart.length() > 0) {
            backPart.insert(0, '.');
        }
        return (this.rawValue / ONE_PERCENT) + backPart.toString() + "%";
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
