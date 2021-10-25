package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ColoredRedstoneValues extends ProjectileEffectValues {

    static ColoredRedstoneValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        ColoredRedstoneValues result = new ColoredRedstoneValues(false);

        if (encoding == ENCODING_COLORED_REDSTONE_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("ColoredRedstoneProjectileEffect", encoding);
        }

        return result;
    }

    private int minRed, minGreen, minBlue;
    private int maxRed, maxGreen, maxBlue;

    private float minRadius, maxRadius;
    private int amount;

    public ColoredRedstoneValues(boolean mutable) {
        super(mutable);
        this.minRed = 200;
        this.minGreen = 0;
        this.minBlue = 0;
        this.maxRed = 255;
        this.maxGreen = 30;
        this.maxBlue = 50;
        this.minRadius = 0.05f;
        this.maxRadius = 0.15f;
        this.amount = 10;
    }

    public ColoredRedstoneValues(ColoredRedstoneValues toCopy, boolean mutable) {
        super(mutable);
        this.minRed = toCopy.getMinRed();
        this.minGreen = toCopy.getMinGreen();
        this.minBlue = toCopy.getMinBlue();
        this.maxRed = toCopy.getMaxRed();
        this.maxGreen = toCopy.getMaxGreen();
        this.maxBlue = toCopy.getMaxBlue();
        this.minRadius = toCopy.getMinRadius();
        this.maxRadius = toCopy.getMaxRadius();
        this.amount = toCopy.getAmount();
    }

    @Override
    public String toString() {
        return "ColoredRedstone(" + minRed + "," + minGreen + "," + minBlue + ")";
    }

    private void load1(BitInput input) {
        this.minRed = input.readByte();
        this.minGreen = input.readByte();
        this.minBlue = input.readByte();
        this.maxRed = input.readByte();
        this.maxGreen = input.readByte();
        this.maxBlue = input.readByte();
        this.minRadius = input.readFloat();
        this.maxRadius = input.readFloat();
        this.amount = input.readInt();
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_COLORED_REDSTONE_1);
        output.addBytes((byte) minRed, (byte) minGreen, (byte) minBlue, (byte) maxRed, (byte) maxGreen, (byte) maxBlue);
        output.addFloats(minRadius, maxRadius);
        output.addInt(amount);
    }

    @Override
    public ColoredRedstoneValues copy(boolean mutable) {
        return new ColoredRedstoneValues(this, mutable);
    }

    public int getMinRed() {
        return minRed;
    }

    public int getMinGreen() {
        return minGreen;
    }

    public int getMinBlue() {
        return minBlue;
    }

    public int getMaxRed() {
        return maxRed;
    }

    public int getMaxGreen() {
        return maxGreen;
    }

    public int getMaxBlue() {
        return maxBlue;
    }

    public float getMinRadius() {
        return minRadius;
    }

    public float getMaxRadius() {
        return maxRadius;
    }

    public int getAmount() {
        return amount;
    }

    public void setMinRed(int newMinRed) {
        assertMutable();
        this.minRed = newMinRed;
    }

    public void setMinGreen(int newMinGreen) {
        assertMutable();
        this.minGreen = newMinGreen;
    }

    public void setMinBlue(int newMinBlue) {
        assertMutable();
        this.minBlue = newMinBlue;
    }

    public void setMaxRed(int newMaxRed) {
        assertMutable();
        this.maxRed = newMaxRed;
    }

    public void setMaxGreen(int newMaxGreen) {
        assertMutable();
        this.maxGreen = newMaxGreen;
    }

    public void setMaxBlue(int newMaxBlue) {
        assertMutable();
        this.maxBlue = newMaxBlue;
    }

    public void setMinRadius(float newMinRadius) {
        assertMutable();
        this.minRadius = newMinRadius;
    }

    public void setMaxRadius(float newMaxRadius) {
        assertMutable();
        this.maxRadius = newMaxRadius;
    }

    public void setAmount(int newAmount) {
        assertMutable();
        this.amount = newAmount;
    }

    private void checkColorValue(String name, int value) throws ValidationException {
        if (value < 0) throw new ValidationException(name + " can't be negative");
        if (value > 255) throw new ValidationException(name + " can be at most 255");
    }

    @Override
    public void validate(SItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        checkColorValue("Min red", minRed);
        checkColorValue("Min green", minGreen);
        checkColorValue("Min blue", minBlue);
        checkColorValue("Max red", maxRed);
        checkColorValue("Max green", maxGreen);
        checkColorValue("Max blue", maxBlue);
        if (minRed > maxRed) throw new ValidationException("Min red can't be larger than Max red");
        if (minGreen > maxGreen) throw new ValidationException("Min green can't be larger than Max green");
        if (minBlue > maxBlue) throw new ValidationException("Min blue can't be larger than Max blue");
        if (minRadius < 0f) throw new ValidationException("Min radius can't be negative");
        if (maxRadius < 0f) throw new ValidationException("Max radius can't be negative");
        if (minRadius > maxRadius) throw new ValidationException("Min radius can't be larger than Max radius");
        if (amount <= 0) throw new ValidationException("Amount must be positive");
    }
}
